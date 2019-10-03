package algorithm

import extensions.toDrawingString

object Generate {

    fun allPossibleColorPatterns(): Set<String> {
        val patterns = mutableSetOf<String>()

        for (p1 in 0..4) {
            for (p2 in 0..4) {
                for (p3 in 0..4) {
                    for (p4 in 0..4) {
                        for (p5 in 0..4) {
                            for (p6 in 0..4) {
                                patterns.add(intArrayOf(p1, p2, p3, p4, p5, p6).sortedArray().toDrawingString())
                            }
                        }
                    }
                }
            }
        }

        return patterns
    }

    fun allPossibleLowHighOddEvenPatterns(): Set<String> {
        val patterns = mutableSetOf<String>()

        for (p1 in 0..1) {
            for (p2 in 0..1) {
                for (p3 in 0..1) {
                    for (p4 in 0..1) {
                        for (p5 in 0..1) {
                            for (p6 in 0..1) {
                                patterns.add(intArrayOf(p1, p2, p3, p4, p5, p6).sortedArray().toDrawingString())
                            }
                        }
                    }
                }
            }
        }

        return patterns
    }

}