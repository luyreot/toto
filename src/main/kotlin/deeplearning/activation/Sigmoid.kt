package deeplearning.activation

import kotlin.math.exp

/**
 * Granular - can optimize well with it.
 * Has an issue - vanishing gradient problem.
 */
data object Sigmoid : ActivationFunction {

    override val type: ActivationFunctionType = ActivationFunctionType.Sigmoid

    override fun forward(input: DoubleArray): DoubleArray {
        return input.map { sigmoid(it) }.toDoubleArray()
    }

    override fun forward(inputs: Array<DoubleArray>): Array<DoubleArray> {
        return inputs.map { dim1 -> dim1.map { dim2 -> sigmoid(dim2) }.toDoubleArray() }.toTypedArray()
    }

    override fun backward(input: DoubleArray): DoubleArray {
        return input.map { sigmoidDerivative(it) }.toDoubleArray()
    }

    override fun backward(inputs: Array<DoubleArray>): Array<DoubleArray> {
        return inputs.map { dim1 -> dim1.map { dim2 -> sigmoidDerivative(dim2) }.toDoubleArray() }.toTypedArray()
    }

    // Math

    private fun sigmoid(x: Double): Double = 1.0 / (1.0 + exp(-x))

    private fun sigmoidDerivative(x: Double): Double {
        val sig = sigmoid(x)
        return sig * (1 - sig)
    }
}