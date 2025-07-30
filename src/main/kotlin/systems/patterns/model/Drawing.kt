package systems.patterns.model

import extension.replaceBrackets
import util.Logger

/**
 * Represents different types of toto numbers:
 * - drawing
 * - odd/even
 * - low/high
 * - group
 *
 * An odd/even pattern for a toto drawing will look like this:
 * 0 - odd, 1 - even
 * 5,14,22,25,34,49 -> 0,1,1,0,1,0
 *
 * A low/high pattern for a toto drawing will look like this:
 * 0 - <= 25, 1 - > 25 (when the toto type is 6x49)
 * 5,14,22,25,34,49 -> 0,0,0,0,1,1
 *
 * A group pattern for a toto drawing will look like this:
 * 0 - 1..9, 1 - 10..19, 2 - 20..29, 3 - 30..39, 4 - 40..49
 * 5,14,22,25,34,49 -> 0,1,2,2,3,4
 *
 * The size of the array depends on the toto type - 6x49, 6x42 or 5x35.
 */
open class Drawing(
    val year: Int,
    val issue: Int,
    val numbers: IntArray,
    val groupPattern: IntArray,
    val lowHighPattern: IntArray,
    val oddEvenPattern: IntArray
) {

    fun printDrawingInfo() {
        Logger.p("Year: $year, Issue: $issue, Numbers: ${getNumbersAsString()}")
    }

    fun getNumbersAsString(): String {
        return numbers.toList().toString().replaceBrackets()
    }
}