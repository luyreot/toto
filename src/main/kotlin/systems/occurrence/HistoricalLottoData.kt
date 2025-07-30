package systems.occurrence

import model.TotoType
import util.loadDrawings

class HistoricalLottoData(
    private val totoType: TotoType,
    private val yearStart: Int
) {

    val longPeriodTendency = mutableMapOf<Int, NumberTendency>()
    val mediumPeriodTendency = mutableMapOf<Int, NumberTendency>()
    val shortPeriodTendency = mutableMapOf<Int, NumberTendency>()

    val longPeriodOccurrenceProgress = mutableMapOf<Int, Int>()
    val mediumPeriodOccurrenceProgress = mutableMapOf<Int, Int>()
    val shortPeriodOccurrenceProgress = mutableMapOf<Int, Int>()

    init {
        calculateTendencies()
    }

    private fun calculateTendencies() {
        val allDraws = loadDrawings(totoType).filter { it.year >= yearStart }
        val yearLast = allDraws.last().year

        val historicalNumberOccurrence = HistoricalNumberOccurrencePerPeriod(
            totoType, allDraws.filter { it.year < yearLast }
        )

        historicalNumberOccurrence.longPeriodOccurrences.forEach { (number, occurrences) ->
            longPeriodTendency[number] = NumberTendency(
                mean = occurrences.mean(),
                median = occurrences.median()
            )
        }
        historicalNumberOccurrence.mediumPeriodOccurrences.forEach { (number, occurrences) ->
            mediumPeriodTendency[number] = NumberTendency(
                mean = occurrences.mean(),
                median = occurrences.median()
            )
        }
        historicalNumberOccurrence.shortPeriodOccurrences.forEach { (number, occurrences) ->
            shortPeriodTendency[number] = NumberTendency(
                mean = occurrences.mean(),
                median = occurrences.median()
            )
        }

        val currentYearNumberOccurrence = HistoricalNumberOccurrencePerPeriod(
            totoType, allDraws.filter { it.year == yearLast }
        )
        longPeriodOccurrenceProgress.putAll(
            currentYearNumberOccurrence.longPeriodOccurrences.mapValues { (_, occurrences) -> occurrences.last() }
        )
        mediumPeriodOccurrenceProgress.putAll(
            currentYearNumberOccurrence.mediumPeriodOccurrences.mapValues { (_, occurrences) -> occurrences.last() }
        )
        shortPeriodOccurrenceProgress.putAll(
            currentYearNumberOccurrence.shortPeriodOccurrences.mapValues { (_, occurrences) -> occurrences.last() }
        )
    }
}