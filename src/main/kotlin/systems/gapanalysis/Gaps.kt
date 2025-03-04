package systems.gapanalysis

import model.TotoType
import util.Constants.PAGE_YEAR
import util.Draw

class Gaps(
    /**
     * Consider numbers as 'hot' that have a gap that is less than the one here.
     */
    val lessThan: MutableMap<Int, Int> = mutableMapOf(),
    /**
     * Consider numbers as 'hot' that have a gap that is greater than the one here.
     */
    val greaterThan: MutableMap<Int, Int> = mutableMapOf()
) {

    fun setGaps(
        totoType: TotoType,
        draws: List<Draw>,
        yearFilter: Int
    ) {
        if (lessThan.isNotEmpty()) lessThan.clear()
        if (greaterThan.isNotEmpty()) greaterThan.clear()

        // Gaps per number per year
        val gaps = mutableMapOf<Int, MutableMap<Int, Int>>()
        for (i in draws.indices) {
            if (draws[i].year < yearFilter || draws[i].year == PAGE_YEAR.toInt()) continue

            for (number in draws[i].numbers) {
                for (j in i - 1 downTo 0) {
                    if (number in draws[j].numbers) {
                        val gap = i - j
                        if (gaps.containsKey(number)) {
                            gaps[number]?.merge(gap, 1, Int::plus)
                        } else {
                            gaps[number] = mutableMapOf(Pair(gap, 1))
                        }
                        break
                    }
                }
            }
        }

        // Sort by value first
        // Sort by key if values are equal
        gaps.keys.forEach { number ->
            gaps[number]?.toList()
                ?.sortedWith(
                    compareBy<Pair<Int, Int>> { it.second }.thenBy { it.first }
                )
                ?.toMap()?.toMutableMap()
                ?.let { gaps[number] = it }
        }

        // Less frequent gaps start at zero index
        // More frequent gaps are at the end of the map
        val gapsToTake = if (totoType == TotoType.T_5X35) 4 else 3
        gaps.forEach { (number, gaps) ->
            val gapsAsList = gaps.toList()

            lessThan[number] = gapsAsList.takeLast(gapsToTake).maxOf { it.first }
            greaterThan[number] = gapsAsList.first().first
        }
    }

    fun getCurrentGaps(
        totoType: TotoType,
        draws: List<Draw>,
    ): Map<Int, Int> {
        val gaps = mutableMapOf<Int, Int>()

        for (number in 1..totoType.totalNumbers) {
            var gap = 1
            for (i in draws.size - 1 downTo 0) {
                if (draws[i].year < PAGE_YEAR.toInt() - 1) break
                if (number in draws[i].numbers) break
                gap++
            }
            gaps[number] = gap
        }

        return gaps
    }
}