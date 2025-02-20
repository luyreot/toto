package systems.deeplearning.util

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
            var sum = biases[j] // Start with bias
            // Dot product of input and weights[j]
            for (i in input.indices) {
                sum += input[i] * weights[j][i]
            }
            output[j] = sum
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
        require(inputs.all { it.size == weights[0].size }) {
            "All input vectors must have the same size as weight vectors"
        }

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