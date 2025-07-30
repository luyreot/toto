package systems.occurrence

import util.totoType

fun doAlgo() {
    val historicalLottoData = HistoricalLottoData(totoType, 2016)
    val numberEligibility = NumberEligibility(
        totoType,
        historicalLottoData.longPeriodTendency,
        historicalLottoData.mediumPeriodTendency,
        historicalLottoData.shortPeriodTendency,
        historicalLottoData.longPeriodOccurrenceProgress,
        historicalLottoData.mediumPeriodOccurrenceProgress,
        historicalLottoData.shortPeriodOccurrenceProgress,
    )

    val eligible = numberEligibility.numberEligibility.filter { it.value }.keys
    val notEligible = numberEligibility.numberEligibility.filter { !it.value }.keys
    println()
}