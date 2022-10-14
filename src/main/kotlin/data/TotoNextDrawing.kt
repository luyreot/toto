package data

import model.TotoType

class TotoNextDrawing(
    private val totoType: TotoType,
    private val totoNumberStats: TotoNumberStats,
    private val totoOddEvenPatternStats: TotoOddEvenPatternStats,
    private val totoOddEvenPatternPredict: TotoOddEvenPatternPredict,
    private val totoLowHighPatternStats: TotoLowHighPatternStats,
    private val totoLowHighPatternPredict: TotoLowHighPatternPredict,
    private val totoGroupPatternStats: TotoGroupPatternStats,
    private val totoGroupPatternPredict: TotoGroupPatternPredict
) {

    lateinit var nextOddEvenPattern: IntArray
    lateinit var nextLowHighPattern: IntArray
    lateinit var nextGroupPattern: IntArray

    fun populateArrays() {
        nextOddEvenPattern = totoOddEvenPatternPredict.nextOddEvenPattern.map { it.toInt() }.toIntArray()
        if (nextOddEvenPattern.size != totoType.drawingSize)
            throw IllegalArgumentException("Something wrong with the predicted odd even pattern!")

        nextLowHighPattern = totoLowHighPatternPredict.nextLowHighPattern.map { it.toInt() }.toIntArray()
        if (nextLowHighPattern.size != totoType.drawingSize)
            throw IllegalArgumentException("Something wrong with the predicted low high pattern!")

        nextGroupPattern = totoGroupPatternPredict.nextGroupPattern.map { it.toInt() }.toIntArray()
        if (nextGroupPattern.size != totoType.drawingSize)
            throw IllegalArgumentException("Something wrong with the predicted group pattern!")
    }

    fun predictNextDrawing() {
        for (i in 0 until totoType.drawingSize) {
            val isOdd = nextOddEvenPattern[i] == 0
            val isLow = nextOddEvenPattern[i] == 0

        }
    }
}