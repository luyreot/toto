package deeplearning.model

import deeplearning.LEARNING_RATE
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

    private var input: DoubleArray = doubleArrayOf()
    private var inputs: Array<DoubleArray> = arrayOf(doubleArrayOf())

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
        this.input = input
        val output = multiply(
            input = input,
            weights = weights,
            biases = neurons.map { it.bias }.toDoubleArray()
        )
        return activationFunction.forward(output)
    }

    override fun forward(inputs: Array<DoubleArray>): Array<DoubleArray> {
        verifyInputs(inputs)
        this.inputs = inputs
        val output = multiply(
            inputs = inputs,
            weights = weights,
            biases = neurons.map { it.bias }.toDoubleArray()
        )
        return activationFunction.forward(output)
    }

    override fun backward(lossGradient: DoubleArray): DoubleArray {
        // Compute activation gradient
        val activationGradient = activationFunctionDerivative.backward(lossGradient)

        // Initialize arrays for the previous layer's deltas
        val prevDelta = DoubleArray(weights[0].size)

        // Update gradients for weights and biases
        for (n in neurons.indices) {
            for (j in weights[n].indices) {
                weights[n][j] -= input[j] * activationGradient[n] * LEARNING_RATE
                prevDelta[j] += weights[n][j] * activationGradient[n]
            }
            neurons[n].bias -= activationGradient[n] * LEARNING_RATE
        }

        return prevDelta
    }

    override fun backward(lossGradients: Array<DoubleArray>): Array<DoubleArray> {
        val batchSize = inputs.size

        // Initialize gradient accumulators
        val weightGradients = Array(weights.size) { DoubleArray(weights[0].size) }
        val biasGradients = DoubleArray(neurons.size)
        val prevDeltas = Array(batchSize) { DoubleArray(weights[0].size) }

        // Loop through each input in the batch
        for (i in inputs.indices) {
            val input = inputs[i]
            val lossGradient = lossGradients[i]

            // Compute activation gradient for this data point
            val activationGradient = activationFunctionDerivative.backward(lossGradient)

            // Update gradients for weights and biases
            for (n in neurons.indices) {
                for (j in weights[n].indices) {
                    weightGradients[n][j] += input[j] * activationGradient[n]
                }
                biasGradients[n] += activationGradient[n]
            }

            // Compute previous deltas for this data point
            for (n in neurons.indices) {
                for (j in weights[n].indices) {
                    prevDeltas[i][j] += weights[n][j] * activationGradient[n]
                }
            }
        }

        // Average gradients over the batch
        for (n in weightGradients.indices) {
            for (j in weightGradients[n].indices) {
                weightGradients[n][j] = weightGradients[n][j] / batchSize
            }
        }
        for (n in biasGradients.indices) {
            biasGradients[n] = biasGradients[n] / batchSize
        }

        // Update weights and biases
        for (n in neurons.indices) {
            for (j in weights[n].indices) {
                weights[n][j] -= weightGradients[n][j] * LEARNING_RATE
            }
            neurons[n].bias -= biasGradients[n] * LEARNING_RATE
        }

        return prevDeltas
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