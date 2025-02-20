package systems.deeplearning.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * TODO: Implement Xavier Initialization: For sigmoid/tanh activations.
 */
object Util {

    fun generateRandomWeights(numNeurons: Int, numInputs: Int): Array<DoubleArray> {
        return Array(numNeurons) { DoubleArray(numInputs) { Random.nextDouble(-1.0, 1.0) } }
    }

    /**
     * He Initialization: Suitable for ReLU activations.
     */
    fun generateRandomWeightsHe(numNeurons: Int, numInputs: Int): Array<DoubleArray> {
        val stdDev = sqrt(2.0 / numInputs)
        return Array(numNeurons) { DoubleArray(numInputs) { Random.nextDouble(-1.0, 1.0) * stdDev } }
    }

    /**
     * Xavier Initialization: Suitable for Tanh and Sigmoid activations.
     */
    fun generateRandomWeightsXavier(neuronsInCurrentLayer: Int, neuronsInPreviousLayer: Int): Array<DoubleArray> {
        val limit = sqrt(6.0 / (neuronsInPreviousLayer + neuronsInCurrentLayer))
        return Array(neuronsInCurrentLayer) { DoubleArray(neuronsInPreviousLayer) { Random.nextDouble(-limit, limit) } }
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