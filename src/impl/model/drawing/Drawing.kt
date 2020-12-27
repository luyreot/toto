package impl.model.drawing

/**
 * Holds information about a single drawing - year, number within that year, and the drawing numbers themselves.
 * Ie: 2020, #99, 5,14,22,25,34,49
 */
data class Drawing(val year: String, val issue: Int, val numbers: IntArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Drawing

        if (year != other.year) return false
        if (issue != other.issue) return false
        if (numbers.contentEquals(other.numbers).not()) return false

        return true
    }

    override fun hashCode(): Int {
        return year.hashCode() xor issue.hashCode() xor numbers.contentHashCode()
    }
}