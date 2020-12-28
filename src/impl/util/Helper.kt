package impl.util

import impl.data.Drawings
import impl.model.pattern.PatternBase
import impl.model.pattern.PatternNumeric

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
        val sortedMap = map
                .toList()
                .sortedBy { (_, v) -> v }
                .toMap()
        map.clear()
        map.putAll(sortedMap)
    }

}