package deeplearning.model

import deeplearning.activation.ActivationFunction
import deeplearning.activation.BackwardPropagationFunction
import deeplearning.activation.ForwardPropagationFunction
import deeplearning.util.Matrix.multiply

class LayerDense(
    val neurons: Array<Neuron>,
    val weights: Array<DoubleArray>,
    override val activationFunction: ForwardPropagationFunction,
    override val activationFunctionDerivative: BackwardPropagationFunction,
    private val verifyInputs: Boolean = true
) : Layer {

    constructor(
        neurons: Array<Neuron>,
        weights: Array<DoubleArray>,
        activationFunction: ActivationFunction,
        verifyArrays: Boolean = true
    ) : this(neurons, weights, activationFunction, activationFunction, verifyArrays)

    init {
        if (verifyInputs) {
            require(neurons.size == weights.size) {
                "Neurons (${neurons.size}) and Weights (${weights.size}) have different sizes."
            }
            require(weights.all { it.size == weights[0].size }) {
                "Not all weights are of the same size.\n${weights.joinToString { "$it" }}"
            }
        }
    }

    override fun forward(input: DoubleArray): DoubleArray {
        verifyInput(input)
        val output = multiply(
            input = input,
            weights = weights,
            biases = neurons.map { it.bias }.toDoubleArray()
        )
        return activationFunction.forward(output)
    }

    override fun forward(inputs: Array<DoubleArray>): Array<DoubleArray> {
        verifyInputs(inputs)
        val output = multiply(
            inputs = inputs,
            weights = weights,
            biases = neurons.map { it.bias }.toDoubleArray()
        )
        return activationFunction.forward(output)
    }

    override fun backward(input: DoubleArray): DoubleArray {
        TODO("Not yet implemented")
    }

    override fun backward(inputs: Array<DoubleArray>): Array<DoubleArray> {
        TODO("Not yet implemented")
    }

    private fun verifyInput(input: DoubleArray) {
        if (!verifyInputs) return

        weights.forEachIndexed { index, weights ->
            require(input.size == weights.size) {
                "Inputs (${input.size}) and Weights (${weights.size}) have different sizes. Index $index."
            }
        }
    }

    private fun verifyInputs(inputs: Array<DoubleArray>) {
        if (!verifyInputs) return

        inputs.forEachIndexed { inputsIndex, inputsArr ->
            weights.forEachIndexed { weightsIndex, weights ->
                require(inputsArr.size == weights.size) {
                    "Inputs (${inputsArr.size}) and Weights (${weights.size}) have different sizes. Weights Index $weightsIndex. Inputs Index $inputsIndex"
                }
            }
        }
    }
}