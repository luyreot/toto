package model

import util.Constants

enum class TotoType(
    val filePath: String,
    val totalNumbers: Int,
    val size: Int,
    val isNumberValid: (Int) -> Boolean,
    val getNumberGroup: (Int) -> Int,
    val getNumberLowHigh: (Int) -> Int,
    val getNumberOddEven: (Int) -> Int
) {
    T_6X49(
        filePath = Constants.PATH_TXT_6x49,
        totalNumbers = 49,
        size = 6,
        isNumberValid = { number: Int -> number in 1..49 },
        getNumberGroup = { number: Int -> number.div(10) },
        // Can also be 24, but with 25 we achieve the correct probability % from the LottoMetrix website
        getNumberLowHigh = { number: Int -> if (number <= 25) 0 else 1 },
        getNumberOddEven = { number: Int -> if ((number and 1) == 0) 1 else 0 }
    ),

    T_6X42(
        filePath = Constants.PATH_TXT_6x42,
        totalNumbers = 42,
        size = 6,
        isNumberValid = { number: Int -> number in 1..42 },
        getNumberGroup = { number: Int -> number.div(8) },
        getNumberLowHigh = { number: Int -> if (number <= 21) 0 else 1 },
        getNumberOddEven = { number: Int -> if ((number and 1) == 0) 1 else 0 }
    ),

    T_5X35(
        filePath = Constants.PATH_TXT_5x35,
        totalNumbers = 35,
        size = 5,
        isNumberValid = { number: Int -> number in 1..35 },
        getNumberGroup = { number: Int -> number.div(8) },
        getNumberLowHigh = { number: Int -> if (number <= 18) 0 else 1 },
        getNumberOddEven = { number: Int -> if ((number and 1) == 0) 1 else 0 }
    )
}