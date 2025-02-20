package systems.deeplearning.activation

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
data object Softmax : ActivationFunction {

    override val type: ActivationFunctionType = ActivationFunctionType.Softmax

    override fun forward(input: DoubleArray): DoubleArray {
        return softmax(input)
    }

    override fun forward(inputs: Array<DoubleArray>): Array<DoubleArray> {
        return softmaxBatch(inputs)
    }

    override fun backward(input: DoubleArray): DoubleArray {
        TODO("Use backward2 method.")
    }

    override fun backward(inputs: Array<DoubleArray>): Array<DoubleArray> {
        TODO("Use backward2 method.")
    }

    fun backward2(input: DoubleArray): Array<DoubleArray> {
        return softmaxDerivative(input)
    }

    fun backward2(inputs: Array<DoubleArray>): Array<Array<DoubleArray>> {
        return softmaxDerivativeBatch(inputs)
    }

    private fun softmax(input: DoubleArray): DoubleArray {
        val maxVal = input.maxOrNull() ?: 0.0
        val expInput = input.map { exp(it - maxVal) }
        val sumExp = expInput.sum()
        return expInput.map { it / sumExp }.toDoubleArray()
    }

    private fun softmaxBatch(inputs: Array<DoubleArray>): Array<DoubleArray> {
        return inputs.map { softmax(it) }.toTypedArray()
    }

    /**
     * This Jacobian matrix represents the gradient of each output with respect to each input,
     * and it's especially important for multi-class classification problems
     * where the softmax output is a probability distribution over multiple classes.
     */
    private fun softmaxDerivative(input: DoubleArray): Array<DoubleArray> {
        val softmaxOutput = softmax(input)  // Compute softmax first
        val size = softmaxOutput.size

        val jacobian = Array(size) { i ->
            DoubleArray(size) { j ->
                if (i == j) {
                    softmaxOutput[i] * (1 - softmaxOutput[i])  // Diagonal: S_i * (1 - S_i)
                } else {
                    -softmaxOutput[i] * softmaxOutput[j]  // Off-diagonal: -S_i * S_j
                }
            }
        }

        return jacobian
    }

    private fun softmaxDerivativeBatch(inputs: Array<DoubleArray>): Array<Array<DoubleArray>> {
        return inputs.map { softmaxDerivative(it) }.toTypedArray()
    }
}