package systems.numbercorrelations.model

class UniquePattern(
    val array: IntArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        if (javaClass != other?.javaClass) return false

        other as UniquePattern

        return array.contentEquals(other.array)
    }

    override fun hashCode(): Int = array.contentHashCode()
}