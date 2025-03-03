package systems.deeplearning.optimization

import systems.deeplearning.model.LayerDense
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * TODO - Optimization
 * Incorporate a learning rate schedule or decay over time (e.g., exponential decay, step decay, etc.),
 * which is often used in practice to improve convergence - learningRate *= decayFactor.pow(t)
 *
 * Handling of Negative Gradients:
 * The code appears to correctly handle updates for both positive and negative gradients,
 * but just be cautious that if any gradients happen to be extremely large or have outliers,
 * the learning rate and sqrt(vBCorrected) could cause instability.
 * Monitoring the training process and implementing gradient clipping (which you've already implemented) could help.
 *
 * Epsilon Stability:
 * Using epsilon = 1e-8, which is standard for numerical stability.
 * However, it’s always good to experiment with different values of epsilon depending on the specific scale of your weights, gradients, and learning rate.
 *
 * TODO:
 * - add caching/restoring of parameters
 */
class Adam(
    val beta1: Double = 0.9,  // First moment decay rate
    val beta2: Double = 0.999, // Second moment decay rate
    val epsilon: Double = 1e-8, // Small constant to prevent division by zero
    var learningRate: Double = 0.001
) : OptimizationFunction {

    override val type: OptimizationFunctionType = OptimizationFunctionType.Adam

    // First moment estimate (weights), running average of the gradients.
    private var mWeights: Array<DoubleArray>

    // Second moment estimate (weights), running average of the squared gradients.
    private var vWeights: Array<DoubleArray>

    // First moment estimate (biases), running average of the gradients.
    private var mBiases: DoubleArray

    // Second moment estimate (biases), running average of the squared gradients.
    private var vBiases: DoubleArray

    // Time step counter
    private var t = 0

    init {
        mWeights = Array(0) { DoubleArray(0) }
        vWeights = Array(0) { DoubleArray(0) }
        mBiases = DoubleArray(0)
        vBiases = DoubleArray(0)
    }

    fun optimize(layer: LayerDense) {
        // Initialize moment estimates for this layer
        if (mWeights.size != layer.weights.size || mWeights[0].size != layer.weights[0].size) {
            mWeights = Array(layer.weights.size) { DoubleArray(layer.weights[0].size) }
            vWeights = Array(layer.weights.size) { DoubleArray(layer.weights[0].size) }
        }
        if (mBiases.size != layer.biases.size) {
            mBiases = DoubleArray(layer.biases.size)
            vBiases = DoubleArray(layer.biases.size)
        }

        // Increment time step only once per parameter update across layers
        t += 1

        // Update weights and biases
        for (i in layer.biases.indices) {
            val gradB = layer.accumulatedBias[i]

            // Update biased first and second moment estimates
            mBiases[i] = beta1 * mBiases[i] + (1 - beta1) * gradB
            vBiases[i] = beta2 * vBiases[i] + (1 - beta2) * gradB * gradB

            // Compute bias-corrected estimates
            val mBCorrected = mBiases[i] / (1 - beta1.pow(t))
            val vBCorrected = vBiases[i] / (1 - beta2.pow(t))

            // Update bias
            layer.biases[i] -= learningRate * mBCorrected / (sqrt(vBCorrected) + epsilon)

            for (j in layer.weights[i].indices) {
                val gradW = layer.accumulatedWeight[i][j]

                // Update biased first and second moment estimates for weights
                mWeights[i][j] = beta1 * mWeights[i][j] + (1 - beta1) * gradW
                vWeights[i][j] = beta2 * vWeights[i][j] + (1 - beta2) * gradW * gradW

                // Compute bias-corrected estimates for weights
                val mWCorrected = mWeights[i][j] / (1 - beta1.pow(t))
                val vWCorrected = vWeights[i][j] / (1 - beta2.pow(t))

                // Apply weight updates
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