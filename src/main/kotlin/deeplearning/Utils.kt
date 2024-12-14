package deeplearning

import deeplearning.Math.factorial
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.pow

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

object Matrix {

    /**
     * For single array inputs.
     */
    fun multiply(
        input: DoubleArray,
        weights: Array<DoubleArray>,
        biases: DoubleArray
    ): DoubleArray {
        val numNeurons = weights.size // Number of neurons
        val output = DoubleArray(numNeurons)

        for (j in 0 until numNeurons) { // Iterate over each neuron
            // Dot product of input and weights[j]
            output[j] = input.zip(weights[j]) { inputVal, weightVal ->
                inputVal * weightVal
            }.sum() + biases[j] // Add bias
        }

        return output
    }

    /**
     * For batch inputs.
     */
    fun multiply(
        inputs: Array<DoubleArray>,
        weights: Array<DoubleArray>,
        biases: DoubleArray
    ): Array<DoubleArray> {
        return inputs.map { input ->
            multiply(input, weights, biases)
        }.toTypedArray()
    }

    fun transpose(matrix: Array<DoubleArray>): Array<DoubleArray> {
        val rows = matrix.size
        val cols = matrix[0].size
        val transposed = Array(cols) { DoubleArray(rows) }
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                transposed[j][i] = matrix[i][j]
            }
        }
        return transposed
    }
}

object Math {

    fun factorial(n: Int): Double = if (n == 0) 1.0 else n * factorial(n - 1)
}

object Statistics {

    /**
     * λ (lambda): The average rate of appearance for the number over a set of past draws (e.g., its frequency in the last 50 draws).
     * k: The expected occurrences in a single draw. For simplicity, we usually set k = 1, k=1 (the number appears once in the draw).
     */
    fun calculatePoissonProbability(frequency: Double, k: Int = 1): Double {
        return (frequency.pow(k) * exp(-frequency)) / factorial(k)
    }
}

object Data {

    fun calculateGapSinceLast(number: Int, draws: List<List<Int>>, drawIndex: Int): Int {
        for (i in drawIndex - 1 downTo 0) {
            if (number in draws[i]) {
                return drawIndex - i
            }
        }
        // The number has never appeared
        return -1
    }
}