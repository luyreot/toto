package data

import extensions.clear
import model.*

/**
 * Alternative way of handling group patterns for drawings.
 *
 * Given a drawing, perform the following algorithm:
 * 1. sort the drawing in ascending order
 * 2. take the first / lowest number as the first result in the output
 * 3. divide subsequent numbers by the previous one in the array
 * 4. use the result in the output
 *
 * To reverse the output to get the original drawing, perform the following algorithm:
 * 1. take the first number as the first result in the output
 * 2. take the second number and add it to the result from step 1
 * 3. use the product from step 2 as the next result in the output
 * 4. perform addition of each subsequent number with the previous product result
 * 5. use each new product in the output
 *
 * Here is an example:
 *
 * Original drawing:
 * 4, 6, 9, 21, 36, 46
 *
 * Delta algorithm operation forward:
 * 4
 * 6 - 4 = 2
 * 9 - 6 = 3
 * 21 - 9 = 12
 * 36 - 21 = 15
 * 46 - 36 = 10
 *
 * Output:
 * 4, 2, 3, 12, 15, 10
 *
 * Delta algorithm reversed (backwards) operation:
 * 4
 * 4 + 2 = 6
 * 6 + 3 = 9
 * 9 + 12 = 21
 * 21 + 15 = 36
 * 36 + 10 = 46
 *
 * Output / Original drawing:
 * 4, 6, 9, 21, 36, 46
 *
 * This algorithm serves the purpose of lowering the total possible numbers that can occur in a drawing.
 * In the case of 6/49 instead of having a total of 49 different numbers we end up with around 15.
 */
class TotoGroupPatternDeltaStats(
    private val totoType: TotoType,
    private val totoNumbers: TotoNumbers,
    private val fromYear: Int? = null
) {

    val patterns: Map<TotoPattern, Int>
        get() = patternsCache

    private val patternsCache = mutableMapOf<TotoPattern, Int>()

    val frequencies: Map<TotoPattern, List<TotoFrequency>>
        get() = frequenciesCache

    private val frequenciesCache = mutableMapOf<TotoPattern, MutableList<TotoFrequency>>()

    private val groupStrategyMethod = groupStrategies[TotoGroupStrategy.DELTA_SUBTRACT] as? (Int, Int) -> Int

    init {
        if (groupStrategyMethod == null)
            throw IllegalArgumentException("Group strategy method is null!")
    }

    fun calculateTotoGroupPatternDeltaStats() {
        totoNumbers.numbers
            .sortedWith(compareBy<TotoNumber> { it.year }.thenBy { it.issue }.thenBy { it.position })
            .let { sortedTotoNumbers ->
                val currentDrawing = IntArray(totoType.drawingSize)
                var currentDrawingIndex = 0
                val lastTotoPatternOccurrenceMap = mutableMapOf<TotoPattern, Int>()

                sortedTotoNumbers.forEach { totoNumber ->
                    if (fromYear != null && totoNumber.year < fromYear) {
                        return@forEach
                    }

                    currentDrawing[totoNumber.position] = totoNumber.number

                    if (totoNumber.position == totoType.drawingSize - 1) {
                        currentDrawingIndex += 1

                        val groupPattern = TotoPattern(convertTotoNumbersToGroupPatternDelta(currentDrawing.copyOf()))

                        patternsCache.merge(groupPattern, 1, Int::plus)

                        // Predict algo call

                        currentDrawing.clear()

                        // Frequencies

                        if (lastTotoPatternOccurrenceMap.containsKey(groupPattern).not()) {
                            lastTotoPatternOccurrenceMap[groupPattern] = currentDrawingIndex
                            return@forEach
                        }

                        lastTotoPatternOccurrenceMap[groupPattern]?.let { lastDrawingIndex ->
                            val newFrequency = currentDrawingIndex - lastDrawingIndex

                            lastTotoPatternOccurrenceMap[groupPattern] = currentDrawingIndex

                            if (frequenciesCache.containsKey(groupPattern).not()) {
                                frequenciesCache[groupPattern] = mutableListOf(TotoFrequency(frequency = newFrequency))
                                return@forEach
                            }

                            val doesNewFrequencyExist: Boolean = frequenciesCache[groupPattern]?.any { it.frequency == newFrequency }
                                ?: false
                            if (doesNewFrequencyExist.not()) {
                                frequenciesCache[groupPattern]?.add(TotoFrequency(frequency = newFrequency))
                                return@forEach
                            }

                            val index: Int = frequenciesCache[groupPattern]?.indexOfFirst { it.frequency == newFrequency } ?: -1
                            if (index == -1) {
                                frequenciesCache[groupPattern]?.add(TotoFrequency(frequency = newFrequency))
                                return@forEach
                            }

                            val totoFrequency: TotoFrequency? = frequenciesCache[groupPattern]?.get(index)
                            if (totoFrequency == null) {
                                frequenciesCache[groupPattern]?.add(TotoFrequency(frequency = newFrequency))
                                return@forEach
                            }

                            frequenciesCache[groupPattern]?.set(
                                index,
                                totoFrequency.copy(count = totoFrequency.count + 1)
                            )
                        }
                    }
                }
            }
    }

    private fun convertTotoNumbersToGroupPatternDelta(
        numbers: IntArray
    ): IntArray {
        val result = IntArray(numbers.size)

        for (i in numbers.indices) {
            if (i == 0) {
                result[i] = numbers[i]
                continue
            }

            result[i] = groupStrategyMethod?.invoke(numbers[i], numbers[i - 1])
                ?: throw IllegalArgumentException("Group strategy method is null!")
        }

        return result
    }
}