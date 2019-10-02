package akotlin.algorithm

import akotlin.utils.*

object Markov {

    val numberCurrentDrawingChain = HashMap<Int, HashMap<Int, Int>>()
    val numberPreviousDrawingChain = HashMap<Int, HashMap<Int, Int>>()
    val colorChain = HashMap<String, HashMap<String, Int>>()
    val lowHighChain = HashMap<String, HashMap<String, Int>>()
    val oddEvenChain = HashMap<String, HashMap<String, Int>>()

    fun train() {
        drawingsList.forEachIndexed { drawingIndex, drawing ->
            processNumberChain(numberCurrentDrawingChain, drawing.numbers)

            if (drawingIndex > 1) {
                processNumberChain(numberPreviousDrawingChain, drawingsList[drawingIndex - 1].numbers, drawing.numbers)
                processArrayChain(
                        colorChain,
                        convertIntArrayToString(convertDrawingArrayToColorPatternArray(drawingsList[drawingIndex - 1].numbers)),
                        convertIntArrayToString(convertDrawingArrayToColorPatternArray(drawing.numbers))
                )
                processArrayChain(
                        lowHighChain,
                        convertIntArrayToString(convertDrawingArrayToLowHighPatternArray(drawingsList[drawingIndex - 1].numbers)),
                        convertIntArrayToString(convertDrawingArrayToLowHighPatternArray(drawing.numbers))
                )
                processArrayChain(
                        oddEvenChain,
                        convertIntArrayToString(convertDrawingArrayToOddEvenPatternArray(drawingsList[drawingIndex - 1].numbers)),
                        convertIntArrayToString(convertDrawingArrayToOddEvenPatternArray(drawing.numbers))
                )
            }
        }
    }

    private fun processNumberChain(chain: HashMap<Int, HashMap<Int, Int>>, drawing: IntArray) {
        drawing.forEachIndexed { firstIndex, firstNumber ->
            drawing.forEachIndexed { secondIndex, secondNumber ->
                if (firstIndex < secondIndex) {
                    if (chain.containsKey(firstNumber)) {
                        if (chain[firstNumber]!!.containsKey(secondNumber)) {
                            chain[firstNumber]!![secondNumber] = chain[firstNumber]!![secondNumber]!!.inc()
                        } else {
                            chain[firstNumber]!![secondNumber] = 1
                        }
                    } else {
                        chain[firstNumber] = hashMapOf()
                        chain[firstNumber]!![secondNumber] = 1
                    }
                }
            }
        }
    }

    private fun processNumberChain(chain: HashMap<Int, HashMap<Int, Int>>, prevDrawing: IntArray, currDrawing: IntArray) {
        prevDrawing.forEach { prevNumber ->
            currDrawing.forEach { currNumber ->
                if (chain.containsKey(prevNumber)) {
                    if (chain[prevNumber]!!.containsKey(currNumber)) {
                        chain[prevNumber]!![currNumber] = chain[prevNumber]!![currNumber]!!.inc()
                    } else {
                        chain[prevNumber]!![currNumber] = 1
                    }
                } else {
                    chain[prevNumber] = hashMapOf()
                    chain[prevNumber]!![currNumber] = 1
                }
            }
        }
    }

    private fun processArrayChain(chain: HashMap<String, HashMap<String, Int>>, prevColorPattern: String, currColorPattern: String) {
        if (chain.containsKey(prevColorPattern)) {
            if (chain[prevColorPattern]!!.containsKey(currColorPattern)) {
                chain[prevColorPattern]!![currColorPattern] = chain[prevColorPattern]!![currColorPattern]!!.inc()
            } else {
                chain[prevColorPattern]!![currColorPattern] = 1
            }
        } else {
            chain[prevColorPattern] = hashMapOf()
            chain[prevColorPattern]!![currColorPattern] = 1
        }
    }

}