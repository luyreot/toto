package algorithm

import extensions.toDrawingString

object Generate {

    fun allPossibleColorPatterns() {
        generateIntArrayPatterns(service.allPossibleColorPatterns, 4, IntArray(6), 0)
    }

    fun allPossibleLowHighPatterns() {
        generateIntArrayPatterns(service.allPossibleLowHighPatterns, 1, IntArray(6), 0)
    }

    fun allPossibleOddEvenPatterns() {
        generateIntArrayPatterns(service.allPossibleOddEvenPatterns, 1, IntArray(6), 0)
    }

    private fun generateIntArrayPatterns(patterns: MutableSet<String>, end: Int, array: IntArray, index: Int) {
        for (x in 0..end) {
            if (index > 0 && x < array[index - 1]) {
                continue
            }
            array[index] = x
            if (index == array.size - 1) {
                patterns.add(array.sortedArray().toDrawingString())
                if (x == end) {
                    return
                }
            } else {
                generateIntArrayPatterns(patterns, end, array, index + 1)
            }
        }
    }

}