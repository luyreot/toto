package model

val groupStrategies = mapOf<GroupStrategy, (Int) -> Int>(
    GroupStrategy.DIVIDE_BY_10 to ::divideBy10,
    GroupStrategy.DIVIDE_BY_8 to ::divideBy8
)

/**
 * Enum for various methods for creating group patterns.
 * Each one converts a drawing number to a number used for group pattens.
 */
enum class GroupStrategy {
    DIVIDE_BY_10,
    DIVIDE_BY_8
}

/**
 * 0 - 1..9, 1 - 10..19, 2 - 20..29, 3 - 30..39, 4 - 40..49
 * 5,14,22,25,34,49 -> 0,1,2,2,3,4
 */
private fun divideBy10(number: Int): Int = number.div(10)

private fun divideBy8(number: Int): Int = number.div(8)