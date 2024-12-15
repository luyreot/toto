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
 */
class Softmax(val overflowGuard: Boolean = true) : ForwardPropagationFunction {

    override fun forward(input: DoubleArray): DoubleArray {
        var inputNew: List<Double> = input.asList()
        if (overflowGuard) {
            val largestValue = input.max()
            inputNew = input.map { it - largestValue }
        }

        val expInput = inputNew.map { exp(it) }
        val product = expInput.sum()
        val normalizedInput = expInput.map { it / product }.toDoubleArray()
        return normalizedInput
    }

    override fun forward(inputs: Array<DoubleArray>): Array<DoubleArray> {
        var inputsNew: List<List<Double>> = inputs.map { it.asList() }
        if (overflowGuard) {
            val largestValues = inputsNew.map { it.max() }
            inputsNew = inputsNew.mapIndexed { dim1Index, dim1 ->
                dim1.map { dim2 -> dim2 - largestValues[dim1Index] }
            }
        }

        val expInput = inputsNew.map { dim1 -> dim1.map { dim2 -> exp(dim2) } }
        val products = expInput.map { it.sum() }
        val normalizedInput = expInput.mapIndexed { dim1Index, dim1 ->
            dim1.map { dim2 -> dim2 / products[dim1Index] }
                .toDoubleArray()
        }.toTypedArray()
        return normalizedInput
    }
}