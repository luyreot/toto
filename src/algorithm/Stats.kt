package algorithm

import model.Drawing
import service.colorChains

// TODO in progress
object Stats {

    private fun getColorChains(drawing: Drawing): MutableMap<String, Int>? {
//        val drawingAsString = convertDrawingIntArrayToString(convertDrawingIntArrayToColorPatternArray(drawing.numbers))
//        val chains = colorChains[drawingAsString]
//                ?: throw IllegalArgumentException("No chains found for $drawingAsString")

        var probabilitySum = 0.0;
        var probabilityTotal = 0
        var aboveAverage = 0
        colorChains.forEach { (_, chains) ->

            var singleOccurrences = 0
            var multipleOccurrences = 0
            chains.forEach { (_, count) ->
                if (count > 1) {
                    multipleOccurrences++
                }
                if (count == 1) {
                    singleOccurrences++
                }
            }
            val chainsProbability = multipleOccurrences.div(singleOccurrences.toDouble()) * 100.0
            if (chainsProbability != 0.0) {
                println(chainsProbability)
                if(chainsProbability >= 27.878693831212544) {
                    aboveAverage++
                }
            } else {
                println()
            }
            probabilitySum += chainsProbability
            probabilityTotal++
        }

        println("Average probability ${probabilitySum.div(probabilityTotal)}")
        println("Above average count $aboveAverage out of $probabilityTotal")


        return null
    }

}