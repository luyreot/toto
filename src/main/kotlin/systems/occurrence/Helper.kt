package systems.occurrence

import util.Draw

fun List<Draw>.countOccurrences(number: Int): Int =
    count { it.numbers.contains(number) }

fun List<Int>.median(): Double {
    if (isEmpty()) return 0.0
    val sorted = this.sorted()
    val mid = size / 2
    return if (size % 2 == 0) {
        (sorted[mid - 1] + sorted[mid]) / 2.0
    } else {
        sorted[mid].toDouble()
    }
}

fun List<Int>.mean(): Double {
    if (isEmpty()) return 0.0
    return sum().toDouble() / size
}