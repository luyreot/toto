package systems.deeplearning.optimization

import systems.deeplearning.model.LayerDense
import kotlin.math.pow
import kotlin.math.sqrt

class Adam(
    val beta1: Double = 0.9,  // First moment decay rate
    val beta2: Double = 0.999, // Second moment decay rate
    val epsilon: Double = 1e-8, // Small constant to prevent division by zero
    val learningRate: Double = 0.0001
) : OptimizationFunction {

    override val type: OptimizationFunctionType = OptimizationFunctionType.Adam

    private var mWeights = mutableMapOf<Pair<Int, Int>, Double>() // First moment estimate (weights)
    private var vWeights = mutableMapOf<Pair<Int, Int>, Double>() // Second moment estimate (weights)
    private var mBiases = mutableMapOf<Int, Double>() // First moment estimate (biases)
    private var vBiases = mutableMapOf<Int, Double>() // Second moment estimate (biases)
    private var t = 0 // Time step counter

    fun optimize(layer: LayerDense) {
        t += 1 // Increment time step

        for (i in layer.biases.indices) {
            val gradB = layer.accumulatedBias[i]

            // Update biased first and second moment estimates
            mBiases[i] = beta1 * (mBiases[i] ?: 0.0) + (1 - beta1) * gradB
            vBiases[i] = beta2 * (vBiases[i] ?: 0.0) + (1 - beta2) * gradB * gradB

            // Compute bias-corrected estimates
            val mBCorrected = mBiases[i]!! / (1 - beta1.pow(t))
            val vBCorrected = vBiases[i]!! / (1 - beta2.pow(t))

            // Update biases
            layer.biases[i] -= learningRate * mBCorrected / (sqrt(vBCorrected) + epsilon)

            for (j in layer.weights[i].indices) {
                val key = Pair(i, j)
                val gradW = layer.accumulatedWeight[i][j]

                // Update biased first and second moment estimates
                mWeights[key] = beta1 * (mWeights[key] ?: 0.0) + (1 - beta1) * gradW
                vWeights[key] = beta2 * (vWeights[key] ?: 0.0) + (1 - beta2) * gradW * gradW

                // Compute bias-corrected estimates
                val mWCorrected = mWeights[key]!! / (1 - beta1.pow(t))
                val vWCorrected = vWeights[key]!! / (1 - beta2.pow(t))

                // Apply weight updates
                layer.weights[i][j] -= learningRate * mWCorrected / (sqrt(vWCorrected) + epsilon)
            }
        }

        // Reset accumulated gradients before updating weights/biases
        for (i in layer.accumulatedWeight.indices) {
            layer.accumulatedWeight[i].fill(0.0)
        }
        layer.accumulatedBias.fill(0.0)
    }
}