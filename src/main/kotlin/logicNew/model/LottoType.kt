package logicNew.model

/**
 * Holds information about the different types of lotto drawings - 6x49, etc.
 */
enum class LottoType(
    val numberCount: Int,
    val drawingSize: Int,
    val lowHighMidPoint: Int
) {
    D_6X49(
        numberCount = 49,
        drawingSize = 6,
        // Can also be 24, but with 25 we achieve the correct probability % from the LottoMetrix website
        lowHighMidPoint = 25
    ),

    D_6X42(
        numberCount = 42,
        drawingSize = 6,
        lowHighMidPoint = 21
    ),

    D_5X35(
        numberCount = 35,
        drawingSize = 5,
        lowHighMidPoint = 18
    )
}