package deeplearning.model

import deeplearning.loss.*
import deeplearning.optimization.OptimizationFunction
import deeplearning.optimization.SGD
import model.TotoType
import kotlin.math.ln

/**
 * Goal is to:
 * - accept various types of data, including probabilities and occurrences of numbers.
 * - predict a future outcome, such as the likelihood of a specific number or combination of numbers occurring.
 *
 * This falls under the category of sequence prediction or time-series forecasting,
 * depending on how the data is structured and the patterns that will be learnt.
 */
class NeuralNetwork(
    val totoType: TotoType,
    val label: String,
    val layers: MutableList<Layer> = mutableListOf(),
    var lossFunction: LossFunction,
    var optimizationFunction: OptimizationFunction,
    private var sleep: Boolean = true
) {

    var learningRate: Double = 0.000001
    var loss: Double = 0.0
    var epoch: Int = 0

    private var sleepDuration: Long = 250

    fun addLayer(layer: Layer) {
        layer.learningRate = learningRate
        layers.add(layer)
    }

    fun addLayers(vararg layers: Layer) {
        layers.forEach { it.learningRate = learningRate }
        this.layers.addAll(layers)
    }

    /**
     * A good rule of thumb is to set the initial bias
     * b = ln(p / 1 - p), where p is the proportion of 1s in the dataset.
     */
    fun optimizeOutputLayerBiasesForBinaryImbalances() {
        verifyOutputLayer()
        layers.find { it.layerType == LayerType.OUTPUT }?.let { outputLayer ->
            val p = totoType.size.toDouble() / totoType.totalNumbers
            val updatedBias = ln(p / (1 - p))
            for (i in outputLayer.biases.indices) {
                outputLayer.biases[i] = updatedBias
            }
        }
    }

    private fun verifyInputLayer() {
        val inputLayers = layers.count { it.layerType == LayerType.INPUT }
        if (inputLayers > 1) {
            throw IllegalArgumentException("Cannot have more than one input layer.")
        }
    }

    private fun verifyOutputLayer() {
        val outputLayers = layers.count { it.layerType == LayerType.OUTPUT }
        if (outputLayers < 1) {
            throw IllegalArgumentException("There must be one output layer.")
        }
        if (outputLayers > 1) {
            throw IllegalArgumentException("Cannot have more than one output layer.")
        }
    }

    // region training

    fun updateLearningRate(rate: Double) {
        learningRate = rate
        layers.forEach { it.learningRate = rate }
    }

    fun train(
        epoch: Int,
        inputs: DoubleArray,
        targets: DoubleArray
    ) {
        loss = 0.0
        var lossGradients: DoubleArray
        this.epoch = epoch

        val output = forward(inputs)
        //println(output.joinToString(", "))

        sleep()

        calculateLoss(predictions = output, targets = targets).let {
            loss = it.first
            lossGradients = it.second
        }

        println("Target - ${targets.contentToString()}")
        println("Output - ${output.contentToString()}")
        println("Loss $loss")
        println("---")

        sleep()

        backward(lossGradients)

        sleep()

        optimize()

        sleep()
    }

    fun train(
        epoch: Int,
        inputs: Array<DoubleArray>,
        targets: Array<DoubleArray>
    ) {
        loss = 0.0
        var lossGradients: Array<DoubleArray>
        this.epoch = epoch

        val output = forward(inputs)
        //output.forEach { row -> println(row.joinToString(", ")) }

        sleep()

        calculateLoss(predictions = output, targets = targets).let {
            loss = it.first
            lossGradients = it.second
        }

        println("Loss $loss")

        sleep()

        backward(lossGradients)

        sleep()

        optimize()

        sleep()
    }

    // endregion training

    // region forward propagation

    private fun forward(input: DoubleArray): DoubleArray {
        var output = input
        for (layer in layers) {
            output = layer.forward(output)
        }
        return output
    }

    private fun forward(inputs: Array<DoubleArray>): Array<DoubleArray> {
        var output = inputs
        for (layer in layers) {
            output = layer.forward(output)
        }
        return output
    }

    // endregion forward propagation

    // region back propagation

    private fun calculateLoss(
        predictions: DoubleArray,
        targets: DoubleArray
    ): Pair<Double, DoubleArray> = lossFunction.let { lf ->
        when (lf) {
            is BinaryCrossEntropy -> {
                val loss = lf.calculateLoss(predictions = predictions, targets = targets)
                sleep()
                val gradients = lf.calculateGradient(predictions = predictions, targets = targets)
                Pair(loss, gradients)
            }

            is CategoricalCrossEntropy -> {
                val loss = lf.calculateLoss(predictions = predictions, targets = targets)
                sleep()
                val gradients = lf.calculateGradient(predictions = predictions, targets = targets)
                Pair(loss, gradients)
            }

            // TODO: Not tested
            is MeanSquaredError -> {
                val loss = lf.calculateLoss(predictions = predictions, targets = targets)
                sleep()
                val gradients = lf.calculateLossDerivative(predictions = predictions, targets = targets)
                Pair(loss, gradients)
            }

            // TODO: Not tested
            is WeightedBinaryCrossEntropy -> {
                val weights = DoubleArray(targets.size) { i ->
                    if (targets[i] == 1.0) totoType.totalNumbers.toDouble() / totoType.size else 1.0
                }
                sleep()
                val loss = lf.calculateLoss(predictions = predictions, targets = targets, weights = weights)
                sleep()
                val gradients = lf.calculateGradient(predictions = predictions, targets = targets, weights = weights)
                Pair(loss, gradients)
            }

            else -> throw IllegalArgumentException("Unknown loss function type ${lf.type}.")
        }
    }

    private fun calculateLoss(
        predictions: Array<DoubleArray>,
        targets: Array<DoubleArray>
    ): Pair<Double, Array<DoubleArray>> = lossFunction.let { lf ->
        when (lf) {
            is BinaryCrossEntropy -> {
                val loss = lf.calculateLoss(predictions = predictions, targets = targets)
                sleep()
                val gradients = lf.calculateGradient(predictions = predictions, targets = targets)
                Pair(loss, gradients)
            }

            is CategoricalCrossEntropy -> {
                val loss = lf.calculateLoss(predictions = predictions, targets = targets)
                sleep()
                val gradients = lf.calculateGradient(predictions = predictions, targets = targets)
                Pair(loss, gradients)
            }

            // TODO: Not tested
            is MeanSquaredError -> {
                val loss = lf.calculateLoss(predictions = predictions, targets = targets)
                sleep()
                val gradients = lf.calculateLossDerivative(predictions = predictions, targets = targets)
                Pair(loss, gradients)
            }

            is WeightedBinaryCrossEntropy -> {
                val weights = Array(targets.size) { i ->
                    DoubleArray(targets[i].size) { j ->
                        if (targets[i][j] == 1.0) totoType.totalNumbers.toDouble() / totoType.size else 1.0
                    }
                }
                sleep()
                val loss = lf.calculateLoss(predictions = predictions, targets = targets, weights = weights)
                sleep()
                val gradients = lf.calculateGradient(predictions = predictions, targets = targets, weights = weights)
                Pair(loss, gradients)
            }

            else -> throw IllegalArgumentException("Unknown loss function type ${lf.type}.")
        }
    }

    private fun backward(lossGradient: DoubleArray): DoubleArray {
        var gradient = lossGradient
        for (layer in layers.reversed()) {
            gradient = layer.backward(gradient)
        }
        return gradient
    }

    private fun backward(lossGradients: Array<DoubleArray>): Array<DoubleArray> {
        var gradients = lossGradients
        for (layer in layers.reversed()) {
            gradients = layer.backward(gradients)
        }
        return gradients
    }

    // region back propagation

    // region optimize

    private fun optimize() {
        optimizationFunction.let { of ->
            when (of) {
                is SGD -> {
                    layers.map { it as LayerDense }.forEach { layer ->
                        of.optimizeWithL2Regularization(
                            layer = layer,
                            learningRate = learningRate,
                        )
                    }
                }
            }
        }
    }

    // endregion optimize

    private fun sleep() {
        if (!sleep) return
        try {
            Thread.sleep(sleepDuration)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}