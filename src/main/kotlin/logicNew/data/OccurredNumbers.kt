package logicNew.data

import kotlinx.coroutines.coroutineScope
import logicNew.model.drawing.DrawingType
import logicNew.model.drawing.DrawnNumber

/**
 * Holds information on how often a number has been drawn.
 */
class OccurredNumbers(
    private val drawingType: DrawingType
) {

    val numbers: Map<Int, Int>
        get() = numbersCache

    private val numbersCache = mutableMapOf<Int, Int>()

    init {
        when (drawingType) {
            DrawingType.D_6x49 -> for (i in 1..49) numbersCache[i] = 0
            DrawingType.D_6x42 -> for (i in 1..42) numbersCache[i] = 0
            DrawingType.D_5x35 -> for (i in 1..35) numbersCache[i] = 0
        }
    }

    suspend fun calculateNumberOccurrences(
        drawnNumbers: List<DrawnNumber>
    ) = coroutineScope {
        drawnNumbers.forEach { number ->
            numbersCache.merge(number.number, 1, Int::plus)
        }

        validateNumberOccurrences()
    }

    private fun validateNumberOccurrences() {
        when (drawingType) {
            DrawingType.D_6x49 -> if (numbersCache.size != 49)
                throw IllegalArgumentException("Drawing is not ${drawingType.name}!")

            DrawingType.D_6x42 -> if (numbersCache.size != 42)
                throw IllegalArgumentException("Drawing is not ${drawingType.name}!")

            DrawingType.D_5x35 -> if (numbersCache.size != 35)
                throw IllegalArgumentException("Drawing is not ${drawingType.name}!")
        }
    }
}