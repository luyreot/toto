package model

/**
 * Methods for creating group patterns.
 * Each one convert a drawing number to a number used for group pattens.
 */

/**
 * 0 - 1..9, 1 - 10..19, 2 - 20..29, 3 - 30..39, 4 - 40..49
 * 5,14,22,25,34,49 -> 0,1,2,2,3,4
 */
fun divideBy10(number: Int): Int = number.div(10)