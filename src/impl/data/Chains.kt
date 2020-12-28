package impl.data

import impl.extension.toStringDrawing
import impl.model.chain.Chain
import impl.util.Convert
import impl.util.Helper

/**
 *
 */
object Chains {

    // Tracks the single number relations from within the same drawing
    val numsInDrawing = mutableMapOf<Int, Chain>()

    // Tracks the single number relations from subsequent drawings
    val numsOffDrawing = mutableMapOf<Int, Chain>()

    // Tracks the pattern relations from subsequent drawings
    val colors = mutableMapOf<String, Chain>()
    val oddEvens = mutableMapOf<String, Chain>()
    val lowHighs = mutableMapOf<String, Chain>()

    init {
        Drawings.checkDrawings()
        generate()
        sort()
    }

    private fun generate() {
        Drawings.drawings.forEachIndexed { index, drawing ->
            generateFromNumbersInSameDrawing(drawing.numbers)

            if (index > 1) {
                val previousNumbers = Drawings.drawings[index - 1].numbers
                generateFromNumbersOffDrawing(previousNumbers, drawing.numbers)
                generateFromPatterns(previousNumbers, drawing.numbers)
            }
        }
    }

    private fun generateFromNumbersInSameDrawing(numbers: IntArray) {
        numbers.forEachIndexed { index1, num1 ->
            numbers.forEachIndexed { index2, num2 ->
                if (index1 != index2) {
                    insertNumberChain(
                            map = numsInDrawing,
                            entity = num1,
                            chainKey = num2.toString())
                }
            }
        }
    }

    private fun generateFromNumbersOffDrawing(previousNumbers: IntArray, numbers: IntArray) {
        previousNumbers.forEach { previousNum ->
            numbers.forEach { num ->
                insertNumberChain(
                        map = numsOffDrawing,
                        entity = previousNum,
                        chainKey = num.toString())
            }
        }
    }

    private fun insertNumberChain(map: MutableMap<Int, Chain>, entity: Int, chainKey: String) {
        if (map.containsKey(entity)) {
            map[entity]?.updateChainMap(chainKey)
        } else {
            val chain = Chain(entity.toString())
            chain.updateChainMap(chainKey)
            map[entity] = chain
        }
    }

    private fun generateFromPatterns(previousNumbers: IntArray, numbers: IntArray) {
        var entity = ""
        var chainKey = ""

        // Colors
        entity = Convert.convertToColorPattern(previousNumbers).toStringDrawing()
        chainKey = Convert.convertToColorPattern(numbers).toStringDrawing()
        insertPatternChain(colors, entity, chainKey)

        // Odd Evens
        entity = Convert.convertToOddEvenPattern(previousNumbers).toStringDrawing()
        chainKey = Convert.convertToOddEvenPattern(numbers).toStringDrawing()
        insertPatternChain(oddEvens, entity, chainKey)

        // Low Highs
        entity = Convert.convertToHighLowPattern(previousNumbers).toStringDrawing()
        chainKey = Convert.convertToHighLowPattern(numbers).toStringDrawing()
        insertPatternChain(lowHighs, entity, chainKey)
    }

    private fun insertPatternChain(map: MutableMap<String, Chain>, entity: String, chainKey: String) {
        if (map.containsKey(entity)) {
            map[entity]?.updateChainMap(chainKey)
        } else {
            val chain = Chain(entity)
            chain.updateChainMap(chainKey)
            map[entity] = chain
        }
    }

    private fun sort() {
        Helper.sortChainMap(numsInDrawing)
        Helper.sortChainMap(numsOffDrawing)
        Helper.sortChainMap(colors)
        Helper.sortChainMap(oddEvens)
        Helper.sortChainMap(lowHighs)
    }

}