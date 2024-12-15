package deeplearning.util

object Data {

    fun calculateGapSinceLast(number: Int, draws: List<List<Int>>, drawIndex: Int): Int {
        for (i in drawIndex - 1 downTo 0) {
            if (number in draws[i]) {
                return drawIndex - i
            }
        }
        // The number has never appeared
        return -1
    }
}