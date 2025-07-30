package systems.occurrence

import model.TotoType
import util.Draw

class HistoricalNumberOccurrencePerPeriod(
    private val totoType: TotoType,
    private val draws: List<Draw>
) {

    val longPeriodOccurrences = (1..totoType.totalNumbers).associateWith { mutableListOf<Int>() }.toMutableMap()
    val mediumPeriodOccurrences = (1..totoType.totalNumbers).associateWith { mutableListOf<Int>() }.toMutableMap()
    val shortPeriodOccurrences = (1..totoType.totalNumbers).associateWith { mutableListOf<Int>() }.toMutableMap()

    init {
        calculateNumberOccurrences()
    }

    private fun calculateNumberOccurrences() {
        val longPeriodDrawSplit = splitDrawsIntoPeriod(DrawsPeriod.LONG)
        val mediumPeriodDrawSplit = splitDrawsIntoPeriod(DrawsPeriod.MEDIUM)
        val shortPeriodDrawSplit = splitDrawsIntoPeriod(DrawsPeriod.SHORT)

        (1..totoType.totalNumbers).forEach { number ->
            longPeriodDrawSplit.forEach { draws ->
                longPeriodOccurrences[number]?.add(draws.countOccurrences(number))
            }
            mediumPeriodDrawSplit.forEach { draws ->
                mediumPeriodOccurrences[number]?.add(draws.countOccurrences(number))
            }
            shortPeriodDrawSplit.forEach { draws ->
                shortPeriodOccurrences[number]?.add(draws.countOccurrences(number))
            }
        }
    }

    private fun splitDrawsIntoPeriod(period: DrawsPeriod): List<List<Draw>> {
        val windowSize: Int = when {
            totoType == TotoType.T_6X49 && period == DrawsPeriod.SHORT -> 26
            totoType == TotoType.T_6X42 && period == DrawsPeriod.SHORT -> 26
            totoType == TotoType.T_5X35 && period == DrawsPeriod.SHORT -> 26

            totoType == TotoType.T_6X49 && period == DrawsPeriod.MEDIUM -> 52
            totoType == TotoType.T_6X42 && period == DrawsPeriod.MEDIUM -> 52
            totoType == TotoType.T_5X35 && period == DrawsPeriod.MEDIUM -> 52

            totoType == TotoType.T_6X49 && period == DrawsPeriod.LONG -> 104
            totoType == TotoType.T_6X42 && period == DrawsPeriod.LONG -> 104
            totoType == TotoType.T_5X35 && period == DrawsPeriod.LONG -> 104

            else -> throw IllegalArgumentException("Toto type (${totoType.name}) and/or draw period (${period.name}) incorrectly selected.")
        }

        return draws.windowed(
            size = windowSize,
            step = windowSize,
            partialWindows = true
        )
    }
}