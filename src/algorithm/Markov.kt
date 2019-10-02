package algorithm

import utils.*

object Markov {

    val numberCurrentDrawingChains = HashMap<Int, MutableMap<Int, Int>>()
    val numberPreviousDrawingChains = HashMap<Int, MutableMap<Int, Int>>()
    val colorChains = HashMap<String, MutableMap<String, Int>>()
    val lowHighChains = HashMap<String, MutableMap<String, Int>>()
    val oddEvenChains = HashMap<String, MutableMap<String, Int>>()

    fun train() {
        drawingsList.forEachIndexed { drawingIndex, drawing ->
            processNumberChain(numberCurrentDrawingChains, drawing.numbers)

            if (drawingIndex > 1) {
                processNumberChain(numberPreviousDrawingChains, drawingsList[drawingIndex - 1].numbers, drawing.numbers)
                processArrayChain(
                        colorChains,
                        convertIntArrayToString(convertDrawingArrayToColorPatternArray(drawingsList[drawingIndex - 1].numbers)),
                        convertIntArrayToString(convertDrawingArrayToColorPatternArray(drawing.numbers))
                )
                processArrayChain(
                        lowHighChains,
                        convertIntArrayToString(convertDrawingArrayToLowHighPatternArray(drawingsList[drawingIndex - 1].numbers)),
                        convertIntArrayToString(convertDrawingArrayToLowHighPatternArray(drawing.numbers))
                )
                processArrayChain(
                        oddEvenChains,
                        convertIntArrayToString(convertDrawingArrayToOddEvenPatternArray(drawingsList[drawingIndex - 1].numbers)),
                        convertIntArrayToString(convertDrawingArrayToOddEvenPatternArray(drawing.numbers))
                )
            }
        }
    }

    fun sortChains() {
        numberCurrentDrawingChains.forEach { (key, value) ->
            numberCurrentDrawingChains[key] = value.toList().sortedBy { (_, value) -> value }.toMap().toMutableMap()
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

    private fun processNumberChain(chain: HashMap<Int, MutableMap<Int, Int>>, drawing: IntArray) {
        drawing.forEachIndexed { firstIndex, firstNumber ->
            drawing.forEachIndexed { secondIndex, secondNumber ->
                if (firstIndex != secondIndex) {
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

    private fun processNumberChain(chain: HashMap<Int, MutableMap<Int, Int>>, prevDrawing: IntArray, currDrawing: IntArray) {
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

    private fun processArrayChain(chain: HashMap<String, MutableMap<String, Int>>, prevColorPattern: String, currColorPattern: String) {
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