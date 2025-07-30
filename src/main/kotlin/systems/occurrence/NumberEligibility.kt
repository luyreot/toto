package systems.occurrence

import model.TotoType

class NumberEligibility(
    private val totoType: TotoType,
    private val longPeriodTendency: Map<Int, NumberTendency>,
    private val mediumPeriodTendency: Map<Int, NumberTendency>,
    private val shortPeriodTendency: Map<Int, NumberTendency>,
    private val longPeriodOccurrenceProgress: Map<Int, Int>,
    private val mediumPeriodOccurrenceProgress: Map<Int, Int>,
    private val shortPeriodOccurrenceProgress: Map<Int, Int>
) {

    val numberEligibility = mutableMapOf<Int, Boolean>()

    init {
        calculateNumberEligibility()
    }

    private fun calculateNumberEligibility() {
        (1..totoType.totalNumbers).forEach { number ->
            numberEligibility[number] =
                shortPeriodOccurrenceProgress[number]!! < shortPeriodTendency[number]!!.median
                        && mediumPeriodOccurrenceProgress[number]!! < mediumPeriodTendency[number]!!.median
                        && longPeriodOccurrenceProgress[number]!! < longPeriodTendency[number]!!.mean
        }
    }
}