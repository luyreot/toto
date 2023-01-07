package model

/**
 * Represents different types of toto numbers:
 * - drawing
 * - odd/even
 * - low/high
 * - group
 * - group delta
 *
 * An odd/even pattern for a toto drawing will look like this:
 * 0 - odd, 1 - even
 * 5,14,22,25,34,49 -> 0,1,1,0,1,0
 *
 * An low/high pattern for a toto drawing will look like this:
 * 0 - <= 25, 1 - > 25 (when the toto type is 6x49)
 * 5,14,22,25,34,49 -> 0,0,0,0,1,1
 *
 * A group pattern for a toto drawing will look like this:
 * 0 - 1..9, 1 - 10..19, 2 - 20..29, 3 - 30..39, 4 - 40..49
 * 5,14,22,25,34,49 -> 0,1,2,2,3,4
 *
 * The size of the array depends on the toto type - 6x49, 6x42 or 5x35.
 */
data class NumbersFloat(
    val numbers: FloatArray
) : CompareDeltaPattern<NumbersFloat> {

    override fun equals(
        other: Any?
    ): Boolean = when {
        this === other -> true

        javaClass != other?.javaClass -> false

        else -> numbers.contentEquals((other as? NumbersFloat)?.numbers)
    }

    override fun hashCode(): Int = numbers.contentHashCode()

    override fun compareDeltaPatternTo(other: NumbersFloat): Int {
        for (i in 1 until numbers.size) {
            if (numbers[i] == other.numbers[i]) {
                if (i == numbers.size - 1) {
                    return 0
                } else {
                    continue
                }
            }

            if (numbers[i] < other.numbers[i]) return -1

            return 1
        }

        return 1
    }
}