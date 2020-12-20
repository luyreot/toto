package old.algorithm

import old.extensions.addChain
import old.extensions.sortChain
import old.extensions.toDrawingString
import old.service.*
import old.utils.convertDrawingIntArrayToColorPatternArray
import old.utils.convertDrawingIntArrayToLowHighPatternArray
import old.utils.convertDrawingIntArrayToOddEvenPatternArray

object Markov {

    fun trainChains() {
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