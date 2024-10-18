package util

object Logg {

    fun p(msg: String?) {
        println(msg)
    }

    fun p(list: List<Int>) {
        println(list)
    }

    fun printIntArray(array: IntArray) {
        println(array.toList().toString().replace("[", "").replace("]", ""))
    }
}