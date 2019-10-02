package algorithm

import extensions.addChain
import utils.*

object Markov {

    val numberSameDrawingChains = mutableMapOf<String, MutableMap<String, Int>>()
    val numberPreviousDrawingChains = mutableMapOf<String, MutableMap<String, Int>>()
    val colorChains = mutableMapOf<String, MutableMap<String, Int>>()
    val lowHighChains = mutableMapOf<String, MutableMap<String, Int>>()
    val oddEvenChains = mutableMapOf<String, MutableMap<String, Int>>()

    fun train() {
        drawingsList.forEachIndexed { drawingIndex, drawing ->
            numberSameDrawingChains.addChain(drawing.numbers)

            if (drawingIndex > 1) {
                numberPreviousDrawingChains.addChain(drawingsList[drawingIndex - 1].numbers, drawing.numbers)
                colorChains.addChain(
                        convertDrawingIntArrayToString(convertDrawingIntArrayToColorPatternArray(drawingsList[drawingIndex - 1].numbers)),
                        convertDrawingIntArrayToString(convertDrawingIntArrayToColorPatternArray(drawing.numbers))
                )
                lowHighChains.addChain(
                        convertDrawingIntArrayToString(convertDrawingIntArrayToLowHighPatternArray(drawingsList[drawingIndex - 1].numbers)),
                        convertDrawingIntArrayToString(convertDrawingIntArrayToLowHighPatternArray(drawing.numbers))
                )
                oddEvenChains.addChain(
                        convertDrawingIntArrayToString(convertDrawingIntArrayToOddEvenPatternArray(drawingsList[drawingIndex - 1].numbers)),
                        convertDrawingIntArrayToString(convertDrawingIntArrayToOddEvenPatternArray(drawing.numbers))
                )
            }
        }
    }

    fun sortChains() {
        // TODO
        numberSameDrawingChains.forEach { (key, value) ->
            numberSameDrawingChains[key] = value.toList().sortedBy { (_, value) -> value }.toMap().toMutableMap()
        }
        numberPreviousDrawingChains.forEach { (key, value) ->
            numberPreviousDrawingChains[key] = value.toList().sortedBy { (_, value) -> value }.toMap().toMutableMap()
        }
        colorChains.forEach { (key, value) ->
            colorChains[key] = value.toList().sortedBy { (_, value) -> value }.toMap().toMutableMap()
        }
        lowHighChains.forEach { (key, value) ->
            lowHighChains[key] = value.toList().sortedBy { (_, value) -> value }.toMap().toMutableMap()
        }
        oddEvenChains.forEach { (key, value) ->
            oddEvenChains[key] = value.toList().sortedBy { (_, value) -> value }.toMap().toMutableMap()
        }
    }

}