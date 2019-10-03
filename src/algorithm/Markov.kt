package algorithm

import extensions.addChain
import extensions.sortChain
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
        numberSameDrawingChains.sortChain()
        numberPreviousDrawingChains.sortChain()
        colorChains.sortChain()
        lowHighChains.sortChain()
        oddEvenChains.sortChain()
    }

}