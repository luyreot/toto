package model

val groupStrategies = mapOf<GroupStrategy, Any>(
    GroupStrategy.DIVIDE_BY_10 to ::divideBy10,
    GroupStrategy.DELTA_SUBTRACT to Int::deltaSubtract,
    GroupStrategy.DELTA_ADD to Int::deltaAdd
)

/**
 * Enum for various methods for creating group patterns.
 * Each one converts a drawing number to a number used for group pattens.
 */
enum class GroupStrategy {
    DIVIDE_BY_10,
    DELTA_SUBTRACT,
    DELTA_ADD
}

/**
 * 0 - 1..9, 1 - 10..19, 2 - 20..29, 3 - 30..39, 4 - 40..49
 * 5,14,22,25,34,49 -> 0,1,2,2,3,4
 */
private fun divideBy10(number: Int): Int = number.div(10)

private fun Int.deltaSubtract(number: Int): Int = this - number

private fun Int.deltaAdd(number: Int): Int = this + number