package model

data class UniqueIntArray(val numbers: IntArray) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UniqueIntArray

        return numbers.contentEquals(other.numbers)
    }

    override fun hashCode(): Int {
        return numbers.contentHashCode()
    }
}