package deeplearning

import deeplearning.Matrix.multiply
import deeplearning.activation.ActivationFunction
import deeplearning.activation.BackwardPropagationFunction
import deeplearning.activation.ForwardPropagationFunction

class LayerDense(
    val neurons: Array<Neuron>,
    val weights: Array<Array<Weight>>,
    override val activationFunction: ForwardPropagationFunction,
    override val activationFunctionDerivative: BackwardPropagationFunction,
    private val verifyLayerData: Boolean = true
) : Layer {

    constructor(
        neurons: Array<Neuron>,
        weights: Array<Array<Weight>>,
        activationFunction: ActivationFunction,
        verifyArrays: Boolean = true
    ) : this(neurons, weights, activationFunction, activationFunction, verifyArrays)

    init {
        if (verifyLayerData) {
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
        val output = multiply(
            input = input,
            weights = weights.map { dim1 -> dim1.map { dim2 -> dim2.weight }.toDoubleArray() }.toTypedArray(),
            biases = neurons.map { it.bias.bias }.toDoubleArray()
        )
        return activationFunction.forward(output)
    }

    override fun forward(inputs: Array<DoubleArray>): Array<DoubleArray> {
        verifyInputSize(inputs)
        val output = multiply(
            inputs = inputs,
            weights = weights.map { dim1 -> dim1.map { dim2 -> dim2.weight }.toDoubleArray() }.toTypedArray(),
            biases = neurons.map { it.bias.bias }.toDoubleArray()
        )
        return activationFunction.forward(output)
    }

    override fun backward(input: DoubleArray): DoubleArray {
        TODO("Not yet implemented")
    }

    override fun backward(inputs: Array<DoubleArray>): Array<DoubleArray> {
        TODO("Not yet implemented")
    }

    private fun verifyInputSize(input: DoubleArray) {
        if (verifyLayerData) {
            require(input.size == weights[0].size) {
                "Inputs (${input.size}) and Weights (${weights[0].size}) have different sizes."
            }
        }
    }

    private fun verifyInputSize(inputs: Array<DoubleArray>) {
        if (verifyLayerData) {
            inputs.forEachIndexed { index, input ->
                require(input.size == weights[0].size) {
                    "Inputs (${input.size}) at index $index and Weights (${weights[0].size}) have different sizes."
                }
            }
        }
    }
}