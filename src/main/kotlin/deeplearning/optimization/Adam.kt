package deeplearning.optimization

import deeplearning.model.LayerDense
import kotlin.math.pow
import kotlin.math.sqrt

class Adam(
    // Exponential decay rate for the first moment
    val beta1: Double = 0.9,
    // Exponential decay rate for the second moment
    val beta2: Double = 0.999,
    // Small constant to prevent division by zero
    val epsilon: Double = 1e-8,
    val learningRate: Double = 0.001
) : OptimizationFunction {

    override val type: OptimizationFunctionType = OptimizationFunctionType.Adam

    private var mWeights = mutableMapOf<Pair<Int, Int>, Double>()
    private var vWeights = mutableMapOf<Pair<Int, Int>, Double>()
    private var mBiases = mutableMapOf<Int, Double>()
    private var vBiases = mutableMapOf<Int, Double>()
    private var t = 0

    fun optimize(layer: LayerDense) {
        t += 1 // Time step

        for (i in layer.biases.indices) {
            val gradB = layer.accumulatedBias[i]
            mBiases[i] = beta1 * (mBiases[i] ?: 0.0) + (1 - beta1) * gradB
            vBiases[i] = beta2 * (vBiases[i] ?: 0.0) + (1 - beta2) * gradB * gradB

            val mBCorrected = mBiases[i]!! / (1 - beta1.pow(t))
            val vBCorrected = vBiases[i]!! / (1 - beta2.pow(t))

            layer.biases[i] -= learningRate * mBCorrected / (sqrt(vBCorrected) + epsilon)

            for (j in layer.weights[i].indices) {
                val key = Pair(i, j)

                val gradW = layer.accumulatedWeight[i][j]

                // Update biased first moment estimate
                mWeights[key] = beta1 * (mWeights[key] ?: 0.0) + (1 - beta1) * gradW

                // Update biased second moment estimate
                vWeights[key] = beta2 * (vWeights[key] ?: 0.0) + (1 - beta2) * gradW * gradW

                // Compute bias-corrected estimates
                val mWCorrected = mWeights[key]!! / (1 - beta1.pow(t))
                val vWCorrected = vWeights[key]!! / (1 - beta2.pow(t))

                // Apply updates
                layer.weights[i][j] -= learningRate * mWCorrected / (sqrt(vWCorrected) + epsilon)
            }
        }

        // Reset accumulated gradients
        for (i in layer.accumulatedWeight.indices) {
            layer.accumulatedWeight[i].fill(0.0)
        }
        layer.accumulatedBias.fill(0.0)
    }
}