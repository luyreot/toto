package deeplearning.optimization

import deeplearning.model.LayerDense
import deeplearning.util.Data.clipGradient

/**
 * Stochastic Gradient Descent
 */
object SGD : OptimizationFunction {

    override val type = OptimizationFunctionType.SGD

    fun optimize(layer: LayerDense, learningRate: Double) {
        for (i in layer.neurons.indices) {
            for (j in layer.weights[i].indices) {
                layer.weights[i][j] -= learningRate * layer.accumulatedWeight[i][j]
            }
            layer.neurons[i].bias -= learningRate * layer.accumulatedBias[i]
        }

        for (i in layer.accumulatedWeight.indices) {
            layer.accumulatedWeight[i].fill(0.0) // Reset weight gradients
        }
        layer.accumulatedBias.fill(0.0) // Reset bias gradients
    }

    fun optimizeWithClippedGradients(layer: LayerDense, learningRate: Double) {
        for (i in layer.neurons.indices) {
            for (j in layer.weights[i].indices) {
                layer.weights[i][j] -= learningRate * clipGradient(layer.accumulatedWeight[i][j])
            }
            layer.neurons[i].bias -= learningRate * clipGradient(layer.accumulatedBias[i])
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
        for (i in layer.neurons.indices) {
            for (j in layer.weights[i].indices) {
                // Apply L2 regularization (weight decay) to the gradient
                val regularizedDWeight = layer.accumulatedWeight[i][j] + lambda * layer.weights[i][j]
                layer.weights[i][j] -= learningRate * regularizedDWeight
            }

            // Apply L2 regularization (weight decay) to the bias gradient (optional)
            val regularizedDBias = layer.accumulatedBias[i] + lambda * layer.neurons[i].bias
            layer.neurons[i].bias -= learningRate * regularizedDBias
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
        for (i in layer.neurons.indices) {
            for (j in layer.weights[i].indices) {
                // Apply L2 regularization (weight decay) to the gradient
                val regularizedDWeight = layer.accumulatedWeight[i][j] + lambda * layer.weights[i][j]
                layer.weights[i][j] -= learningRate * clipGradient(regularizedDWeight)
            }

            // Apply L2 regularization (weight decay) to the bias gradient (optional)
            val regularizedDBias = layer.accumulatedBias[i] + lambda * layer.neurons[i].bias
            layer.neurons[i].bias -= learningRate * clipGradient(regularizedDBias)
        }

        for (i in layer.accumulatedWeight.indices) {
            layer.accumulatedWeight[i].fill(0.0) // Reset weight gradients
        }
        layer.accumulatedBias.fill(0.0) // Reset bias gradients
    }
}