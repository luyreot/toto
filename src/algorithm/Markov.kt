package algorithm

import extensions.addChain
import extensions.sortChain
import extensions.toDrawingString
import service.*
import utils.convertDrawingIntArrayToColorPatternArray
import utils.convertDrawingIntArrayToLowHighPatternArray
import utils.convertDrawingIntArrayToOddEvenPatternArray

object Markov {

    fun train() {
        drawingsList.forEachIndexed { drawingIndex, drawing ->
            numberSameDrawingChains.addChain(drawing.numbers)

            if (drawingIndex > 1) {
                numberPreviousDrawingChains.addChain(drawingsList[drawingIndex - 1].numbers, drawing.numbers)
                colorChains.addChain(
                        convertDrawingIntArrayToColorPatternArray(drawingsList[drawingIndex - 1].numbers).toDrawingString(),
                        convertDrawingIntArrayToColorPatternArray(drawing.numbers).toDrawingString()
                )
                lowHighChains.addChain(
                        convertDrawingIntArrayToLowHighPatternArray(drawingsList[drawingIndex - 1].numbers).toDrawingString(),
                        convertDrawingIntArrayToLowHighPatternArray(drawing.numbers).toDrawingString()
                )
                oddEvenChains.addChain(
                        convertDrawingIntArrayToOddEvenPatternArray(drawingsList[drawingIndex - 1].numbers).toDrawingString(),
                        convertDrawingIntArrayToOddEvenPatternArray(drawing.numbers).toDrawingString()
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