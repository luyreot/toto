package deeplearning.model

import deeplearning.activation.ActivationFunction
import deeplearning.activation.Sigmoid
import deeplearning.activation.Tanh
import deeplearning.util.Matrix.multiply

class LayerDense(
    override val tag: String,
    override val layerType: LayerType,
    override val biases: DoubleArray,
    override val weights: Array<DoubleArray>,
    override val activationFunction: ActivationFunction,
    override val activationFunctionDerivative: ActivationFunction,
    override val verifyInputs: Boolean = true,
    override var learningRate: Double = 0.0
) : Layer {

    private var input: DoubleArray = doubleArrayOf()
    private var inputs: Array<DoubleArray> = arrayOf(doubleArrayOf())

    var accumulatedWeight: Array<DoubleArray> = arrayOf(doubleArrayOf())
    var accumulatedBias: DoubleArray = doubleArrayOf()

    constructor(
        tag: String,
        layerType: LayerType,
        activationFunction: ActivationFunction,
        numNeurons: Int,
        numInputs: Int,
        weightInit: (x: Int, y: Int) -> Array<DoubleArray>
    ) : this(
        tag,
        layerType,
        DoubleArray(numNeurons) { 0.0 },
        weightInit(numNeurons, numInputs),
        activationFunction,
        activationFunction
    )

    init {
        if (verifyInputs) {
            require(biases.size == weights.size) {
                "Neurons (${biases.size}) and Weights (${weights.size}) have different sizes."
            }
            require(weights.all { it.size == weights[0].size }) {
                "Not all weights are of the same size.\n${weights.joinToString { "$it" }}"
            }
        }
        accumulatedWeight = Array(weights.size) { DoubleArray(weights[0].size) }
        accumulatedBias = DoubleArray(biases.size)
    }

    override fun forward(input: DoubleArray): DoubleArray {
        verifyInput(input)
        this.input = input
        val output = multiply(
            input = input,
            weights = weights,
            biases = biases
        )
        val activation = activationFunction.forward(output)
        return activation
    }

    override fun forward(inputs: Array<DoubleArray>): Array<DoubleArray> {
        verifyInputs(inputs)
        this.inputs = inputs
        val output = multiply(
            inputs = inputs,
            weights = weights,
            biases = biases
        )
        val activation = activationFunction.forward(output)
        return activation
    }

    override fun backward(lossGradient: DoubleArray): DoubleArray {
        // Compute activation gradient
        val activationGradient: DoubleArray = if (activationFunction is Sigmoid || activationFunction is Tanh) {
            lossGradient
                .zip(activationFunctionDerivative.backward(lossGradient)) { loss, act -> loss * act }
                .toDoubleArray()
        } else {
            activationFunctionDerivative.backward(lossGradient)
        }

        // Initialize arrays for the previous layer's deltas
        val prevDelta = DoubleArray(weights[0].size)

        // Update gradients for weights and biases
        for (n in biases.indices) {
            for (j in weights[n].indices) {
                // Accumulate weight gradient
                accumulatedWeight[n][j] += input[j] * activationGradient[n]
                prevDelta[j] += weights[n][j] * activationGradient[n]
            }

            // Accumulate bias gradient
            accumulatedBias[n] += activationGradient[n]
        }

        return prevDelta
    }

    override fun backward(lossGradients: Array<DoubleArray>): Array<DoubleArray> {
        val batchSize = inputs.size

        // Initialize gradient accumulators
        val weightGradients = Array(weights.size) { DoubleArray(weights[0].size) { 0.0 } }
        val biasGradients = DoubleArray(biases.size) { 0.0 }
        val prevDeltas = Array(batchSize) { DoubleArray(weights[0].size) { 0.0 } }

        // Loop through each input in the batch
        for (i in inputs.indices) {
            val input = inputs[i]
            val lossGradient = lossGradients[i]

            // Compute activation gradient for this data point
            val activationGradient: DoubleArray = if (activationFunction is Sigmoid || activationFunction is Tanh) {
                lossGradient
                    .zip(activationFunctionDerivative.backward(lossGradient)) { loss, act -> loss * act }
                    .toDoubleArray()
            } else {
                activationFunctionDerivative.backward(lossGradient)
            }

            // Compute weight & bias gradients
            for (n in biases.indices) {
                for (j in weights[n].indices) {
                    weightGradients[n][j] += input[j] * activationGradient[n]
                }
                biasGradients[n] += activationGradient[n]
            }

            // Compute previous deltas **before** averaging
            for (j in weights[0].indices) {
                for (n in biases.indices) {
                    prevDeltas[i][j] += weights[n][j] * activationGradient[n]
                }
            }
        }

        // **Average gradients over batch size**
        for (n in weightGradients.indices) {
            for (j in weightGradients[n].indices) {
                weightGradients[n][j] = weightGradients[n][j] / batchSize
            }
        }
        for (n in biasGradients.indices) {
            biasGradients[n] = biasGradients[n] / batchSize
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