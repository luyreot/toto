package systems.deeplearning.activation

import kotlin.math.exp

/**
 * Granular - can optimize well with it.
 * Has an issue - vanishing gradient problem.
 *
 * Sigmoid is appropriate when your output layer has a single neuron,
 * and we're solving a binary classification problem where each input belongs to one of two classes.
 * Sigmoid squashes the output into the range [0, 1],
 * representing the probability of belonging to the positive class (class 1).
 */
data object Sigmoid : ActivationFunction {

    override val type: ActivationFunctionType = ActivationFunctionType.Sigmoid

    override fun forward(input: DoubleArray): DoubleArray {
        return DoubleArray(input.size) { i -> sigmoid(input[i]) }
    }

    override fun forward(inputs: Array<DoubleArray>): Array<DoubleArray> {
        return Array(inputs.size) { i -> DoubleArray(inputs[i].size) { j -> sigmoid(inputs[i][j]) } }
    }

    override fun backward(input: DoubleArray): DoubleArray {
        return DoubleArray(input.size) { i -> sigmoidDerivative(input[i]) }
    }

    override fun backward(inputs: Array<DoubleArray>): Array<DoubleArray> {
        return Array(inputs.size) { i -> DoubleArray(inputs[i].size) { j -> sigmoidDerivative(inputs[i][j]) } }
    }

    // Math

    /**
     * The sigmoid function can cause overflow or underflow due to exp(-x).
     * For large positive x, exp(-x) is very small, and 1.0 + exp(-x) may lose precision.
     * For large negative x, exp(-x) grows very large, leading to potential floating-point errors.
     */
    private fun sigmoid(x: Double): Double {
        if (x >= 0) {
            val result = exp(-x)
            return 1.0 / (1.0 + result) // More stable when x is large
        }

        val result = exp(x)
        return result / (1.0 + result) // More stable when x is very negative
    }

    /**
     * TODO - Optimization
     * - If sigmoid(x) is already computed during the forward pass, store it in the Layer and reuse it during backpropagation.
     * Instead of recomputing sigmoid(x) during the backward pass, modify the function to take sigmoid(x) as input.
     */
    private fun sigmoidDerivative(x: Double): Double {
        val sig = sigmoid(x)
        return sig * (1 - sig)
    }
}