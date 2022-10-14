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

    fun predictNextDrawing() {

    }
}