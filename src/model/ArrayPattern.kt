package model

/**
 * A int array pattern representation of a drawing (1, 4, 15, 33, 44, 45), where
 * 0 - 1..9, 1 - 10..19, 2 - 20..29, 3 - 30..39, 4 - 40..49 (Color Pattern),
 * 0 - <= 25, 1 - > 25 (Low / High Pattern),
 * 0 - odd, 1 - even (Odd / Even Pattern).
 */
data class ArrayPattern(val numbers: IntArray, override var lastFrequencyIndex: Int) : SubPattern() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ArrayPattern

        return numbers.contentEquals(other.numbers)
    }

    override fun hashCode(): Int = 1 xor numbers.contentHashCode()

}