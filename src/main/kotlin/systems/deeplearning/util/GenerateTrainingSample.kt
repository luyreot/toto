package systems.deeplearning.util

import systems.deeplearning.model.TrainingSample

fun generateTrainingSamples(
    totalNumbersInDraw: Int,
    draws: List<List<Int>>,
    windowSize: Int // Number of past draws used as input
): List<TrainingSample> {
    val inputSize = totalNumbersInDraw * windowSize
    val result = mutableListOf<TrainingSample>()

    val min = 0.1
    val max = 0.9

    // We need at least (windowSize + 1) draws to generate one training sample
    for (i in 0 until (draws.size - windowSize)) {
        val input = DoubleArray(inputSize) { min }
        val target = DoubleArray(totalNumbersInDraw) { min }

        // Create input from past `windowSize` draws
        for (w in 0 until windowSize) {
            val draw = draws[i + w]
            for (num in draw) {
                val index = w * totalNumbersInDraw + (num - 1)
                input[index] = max
            }
        }

        // Target is the draw that comes immediately after the window
        val nextDraw = draws[i + windowSize]
        for (num in nextDraw) {
            target[num - 1] = max // Basic version, change to 0.9/0.1 if smoothing
        }

        result += TrainingSample(input, target)
    }

    return result
}

fun generateLatestTrainingSample(
    totalNumbersInDraw: Int,
    draws: List<List<Int>>,
    windowSize: Int
): TrainingSample {
    val inputSize = totalNumbersInDraw * windowSize

    val min = 0.1
    val max = 0.9

    if (draws.size <= windowSize)
        throw IllegalArgumentException("Draw size ${draws.size} is lower or equal to window size $windowSize")

    val input = DoubleArray(inputSize) { min }
    val target = DoubleArray(totalNumbersInDraw) { min }

    // Use the last `windowSize` draws as input
    val start = draws.size - windowSize - 1
    for (w in 0 until windowSize) {
        val draw = draws[start + w]
        for (num in draw) {
            val index = w * totalNumbersInDraw + (num - 1)
            input[index] = max
        }
    }

    // Target is the draw that comes immediately after the input window
    val nextDraw = draws.last()
    for (num in nextDraw) {
        target[num - 1] = max
    }

    return TrainingSample(input, target)
}

fun generateLatestInputOnly(
    totalNumbersInDraw: Int,
    draws: List<List<Int>>,
    windowSize: Int
): DoubleArray {
    val inputSize = totalNumbersInDraw * windowSize
    val min = 0.1
    val max = 0.9

    if (draws.size < windowSize)
        throw IllegalArgumentException("Draw size ${draws.size} is lower or equal to window size $windowSize")

    val input = DoubleArray(inputSize) { min }

    // Use the last `windowSize` draws as input
    val start = draws.size - windowSize
    for (w in 0 until windowSize) {
        val draw = draws[start + w]
        for (num in draw) {
            val index = w * totalNumbersInDraw + (num - 1)
            input[index] = max
        }
    }

    return input
}