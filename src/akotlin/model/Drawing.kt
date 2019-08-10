package akotlin.model

data class Drawing(
        val year: String,
        val issue: Int,
        val numbers: IntArray
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Drawing

        if (year != other.year) return false
        if (issue != other.issue) return false
        if (!numbers.contentEquals(other.numbers)) return false
        return true
    }

    override fun hashCode(): Int {
        var result = 1
        result = result xor year.hashCode()
        result = result xor issue
        result = result xor numbers.contentHashCode()
        return result
    }

}