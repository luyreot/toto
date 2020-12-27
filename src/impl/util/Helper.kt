package impl.util

import impl.data.Drawing

object Helper {

    /**
     * Prints how many duplicate drawings there are in [Drawing.drawings].
     */
    fun printDuplicateDrawingsCount() {
        val totalDrawingsCount = Drawing.drawings.count()
        val drawingArrayToSetCount = Drawing.drawings.map { it.numbers }.toHashSet().count()
        println("Duplicate drawings - ${totalDrawingsCount - drawingArrayToSetCount}")
    }

}