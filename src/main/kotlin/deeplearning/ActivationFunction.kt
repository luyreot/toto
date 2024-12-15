package deeplearning

import kotlin.math.exp
import kotlin.math.max

sealed interface ActivationFunction {

    fun forward(input: DoubleArray): DoubleArray

    fun forward(inputs: Array<DoubleArray>): Array<DoubleArray>

    /**
     * ReLU - Rectified Linear Unit
     * Introduces the property of nonlinearity to a deep learning model
     * and solves the vanishing gradients issue.
     * It interprets the positive part of its argument.
     * It is one of the most popular activation functions in deep learning.
     *
     * Issues:
     * - 0 or negative values will be lost during loss optimization
     */
    data object ReLU : ActivationFunction {

        override fun forward(input: DoubleArray): DoubleArray {
            return input.map { max(0.0, it) }.toDoubleArray()
        }

        override fun forward(inputs: Array<DoubleArray>): Array<DoubleArray> {
            return inputs.map { dim1 -> dim1.map { dim2 -> max(0.0, dim2) }.toDoubleArray() }.toTypedArray()
        }
    }

    data object ReLUDerivative : ActivationFunction {

        override fun forward(input: DoubleArray): DoubleArray {
            return input.map { if (it > 0.0) 1.0 else 0.0 }.toDoubleArray()
        }

        override fun forward(inputs: Array<DoubleArray>): Array<DoubleArray> {
            return inputs.map { dim1 ->
                dim1.map { dim2 ->
                    if (dim2 > 0.0) 1.0 else 0.0
                }.toDoubleArray()
            }.toTypedArray()
        }
    }

    data object Sigmoid : ActivationFunction {

        override fun forward(input: DoubleArray): DoubleArray {
            return input.map { Math.sigmoid(it) }.toDoubleArray()
        }

        override fun forward(inputs: Array<DoubleArray>): Array<DoubleArray> {
            return inputs.map { dim1 -> dim1.map { dim2 -> Math.sigmoid(dim2) }.toDoubleArray() }.toTypedArray()
        }
    }

    data object SigmoidDerivative : ActivationFunction {

        override fun forward(input: DoubleArray): DoubleArray {
            return input.map { Math.sigmoidDerivative(it) }.toDoubleArray()
        }

        override fun forward(inputs: Array<DoubleArray>): Array<DoubleArray> {
            return inputs.map { dim1 ->
                dim1.map { dim2 ->
                    Math.sigmoidDerivative(dim2)
                }.toDoubleArray()
            }.toTypedArray()
        }
    }

    /**
     * Issues:
     * - explosion of values once the exponentiation grows. It is easy to get massive numbers
     * and reach overflow. To combat this take the largest number from the input prior to exponentiation
     * and subtract it from input value. The largest value will then be 0 and everything else will be less than 0.
     * The rage of possibilities now becomes somewhere between 0 and 1 after exponentiation.
     * The actual output in the end will be the same.
     *
     * In classification, the 'Categorical Cross-Entropy' loss function is used when soft max is used in the output layer.
     */
    data class Softmax(val overflowGuard: Boolean = true) : ActivationFunction {

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
}