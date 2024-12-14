package deeplearning

import kotlin.math.max

sealed interface ActivationFunctions {

    /**
     * ReLU - Rectified Linear Unit
     * Introduces the property of nonlinearity to a deep learning model
     * and solves the vanishing gradients issue.
     * It interprets the positive part of its argument.
     * It is one of the most popular activation functions in deep learning.
     */
    data object ReLU : ActivationFunctions {

        fun forward(input: DoubleArray): DoubleArray {
            return input.map { max(0.0, it) }.toDoubleArray()
        }

        fun forward(inputs: Array<DoubleArray>): Array<DoubleArray> {
            return inputs.map { dim1 -> dim1.map { dim2 -> max(0.0, dim2) }.toDoubleArray() }.toTypedArray()
        }
    }
}