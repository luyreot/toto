package model

import util.Constants

enum class TotoType(
    val filePath: String,
    val totalNumbers: Int,
    val size: Int
) {
    T_6X49(
        filePath = Constants.PATH_TXT_6x49,
        totalNumbers = 49,
        size = 6
    ),

    T_6X42(
        filePath = Constants.PATH_TXT_6x42,
        totalNumbers = 42,
        size = 6
    ),

    T_5X35(
        filePath = Constants.PATH_TXT_5x35,
        totalNumbers = 35,
        size = 5
    )
}