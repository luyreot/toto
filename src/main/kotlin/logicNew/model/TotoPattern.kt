package logicNew.model

/**
 * Represents different types of toto patterns:
 * - odd/even
 * - low/high
 * - color
 *
 * An odd/even pattern for a toto drawing will look like this:
 * 0 - odd, 1 - even
 * 5,14,22,25,34,49 -> 0,1,1,0,1,0
 *
 * An odd/even pattern for a toto drawing will look like this:
 * 0 - <= 25, 1 - > 25 (when the toto type is 6x49)
 * 5,14,22,25,34,49 -> 0,0,0,0,1,1
 *
 * The size of the array depends on the toto type - 6x49, 6x42 or 5x35.
 */
data class TotoPattern(
    val pattern: IntArray
) {

    override fun equals(
        other: Any?
    ): Boolean = when {
        this === other -> true

        javaClass != other?.javaClass -> false

        else -> pattern.contentEquals((other as? TotoPattern)?.pattern)
    }

    override fun hashCode(): Int = pattern.contentHashCode()
}
