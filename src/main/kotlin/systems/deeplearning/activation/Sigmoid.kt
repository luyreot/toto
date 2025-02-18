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