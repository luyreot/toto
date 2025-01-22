package deeplearning.activation

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
        return input.map { relu(it) }.toDoubleArray()
    }

    override fun forward(inputs: Array<DoubleArray>): Array<DoubleArray> {
        return inputs.map { dim1 -> dim1.map { dim2 -> relu(dim2) }.toDoubleArray() }.toTypedArray()
    }

    override fun backward(input: DoubleArray): DoubleArray {
        return input.map { reluDerivative(it) }.toDoubleArray()
    }

    override fun backward(inputs: Array<DoubleArray>): Array<DoubleArray> {
        return inputs.map { dim1 -> dim1.map { dim2 -> reluDerivative(dim2) }.toDoubleArray() }.toTypedArray()
    }

    // Math

    private fun relu(x: Double): Double = max(0.0, x)

    private fun reluDerivative(x: Double): Double = if (x > 0.0) 1.0 else 0.0
}