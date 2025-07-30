package util

import extension.replaceBrackets

object Logger {

    fun p(msg: String?) {
        println(msg)
    }

    fun p(list: List<Int>) {
        println(list)
    }

    fun p(array: IntArray) {
        println(array.toList().toString().replaceBrackets())
    }
}