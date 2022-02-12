package logicOld.data

import logicOld.extension.toStringDrawing
import logicOld.model.chain.Chain
import logicOld.util.Helper

/**
 * Holds information about relations between two entities of the same type.
 * The entities could be either two numbers, or two patterns of the same type - color, odd even, low high.
 * Implies the Markov algorithm, where we track a pattern or a number (entity) and all the possible respective
 * patterns (or numbers) that come sequentially.
 *
 * Currently tracking:
 * - pairs of numbers from within the same drawing
 * - pairs of numbers from subsequent drawings
 * - pairs of patterns (color, odd even, low high) from subsequent drawings
 */
object Chains {

    // Tracks the single number relations from within the same drawing
    val numsInDrawing = mutableMapOf<Int, Chain>()

    // Tracks the single number relations from subsequent drawings
    val numsSubDrawings = mutableMapOf<Int, Chain>()

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
                generateFromNumbersSubDrawings(previousNumbers, drawing.numbers)
                generateFromPatterns(previousNumbers, drawing.numbers)
            }
        }
    }

    /**
     * From within the same drawing, creates every possible two number combination.
     */
    private fun generateFromNumbersInSameDrawing(numbers: IntArray) {
        numbers.forEachIndexed { index1, num1 ->
            numbers.forEachIndexed { index2, num2 ->
                if (index1 != index2) {
                    insertNumberEntity(
                            map = numsInDrawing,
                            entity = num1,
                            entityKey = num2.toString())
                }
            }
        }
    }

    /**
     * From subsequent drawings, creates every possible two number combination.
     */
    private fun generateFromNumbersSubDrawings(previousNumbers: IntArray, numbers: IntArray) {
        previousNumbers.forEach { previousNum ->
            numbers.forEach { num ->
                insertNumberEntity(
                        map = numsSubDrawings,
                        entity = previousNum,
                        entityKey = num.toString())
            }
        }
    }

    /**
     * Helper method for creating new / updating existing Chain number entity from the provided map.
     */
    private fun insertNumberEntity(map: MutableMap<Int, Chain>, entity: Int, entityKey: String) {
        if (map.containsKey(entity)) {
            map[entity]?.updateEntityMap(entityKey)
        } else {
            val chain = Chain(entity.toString())
            chain.updateEntityMap(entityKey)
            map[entity] = chain
        }
    }

    /**
     * Creates a pair of patterns from two subsequent drawing numbers.
     */
    private fun generateFromPatterns(previousNumbers: IntArray, numbers: IntArray) {
//        // Colors
//        var entity: String = Convert.convertToColorPattern(previousNumbers).toStringDrawing()
//        var entityKey: String = Convert.convertToColorPattern(numbers).toStringDrawing()
//        insertPatternEntity(colors, entity, entityKey)
//
//        // Odd Evens
//        entity = Convert.convertToOddEvenPattern(previousNumbers).toStringDrawing()
//        entityKey = Convert.convertToOddEvenPattern(numbers).toStringDrawing()
//        insertPatternEntity(oddEvens, entity, entityKey)
//
//        // Low Highs
//        entity = Convert.convertToHighLowPattern(previousNumbers).toStringDrawing()
//        entityKey = Convert.convertToHighLowPattern(numbers).toStringDrawing()
//        insertPatternEntity(lowHighs, entity, entityKey)
    }

    /**
     * Helper method for creating new / updating existing Chain pattern entity from the provided map.
     */
    private fun insertPatternEntity(map: MutableMap<String, Chain>, entity: String, entityKey: String) {
        if (map.containsKey(entity)) {
            map[entity]?.updateEntityMap(entityKey)
        } else {
            val chain = Chain(entity)
            chain.updateEntityMap(entityKey)
            map[entity] = chain
        }
    }

    private fun sort() {
        Helper.sortChainMap(numsInDrawing)
        Helper.sortChainMap(numsSubDrawings)
        Helper.sortChainMap(colors)
        Helper.sortChainMap(oddEvens)
        Helper.sortChainMap(lowHighs)
    }

}