package akotlin.model

data class Drawing(val year: String, val issue: Int, val numbers: IntArray) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Drawing

        return year == other.year && issue == other.issue && numbers.contentEquals(other.numbers)
//        return numbers.contentEquals(other.numbers)
    }

    override fun hashCode(): Int {
        return 1 xor year.hashCode() xor issue.hashCode() xor numbers.contentHashCode()
//        return 1 xor numbers.contentHashCode()
    }

}