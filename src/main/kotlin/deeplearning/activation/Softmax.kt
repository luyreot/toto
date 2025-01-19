package deeplearning.activation

import kotlin.math.exp

/**
 * Issues:
 * - explosion of values once the exponentiation grows. It is easy to get massive numbers
 * and reach overflow. To combat this take the largest number from the input prior to exponentiation
 * and subtract it from input value. The largest value will then be 0 and everything else will be less than 0.
 * The rage of possibilities now becomes somewhere between 0 and 1 after exponentiation.
 * The actual output in the end will be the same.
 *
 * In classification, the 'Categorical Cross-Entropy' loss function is used when soft max is used in the output layer.
 *
 * For backpropagation, the derivative is handled differently because it’s part of the cross-entropy loss optimization process.
 * Not that there is a combined implementation for backpropagation, i.e. softmax + cross-entropy as a single function.
 */
data class Softmax(private val overflowGuard: Boolean = true) : ActivationFunction {

    override fun forward(input: DoubleArray): DoubleArray {
        return softmax(input)
    }

    override fun forward(inputs: Array<DoubleArray>): Array<DoubleArray> {
        return softmaxBatch(inputs)
    }

    override fun backward(input: DoubleArray): DoubleArray {
        return softmaxDerivative(input)
    }

    override fun backward(inputs: Array<DoubleArray>): Array<DoubleArray> {
        return softmaxDerivativeBatch(inputs)
    }

    private fun softmax(input: DoubleArray): DoubleArray {
        val inputNew: DoubleArray = input.copyOf()

        if (overflowGuard) {
            val largestValue: Double = inputNew.maxOrNull() ?: 0.0
            for (i in inputNew.indices) {
                inputNew[i] = inputNew[i] - largestValue
            }
        }

        val expInput = inputNew.map { exp(it) }
        val product = expInput.sum()
        val normalizedInput = expInput.map { it / product }.toDoubleArray()

        return normalizedInput
    }

    private fun softmaxBatch(inputs: Array<DoubleArray>): Array<DoubleArray> {
        return inputs.map { softmax(it) }.toTypedArray()
    }

    private fun softmaxDerivative(input: DoubleArray): DoubleArray {
        val softmax = softmax(input)
        // Compute the diagonal terms of the Jacobian matrix
        return input.indices.map { i ->
            softmax[i] * (1 - softmax[i])
        }.toDoubleArray()
    }

    private fun softmaxDerivativeBatch(inputs: Array<DoubleArray>): Array<DoubleArray> {
        return inputs.map { softmaxDerivative(it) }.toTypedArray()
    }
}