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
    val tag: String,
    val layers: MutableList<Layer> = mutableListOf()
) {

    var learningRate: Double = 0.01
    var loss: Double = 0.0
    var epochs: Int = 0

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
        epochs: Int,
        input: DoubleArray,
        target: DoubleArray
    ) {
        this.loss = 0.0
        this.epochs = epochs

        for (i in 0..epochs) {
            val output = forward(input)
            println(output.joinToString(", "))

            loss = LossFunctions.categoricalCrossEntropy(predicted = output, actual = target)
            println("Epoch $i, Loss $loss")

            val lossGradient = LossFunctions.categoricalCrossEntropyGradient(predicted = output, actual = target)
            backward(lossGradient)
        }
    }

    fun train(
        epochs: Int,
        inputs: Array<DoubleArray>,
        targets: Array<DoubleArray>
    ) {
        this.loss = 0.0
        this.epochs = epochs

        for (i in 0..epochs) {
            val output = forward(inputs)
            output.forEach { row -> println(row.joinToString(", ")) }

            loss = LossFunctions.categoricalCrossEntropyBatch(predicted = output, actual = targets)
            println("Epoch $i, Loss $loss")

            val lossGradients = LossFunctions.categoricalCrossEntropyGradientBatch(predicted = output, actual = targets)
            backward(lossGradients)
        }
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
}