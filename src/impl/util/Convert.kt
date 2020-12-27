package impl.util

/**
 * Handles conversion of a drawing number to a number for the various patterns.
 * Holds all the strategies for the possibles conversions.
 */
object Convert {

    // region Color Pattern

    /**
     * Convert a drawing number to a number used for color pattens.
     * 0 - 1..9, 1 - 10..19, 2 - 20..29, 3 - 30..39, 4 - 40..49
     * 5,14,22,25,34,49 -> 0,1,2,2,3,4
     */
    private fun colorStrategyDefault(num: Int): Int = num / 10

    // No need to sort the array, drawing numbers are already sorted
    fun convertToColorPattern(
            drawing: IntArray,
            strategy: ((num: Int) -> Int) = { num -> colorStrategyDefault(num) }): IntArray {
        return drawing.map { number -> strategy.invoke(number) }.toIntArray()
    }

    // endregion Color Pattern


    // region High Low Pattern

    // Can also be 24, but with 25 we achieve the correct probability % from the LottoMetrix website.
    private const val HIGH_LOW_MIDPOINT: Int = 25

    /**
     * Convert a drawing number to a number used for low high patterns.
     * 0 - <= 25, 1 - > 25
     * 5,14,22,25,34,49 -> 0,0,0,0,1,1
     */
    private fun highLowStrategyDefault(num: Int): Int = if (num <= HIGH_LOW_MIDPOINT) 0 else 1

    // No need to sort the array, drawing numbers are already sorted
    fun convertToHighLowPattern(
            drawing: IntArray,
            strategy: ((num: Int) -> Int) = { num -> highLowStrategyDefault(num) }): IntArray {
        return drawing.map { number -> strategy.invoke(number) }.toIntArray()
    }

    // endregion High Low Pattern


    // region Odd Even Pattern

    /**
     * Convert a drawing number to a number used for odd even patterns.
     * 0 - odd, 1 - even
     * 5,14,22,25,34,49 -> 0,1,1,0,1,0
     */
    private fun oddEvenStrategyDefault(num: Int): Int = if ((num and 1) == 0) 1 else 0

    // Have to sort the array since the odd and even number might be at any index
    fun convertToOddEvenPattern(
            drawing: IntArray,
            strategy: ((num: Int) -> Int) = { num -> oddEvenStrategyDefault(num) }): IntArray {
        return drawing.map { number -> strategy.invoke(number) }.toIntArray().sortedArray()
    }

    // endregion Odd Even Pattern

}