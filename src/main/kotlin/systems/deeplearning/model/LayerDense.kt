package systems.deeplearning.model

import systems.deeplearning.activation.ActivationFunction
import systems.deeplearning.activation.Sigmoid
import systems.deeplearning.activation.Softmax
import systems.deeplearning.activation.Tanh
import systems.deeplearning.util.Matrix.multiply
import kotlin.math.sign

class LayerDense(
    override val tag: String,
    override val layerType: LayerType,
    override val biases: DoubleArray,
    override val weights: Array<DoubleArray>,
    override val activationFunction: ActivationFunction,
    override val activationFunctionDerivative: ActivationFunction,
    override val verifyInputs: Boolean = true,
    override var learningRate: Double = 0.0,
    override val l1RegularizationLambda: Double = 0.1,
    override val l2RegularizationLambda: Double = 0.1
) : ActiveLayer {

    private var input: DoubleArray = doubleArrayOf()
    private var inputs: Array<DoubleArray> = arrayOf(doubleArrayOf())

    val accumulatedWeight: Array<DoubleArray> = Array(weights.size) { DoubleArray(weights[0].size) { 0.0 } }
    val accumulatedBias: DoubleArray = DoubleArray(biases.size) { 0.0 }

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
        // Compute activation gradient with respect to the activation output of the current layer
        val activationGradient: DoubleArray = when (activationFunctionDerivative) {
            is Sigmoid, is Tanh -> lossGradient
                .zip(activationFunctionDerivative.backward(lossGradient)) { loss, act -> loss * act }
                .toDoubleArray()

            is Softmax -> {
                // Softmax: Handle with the derivative which returns a 2D array
                // We need to multiply the lossGradient with the Jacobian matrix to get the correct gradient for each class.
                // In the simplified version, we're just handling the diagonal of the Jacobian matrix (jacobian[i][i]),
                // which is usually sufficient for the backpropagation in multi-class classification.
                val jacobian = activationFunctionDerivative.backward2(lossGradient) // This returns a 2D array
                // To process the Jacobian properly, we need to multiply it with the loss gradient
                val softmaxGradient = DoubleArray(lossGradient.size)
                for (i in lossGradient.indices) {
                    softmaxGradient[i] = jacobian[i][i] * lossGradient[i] // This is just a simplified approach
                }
                softmaxGradient // Returns a 1D array after multiplication with the Jacobian
            }

            else -> activationFunctionDerivative.backward(lossGradient)
        }

        // Initialize arrays for the previous layer's deltas
        val prevDelta = DoubleArray(weights[0].size)

        // Update gradients for weights and biases
        for (bIndex in biases.indices) {
            for (wIndex in weights[bIndex].indices) {
                // Accumulate weight gradient
                accumulatedWeight[bIndex][wIndex] += input[wIndex] * activationGradient[bIndex]
                prevDelta[wIndex] += weights[bIndex][wIndex] * activationGradient[bIndex]
            }

            // Accumulate bias gradient
            accumulatedBias[bIndex] += activationGradient[bIndex]
        }

        return prevDelta
    }

    override fun backward(lossGradients: Array<DoubleArray>): Array<DoubleArray> {
        val batchSize = inputs.size

        // Initialize batch gradient accumulators
        val weightGradients = Array(weights.size) { DoubleArray(weights[0].size) { 0.0 } }
        val biasGradients = DoubleArray(biases.size) { 0.0 }
        val prevDeltas = Array(batchSize) { DoubleArray(weights[0].size) { 0.0 } }

        // Loop through each input in the batch
        for (inputIndex in inputs.indices) {
            val input = inputs[inputIndex]
            val lossGradient = lossGradients[inputIndex]

            // Compute activation gradient for this data point
            val activationGradient: DoubleArray = when (activationFunctionDerivative) {
                is Sigmoid, is Tanh -> lossGradient
                    .zip(activationFunctionDerivative.backward(lossGradient)) { loss, act -> loss * act }
                    .toDoubleArray()

                is Softmax -> {
                    // Softmax: Handle with the derivative which returns a 2D array
                    val jacobian = activationFunctionDerivative.backward2(input) // This returns a 2D array (Jacobian)
                    val softmaxGradient = DoubleArray(lossGradient.size)
                    for (iGradient in lossGradient.indices) {
                        // Using diagonal of Jacobian for simplicity
                        softmaxGradient[iGradient] = jacobian[iGradient][iGradient] * lossGradient[iGradient]
                    }
                    softmaxGradient // Return a 1D array after applying the Jacobian
                }

                else -> activationFunctionDerivative.backward(lossGradient)
            }

            for (bIndex in biases.indices) {
                for (wIndex in weights[bIndex].indices) {
                    // Compute weight gradients
                    weightGradients[bIndex][wIndex] += input[wIndex] * activationGradient[bIndex] / batchSize // Average within loop

                    // Compute previous layer deltas
                    prevDeltas[inputIndex][wIndex] += weights[bIndex][wIndex] * activationGradient[bIndex]
                }
                // Compute bias gradients
                biasGradients[bIndex] += activationGradient[bIndex] / batchSize // Average within loop
            }
        }

        // Accumulate the computed gradients
        for (wIndex in weights.indices) {
            for (wIndex2 in weights[wIndex].indices) {
                accumulatedWeight[wIndex][wIndex2] += weightGradients[wIndex][wIndex2] // Store accumulated weights
            }
        }
        for (bIndex in biases.indices) {
            accumulatedBias[bIndex] += biasGradients[bIndex] // Store accumulated biases
        }

        return prevDeltas
    }

    override fun applyL1Regularization() {
        if (l1RegularizationLambda <= 0.0) {
            return
        }

        for (i in weights.indices) {
            for (j in weights[i].indices) {
                // Apply L1 weight decay (shrinkage by absolute value)
                weights[i][j] -= l1RegularizationLambda * weights[i][j].sign
            }
        }
    }

    override fun applyL2Regularization() {
        if (l2RegularizationLambda <= 0.0) {
            return
        }

        for (i in weights.indices) {
            for (j in weights[i].indices) {
                // Apply L2 weight decay
                weights[i][j] -= l2RegularizationLambda * weights[i][j]
            }
        }
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