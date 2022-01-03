package logicNew.model

/**
 * Hold information about:
 * - a lotto number [frequency] value, ie. the spacing between issues when a particular number has occurred;
 * - the [count] of how often that [frequency] has occurred.
 */
data class LottoNumberFrequency(
    val frequency: Int,
    val count: Int = 1
)