package deeplearning.model

import deeplearning.loss.LossFunctions

/**
 * Goal is to:
 * - accept various types of data, including probabilities and occurrences of numbers.
 * - predict a future outcome, such as the likelihood of a specific number or combination of numbers occurring.
 *
 * This falls under the category of sequence prediction or time-series forecasting,
 * depending on how the data is structured and the patterns that will be learnt.
 */
class NeuralNetwork(
    val label: String,
    val layers: MutableList<Layer> = mutableListOf(),
    val sleep: Boolean = true
) {

    var learningRate: Double = 0.01
    var loss: Double = 0.0
    var epoch: Int = 0

    fun addLayer(layer: Layer) {
        layer.learningRate = learningRate
        layers.add(layer)
    }

    fun addLayers(vararg layers: Layer) {
        layers.forEach { it.learningRate = learningRate }
        this.layers.addAll(layers)
    }

    // region training

    fun updateLearningRate(rate: Double) {
        learningRate = rate
        layers.forEach { it.learningRate = rate }
    }

    fun train(
        epoch: Int,
        input: DoubleArray,
        target: DoubleArray
    ) {
        loss = 0.0
        this.epoch = epoch

        val output = forward(input)
//        println(output.joinToString(", "))

        sleep()

        loss = LossFunctions.categoricalCrossEntropy(predictions = output, targets = target)
        println("Loss $loss")

        sleep()

        val lossGradient = LossFunctions.categoricalCrossEntropyDerivative(predictions = output, targets = target)

        sleep()

        backward(lossGradient)

        sleep()
    }

    fun train(
        epoch: Int,
        inputs: Array<DoubleArray>,
        targets: Array<DoubleArray>
    ) {
        loss = 0.0
        this.epoch = epoch

        val output = forward(inputs)
//        output.forEach { row -> println(row.joinToString(", ")) }

        sleep()

        loss = LossFunctions.categoricalCrossEntropyBatch(predictions = output, targets = targets)
        println("Loss $loss")

        sleep()

        val lossGradients = LossFunctions.categoricalCrossEntropyGradientBatch(predicted = output, actual = targets)

        sleep()

        backward(lossGradients)

        sleep()
    }

    // endregion training

    // region forward propagation

    fun forward(input: DoubleArray): DoubleArray {
        var output = input
        for (layer in layers) {
            output = layer.forward(output)
        }
        return output
    }

    fun forward(inputs: Array<DoubleArray>): Array<DoubleArray> {
        var output = inputs
        for (layer in layers) {
            output = layer.forward(output)
        }
        return output
    }

    // endregion forward propagation

    // region back propagation

    fun backward(lossGradient: DoubleArray): DoubleArray {
        var gradient = lossGradient
        for (layer in layers.reversed()) {
            gradient = layer.backward(gradient)
        }
        return gradient
    }

    fun backward(lossGradients: Array<DoubleArray>): Array<DoubleArray> {
        var gradients = lossGradients
        for (layer in layers.reversed()) {
            gradients = layer.backward(gradients)
        }
        return gradients
    }

    // region back propagation

    private fun sleep() {
        if (!sleep) return
        try {
            Thread.sleep(250)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}