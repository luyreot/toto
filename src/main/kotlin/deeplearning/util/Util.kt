package deeplearning.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * TODO: Implement Xavier Initialization: For sigmoid/tanh activations.
 */
object Util {

    fun generateRandomWeights(numNeurons: Int, numInputs: Int): Array<DoubleArray> {
        val weights = Array(numNeurons) { doubleArrayOf() }
        for (i in 0 until numNeurons) {
            val randomWeights = DoubleArray(numInputs)
            for (j in 0 until numInputs) {
                randomWeights[j] = getRandomDouble(-32, 32)
            }
            weights[i] = randomWeights
        }
        return weights
    }

    /**
     * He Initialization: For ReLU activations.
     */
    fun generateRandomWeightsHe(numNeurons: Int, numInputs: Int): Array<DoubleArray> {
        val stdDev = sqrt(2.0 / numInputs)
        val weights = Array(numNeurons) { doubleArrayOf() }
        for (i in 0 until numNeurons) {
            val randomWeights = DoubleArray(numInputs)
            for (j in 0 until numInputs) {
                randomWeights[j] = getRandomDouble(-1, 1) * stdDev
            }
            weights[i] = randomWeights
        }
        return weights
    }

    /**
     * He Initialization: For Tanh activations.
     */
    fun generateRandomWeightsXavier(
        neuronsInCurrentLayer: Int,
        neuronsInPreviousLayer: Int
    ): Array<DoubleArray> {
        val limit = sqrt(6.0 / (neuronsInPreviousLayer + neuronsInCurrentLayer))
        val weights = Array(neuronsInCurrentLayer) { doubleArrayOf() }
        for (i in 0 until neuronsInCurrentLayer) {
            val randomWeights = DoubleArray(neuronsInPreviousLayer)
            for (j in 0 until neuronsInPreviousLayer) {
                randomWeights[j] = Random.nextDouble(-limit, limit)
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