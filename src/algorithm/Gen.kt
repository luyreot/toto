package algorithm

import model.Drawing
import service.colorChains
import service.drawingsList
import service.lowHighChains
import service.oddEvenChains
import utils.*

object Gen {

    // 1. Get the last drawing
    // 2. Get its color chains (maybe the top n number, not all??)
    // 3. Generate all possible drawing combinations for each color pattern
    // 4. Get the low/high chains (maybe the top n number, not all??)
    // 5. Remove any drawings that do not conform to the low/high patterns
    //    5.1. Store the removed ones and check them if their color pattern probability (decide whether to put them back or not).
    // 6. Same as 4 & 5 but for odd/even chains.
    // 7. Remove any drawings that have already been drawn in previous drawings.
    // 8.


    fun gen() {
        val lastDrawing = getLastDrawing()
        val colorChains = getColorChains(lastDrawing)
        val lowHighChains = getLowHighChains(lastDrawing)
        val oddEvenChains = getOddEvenChains(lastDrawing)

        val drawings = mutableListOf<Drawing>()
        colorChains?.forEach { chain -> drawings.addAll(generatePossibleDrawingsForColorPattern(convertDrawingStringToIntArray(chain.key))) }
    }

    private fun getLastDrawing(): Drawing = drawingsList[drawingsList.size - 1]

    private fun getColorChains(drawing: Drawing): MutableMap<String, Int>? {
        return colorChains[convertDrawingIntArrayToString(convertDrawingIntArrayToColorPatternArray(drawing.numbers))]
    }

    private fun getLowHighChains(drawing: Drawing): MutableMap<String, Int>? {
        return lowHighChains[convertDrawingIntArrayToString(convertDrawingIntArrayToLowHighPatternArray(drawing.numbers))]
    }

    private fun getOddEvenChains(drawing: Drawing): MutableMap<String, Int>? {
        return oddEvenChains[convertDrawingIntArrayToString(convertDrawingIntArrayToOddEvenPatternArray(drawing.numbers))]
    }

    // TODO get number chains

    private fun generatePossibleDrawingsForColorPattern(colorPattern: IntArray): List<Drawing> {
        val drawings = mutableListOf<Drawing>()



        return drawings
    }

}