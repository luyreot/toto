package util

import data.Drawings
import extension.toStringDrawing
import model.chain.Chain
import model.pattern.PatternBase
import model.pattern.PatternNumeric

object Helper {

    /**
     * Prints how many duplicate drawings there are in [Drawings.drawings].
     */
    fun printDuplicateDrawingsCount() {
        val totalDrawingsCount = Drawings.drawings.count()
        val drawingArrayToSetCount = Drawings.drawings.map { it.numbers }.toHashSet().count()
        println("Duplicate drawings - ${totalDrawingsCount - drawingArrayToSetCount}")
    }

    /**
     * Check [PatternBase.compareTo].
     */
    fun <K, V : PatternBase> sortPatternMap(map: MutableMap<K, V>) {
        map.forEach { (_, v) ->
            if (v is PatternNumeric) {
                v.sortFrequencies()
            }
        }
        sortMap(map)
    }

    /**
     * Check [Chain.compareTo].
     */
    fun <K> sortChainMap(map: MutableMap<K, Chain>) {
        map.forEach { (_, v) ->
            sortMap(v.entityMap)
        }
        sortMap(map)
    }

    /**
     * Sorts a mutable map by its values.
     */
    private fun <K, V : Comparable<V>> sortMap(map: MutableMap<K, V>) {
        val sortedMap = map
                .toList()
                .sortedBy { (_, v) -> v }
                .toMap()
        map.clear()
        map.putAll(sortedMap)
    }

    /**
     * TODO
     */
    fun getAllPossibleColorPatterns(patterns: MutableSet<String>, end: Int, array: IntArray, index: Int) {
        for (x in 0..end) {
            if (index > 0 && x < array[index - 1]) continue
            array[index] = x
            if (index == array.size - 1) {
                patterns.add(array.sortedArray().toStringDrawing())
                if (x == end) return
            } else {
                getAllPossibleColorPatterns(patterns, end, array, index + 1)
            }
        }
    }

}