package deeplearning.optimization

/**
 * Optimization algorithms to update weights and biases during training.
 */
interface OptimizationFunction {
    val type: OptimizationFunctionType
}