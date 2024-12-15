package deeplearning.activation

import kotlin.math.max

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

    override fun backward(input: DoubleArray): DoubleArray {
        return input.map { if (it > 0.0) 1.0 else 0.0 }.toDoubleArray()
    }

    override fun backward(inputs: Array<DoubleArray>): Array<DoubleArray> {
        return inputs.map { dim1 ->
            dim1.map { dim2 ->
                if (dim2 > 0.0) 1.0 else 0.0
            }.toDoubleArray()
        }.toTypedArray()
    }
}