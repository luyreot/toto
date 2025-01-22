package deeplearning.optimization

/**
 * Optimization algorithms to update weights and biases during training.
 * TODO
 */
object OptimizationFunctions {

    /**
     * Stochastic Gradient Descent (SGD)
     *
     * Add momentum or adaptive learning rates for more advanced optimization (e.g., Adam).
     */
    fun sgdUpdate(
        weights: Array<DoubleArray>,
        biases: DoubleArray,
        gradients: Array<DoubleArray>,
        learningRate: Double
    ) {
        for (i in weights.indices) {
            for (j in weights[i].indices) {
                weights[i][j] -= learningRate * gradients[i][j]
            }
            biases[i] -= learningRate * gradients[i].sum()
        }
    }

    fun sgdUpdate(
        weights: Array<DoubleArray>,
        biases: DoubleArray,
        gradients: Pair<Array<DoubleArray>, DoubleArray>,
        learningRate: Double
    ) {
        val (weightGradients, biasGradients) = gradients
        for (i in weights.indices) {
            for (j in weights[i].indices) {
                weights[i][j] -= learningRate * weightGradients[i][j]
            }
            biases[i] -= learningRate * biasGradients[i]
        }
    }
}