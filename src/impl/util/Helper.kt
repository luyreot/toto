package impl.util

import impl.data.Data

object Helper {

    /**
     * Prints how many duplicate drawings there are in [Data.drawings].
     */
    fun printDuplicateDrawingsCount() {
        val totalDrawingsCount = Data.drawings.count()
        val drawingArrayToSetCount = Data.drawings.map { it.numbers }.toHashSet().count()
        println("Duplicate drawings - ${totalDrawingsCount - drawingArrayToSetCount}")
    }

}