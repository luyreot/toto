package systems.deeplearning.optimization

import systems.deeplearning.model.LayerDense
import systems.deeplearning.util.Data.clipGradient

/**
 * Stochastic Gradient Descent
 */
object SGD : OptimizationFunction {

    override val type = OptimizationFunctionType.SGD

    fun optimize(layer: LayerDense, learningRate: Double) {
        for (i in layer.biases.indices) {
            for (j in layer.weights[i].indices) {
                layer.weights[i][j] -= learningRate * layer.accumulatedWeight[i][j]
            }
            layer.biases[i] -= learningRate * layer.accumulatedBias[i]
        }

        for (i in layer.accumulatedWeight.indices) {
            layer.accumulatedWeight[i].fill(0.0) // Reset weight gradients
        }
        layer.accumulatedBias.fill(0.0) // Reset bias gradients
    }

    fun optimizeWithClippedGradients(layer: LayerDense, learningRate: Double) {
        for (i in layer.biases.indices) {
            for (j in layer.weights[i].indices) {
                layer.weights[i][j] -= learningRate * clipGradient(layer.accumulatedWeight[i][j])
            }
            layer.biases[i] -= learningRate * clipGradient(layer.accumulatedBias[i])
        }

        for (i in layer.accumulatedWeight.indices) {
            layer.accumulatedWeight[i].fill(0.0) // Reset weight gradients
        }
        layer.accumulatedBias.fill(0.0) // Reset bias gradients
    }

    fun optimizeWithL2Regularization(
        layer: LayerDense,
        learningRate: Double,
        lambda: Double = 0.01
    ) {
        for (i in layer.biases.indices) {
            for (j in layer.weights[i].indices) {
                // Apply L2 regularization (weight decay) to the gradient
                val regularizedDWeight = layer.accumulatedWeight[i][j] + lambda * layer.weights[i][j]
                layer.weights[i][j] -= learningRate * regularizedDWeight
            }

            // Apply L2 regularization (weight decay) to the bias gradient (optional)
            val regularizedDBias = layer.accumulatedBias[i] + lambda * layer.biases[i]
            layer.biases[i] -= learningRate * regularizedDBias
        }

        for (i in layer.accumulatedWeight.indices) {
            layer.accumulatedWeight[i].fill(0.0) // Reset weight gradients
        }
        layer.accumulatedBias.fill(0.0) // Reset bias gradients
    }

    fun optimizeWithL2RegularizationAndClippedGradients(
        layer: LayerDense,
        learningRate: Double,
        lambda: Double = 0.01
    ) {
        for (i in layer.biases.indices) {
            for (j in layer.weights[i].indices) {
                // Apply L2 regularization (weight decay) to the gradient
                val regularizedDWeight = layer.accumulatedWeight[i][j] + lambda * layer.weights[i][j]
                layer.weights[i][j] -= learningRate * clipGradient(regularizedDWeight)
            }

            // Apply L2 regularization (weight decay) to the bias gradient (optional)
            val regularizedDBias = layer.accumulatedBias[i] + lambda * layer.biases[i]
            layer.biases[i] -= learningRate * clipGradient(regularizedDBias)
        }

        for (i in layer.accumulatedWeight.indices) {
            layer.accumulatedWeight[i].fill(0.0) // Reset weight gradients
        }
        layer.accumulatedBias.fill(0.0) // Reset bias gradients
    }
}