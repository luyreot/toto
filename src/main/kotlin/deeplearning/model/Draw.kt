package deeplearning.model

data class Draw(
    val year: Int,
    val id: Int,
    val numbers: IntArray
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Draw

        if (year != other.year) return false
        if (id != other.id) return false
        if (!numbers.contentEquals(other.numbers)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = year
        result = 31 * result + id
        result = 31 * result + numbers.contentHashCode()
        return result
    }
}