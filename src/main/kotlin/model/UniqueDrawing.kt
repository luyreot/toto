package model

class UniqueDrawing(
    year: Int,
    issue: Int,
    numbers: IntArray,
    groupPattern: IntArray,
    lowHighPattern: IntArray,
    oddEvenPattern: IntArray
) : Drawing(
    year = year,
    issue = issue,
    numbers = numbers,
    groupPattern = groupPattern,
    lowHighPattern = lowHighPattern,
    oddEvenPattern = oddEvenPattern
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        if (javaClass != other?.javaClass) return false

        other as UniqueDrawing

        return numbers.contentEquals(other.numbers)
    }

    override fun hashCode(): Int = numbers.contentHashCode()
}