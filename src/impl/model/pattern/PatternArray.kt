package impl.model.pattern

/**
 * Holds a specific set of numbers in an int array which always must have a size of 6.
 *
 * Basically, a int array pattern representation of a drawing (1, 4, 15, 33, 44, 45), ie:
 *
 * - PatternColor:    0 - 1..9, 1 - 10..19, 2 - 20..29, 3 - 30..39, 4 - 40..49
 * - PatternLowHigh:  0 - <= 25, 1 - > 25
 * - PatternOddEven:  0 - odd, 1 - even
 */
data class PatternArray(val numbers: IntArray, override var lfi: Int) : PatternNumeric() {

    init {
        if (numbers.size != 6) {
            throw IllegalArgumentException("Numbers array must always have size of 6! Current is ${numbers.size}")
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PatternArray

        if (!numbers.contentEquals(other.numbers)) return false

        return true
    }

    override fun hashCode(): Int {
        return numbers.contentHashCode()
    }

}