package logicNew.data

import kotlinx.coroutines.coroutineScope
import logicNew.extensions.clear
import logicNew.model.TotoFrequency
import logicNew.model.TotoNumber
import logicNew.model.TotoPattern
import logicNew.model.TotoType

class TotoGroupPatternStats(
    private val totoType: TotoType,
    private val totoNumbers: TotoNumbers,
    private val groupStrategy: (number: Int) -> Int
) {

    val patterns: Map<TotoPattern, Int>
        get() = patternsCache

    private val patternsCache = mutableMapOf<TotoPattern, Int>()

    val frequencies: Map<TotoPattern, List<TotoFrequency>>
        get() = frequenciesCache

    private val frequenciesCache = mutableMapOf<TotoPattern, MutableList<TotoFrequency>>()

    suspend fun calculateTotoGroupPatternStats() = coroutineScope {
        totoNumbers.numbers
            .sortedWith(compareBy<TotoNumber> { it.year }.thenBy { it.issue }.thenBy { it.position })
            .let { sortedTotoNumbers ->
                val currentDrawing = IntArray(totoType.drawingSize)
                var currentDrawingIndex = 0
                val lastTotoPatternOccurrenceMap = mutableMapOf<TotoPattern, Int>()

                sortedTotoNumbers.forEach { totoNumber ->
                    currentDrawing[totoNumber.position] = totoNumber.number

                    if (totoNumber.position == totoType.drawingSize - 1) {
                        currentDrawingIndex += 1

                        val groupPattern = TotoPattern(
                            pattern = convertTotoNumbersToGroupPattern(currentDrawing.copyOf())
                        )

                        patternsCache.merge(groupPattern, 1, Int::plus)

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

    private fun convertTotoNumbersToGroupPattern(
        numbers: IntArray
    ): IntArray {
        for (i in numbers.indices) {
            numbers[i] = groupStrategy.invoke(numbers[i])
        }

        return numbers
    }
}