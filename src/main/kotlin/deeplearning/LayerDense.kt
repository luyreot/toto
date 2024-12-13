package deeplearning

import deeplearning.Matrix.multiply

class LayerDense(
    val neurons: Array<Neuron>,
    val weights: Array<Array<Weight>>,
    private val verifyArrays: Boolean = true
) : Layer {

    constructor(
        neurons: Array<Neuron>,
        weights: Array<Weight>,
        verifyArrays: Boolean = true
    ) : this(neurons, arrayOf(weights), verifyArrays)

    init {
        if (verifyArrays) {
            require(neurons.size == weights.size) {
                "Neurons (${neurons.size}) and Weights (${weights.size}) have different sizes."
            }
            require(weights.all { it.size == weights[0].size }) {
                "Not all weights are of the same size.\n${weights.joinToString { "$it" }}"
            }
        }
    }

    override fun forward(input: DoubleArray): DoubleArray {
        verifyInputSize(input)
        return multiply(
            input = input,
            weights = weights.map { dim1 -> dim1.map { dim2 -> dim2.weight }.toDoubleArray() }.toTypedArray(),
            biases = neurons.map { it.bias.bias }.toDoubleArray()
        )
    }

    override fun forward(inputs: Array<DoubleArray>): Array<DoubleArray> {
        verifyInputSize(inputs)
        return multiply(
            inputs = inputs,
            weights = weights.map { dim1 -> dim1.map { dim2 -> dim2.weight }.toDoubleArray() }.toTypedArray(),
            biases = neurons.map { it.bias.bias }.toDoubleArray()
        )
    }

    private fun verifyInputSize(input: DoubleArray) {
        if (verifyArrays) {
            require(input.size == weights[0].size) {
                "Inputs (${input.size}) and Weights (${weights[0].size}) have different sizes."
            }
        }
    }

    private fun verifyInputSize(inputs: Array<DoubleArray>) {
        if (verifyArrays) {
            inputs.forEachIndexed { index, input ->
                require(input.size == weights[0].size) {
                    "Inputs (${input.size}) at index $index and Weights (${weights[0].size}) have different sizes."
                }
            }
        }
    }
}