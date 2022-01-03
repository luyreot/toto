package logicNew.model

/**
 * Represents different types of lotto patterns:
 * - odd/even
 * - low/high
 * - color
 *
 * An odd/even pattern for a lotto drawing will look like this:
 * 0 - odd, 1 - even
 * 5,14,22,25,34,49 -> 0,1,1,0,1,0
 *
 * An odd/even pattern for a lotto drawing will look like this:
 * 0 - <= 25, 1 - > 25 (when the lotto type is 6x49)
 * 5,14,22,25,34,49 -> 0,0,0,0,1,1
 *
 * The size of the array depends on the lotto type - 6x49, 6x42 or 5x35.
 */
data class LottoPattern(
    val pattern: IntArray
) {

    override fun equals(
        other: Any?
    ): Boolean = when {
        this === other -> true

        javaClass != other?.javaClass -> false

        else -> pattern.contentEquals((other as? LottoPattern)?.pattern)
    }

    override fun hashCode(): Int = pattern.contentHashCode()
}
