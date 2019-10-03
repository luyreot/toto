package algorithm

import extensions.addChain
import extensions.sortChain
import service.*
import utils.convertDrawingIntArrayToColorPatternArray
import utils.convertDrawingIntArrayToLowHighPatternArray
import utils.convertDrawingIntArrayToOddEvenPatternArray
import utils.convertDrawingIntArrayToString

object Markov {

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