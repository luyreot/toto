package systems.deeplearning.activation

import kotlin.math.max

/**
 * ReLU - Rectified Linear Unit
 * Granular - can optimize well with it.
 * Introduces the property of nonlinearity to a deep learning model
 * and solves the vanishing gradients issue.
 * It interprets the positive part of its argument.
 * It is one of the most popular activation functions in deep learning.
 *
 * Good to use in hidden layers.
 *
 * Issues:
 * - 0 or negative values will be lost during loss optimization
 */
data object ReLU : ActivationFunction {

    override val type: ActivationFunctionType = ActivationFunctionType.ReLU

    override fun forward(input: DoubleArray): DoubleArray {
        return DoubleArray(input.size) { i -> relu(input[i]) }
    }

    override fun forward(inputs: Array<DoubleArray>): Array<DoubleArray> {
        return Array(inputs.size) { i -> DoubleArray(inputs[i].size) { j -> relu(inputs[i][j]) } }
    }

    override fun backward(input: DoubleArray): DoubleArray {
        return DoubleArray(input.size) { i -> reluDerivative(input[i]) }
    }

    override fun backward(inputs: Array<DoubleArray>): Array<DoubleArray> {
        return Array(inputs.size) { i -> DoubleArray(inputs[i].size) { j -> reluDerivative(inputs[i][j]) } }
    }

    // Math

    private fun relu(x: Double): Double = max(0.0, x)

    private fun reluDerivative(x: Double): Double = if (x > 0.0) 1.0 else 0.0
}