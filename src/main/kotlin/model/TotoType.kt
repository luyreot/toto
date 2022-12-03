package model

/**
 * Holds information about the different types of toto drawings - 6x49, etc.
 */
enum class TotoType(
    val totalNumbers: Int,
    val size: Int,
    val lowHighMidPoint: Int
) {
    T_6X49(
        totalNumbers = 49,
        size = 6,
        // Can also be 24, but with 25 we achieve the correct probability % from the LottoMetrix website
        lowHighMidPoint = 25
    ),

    T_6X42(
        totalNumbers = 42,
        size = 6,
        lowHighMidPoint = 21
    ),

    T_5X35(
        totalNumbers = 35,
        size = 5,
        lowHighMidPoint = 18
    )
}