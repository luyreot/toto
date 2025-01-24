package deeplearning.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

object Util {

    fun generateRandomWeights(numNeurons: Int, numInputs: Int): Array<DoubleArray> {
        val weights = Array(numNeurons) { doubleArrayOf() }
        for (i in 0 until numNeurons) {
            val randomWeights = DoubleArray(numInputs)
            for (j in 0 until numInputs) {
                randomWeights[j] = getRandomDouble(-1, 1)
            }
            weights[i] = randomWeights
        }
        return weights
    }

    fun getRandomDouble(min: Int, max: Int): Double {
        require(min < max) { "Invalid range [$min, $max]" }
        return min + Random.nextDouble() * (max - min)
    }

    fun getCurrentTime(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val current = LocalDateTime.now().format(formatter)
        return current
    }
}