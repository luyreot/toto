package systems.gapanalysis

import util.Draw

class YearlyAverageOccurrences(
    val occurrences: MutableMap<Int, Int> = mutableMapOf()
) {

    fun setOccurrences(
        draws: List<Draw>,
        yearFilter: Int,
        currentYear: Int
    ) {
        if (occurrences.isNotEmpty()) occurrences.clear()

        val occurrencePerYear = mutableMapOf<Int, MutableMap<Int, Int>>()

        for (i in draws.indices) {
            if (draws[i].year < yearFilter || draws[i].year == currentYear) continue

            val year = draws[i].year
            val draw = draws[i].numbers
            for (number in draw) {
                if (occurrencePerYear.containsKey(number)) {
                    occurrencePerYear[number]?.merge(year, 1, Int::plus)
                } else {
                    occurrencePerYear[number] = mutableMapOf(Pair(year, 1))
                }
            }
        }

        occurrencePerYear.forEach { (number, yearOccurrences) ->
            occurrences[number] = yearOccurrences.values.sum() / yearOccurrences.values.size
        }
    }

    fun getYearlyOccurrences(
        draws: List<Draw>,
        year: Int
    ): Map<Int, Int> {
        val occurrences = mutableMapOf<Int, Int>()

        for (i in draws.indices) {
            if (draws[i].year != year) continue

            val draw = draws[i].numbers
            for (number in draw) {
                if (occurrences.containsKey(number)) {
                    occurrences.merge(number, 1, Int::plus)
                } else {
                    occurrences[number] = 1
                }
            }
        }

        return occurrences
    }
}