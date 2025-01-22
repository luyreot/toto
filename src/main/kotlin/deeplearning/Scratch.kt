package deeplearning

import deeplearning.activation.ReLU
import deeplearning.activation.Softmax
import deeplearning.model.*

fun testNeuralNetwork() {
    val nn = NeuralNetwork(tag = "training")
    nn.addLayers(
        // Input Layer
        LayerDense(
            layerType = LayerType.INPUT,
            neurons = arrayOf(
                Neuron(2.0),
                Neuron(3.0),
                Neuron(0.5),
                Neuron(0.1),
            ),
            weights = arrayOf(
                doubleArrayOf(0.2, 0.8, -0.5, 1.0),
                doubleArrayOf(0.5, -0.91, 0.26, -0.5),
                doubleArrayOf(-0.26, -0.27, 0.17, 0.87),
                doubleArrayOf(-0.26, -0.27, 0.17, 0.87)
            ),
            activationFunction = ReLU
        ),
        // Hidden Layer(s)
        LayerDense(
            layerType = LayerType.HIDDEN,
            neurons = arrayOf(
                Neuron(1.2),
                Neuron(2.2),
                Neuron(0.5)
            ),
            weights = arrayOf(
                doubleArrayOf(0.2, 0.8, -0.5, 1.2),
                doubleArrayOf(0.5, -0.91, 0.26, 3.2),
                doubleArrayOf(-0.26, -0.27, 0.17, 1.2)
            ),
            activationFunction = ReLU
        ),
        // Output Layer
        LayerDense(
            layerType = LayerType.OUTPUT,
            neurons = arrayOf(
                Neuron(-1),
                Neuron(2),
                Neuron(-0.5)
            ),
            weights = arrayOf(
                doubleArrayOf(0.1, -0.14, 0.5),
                doubleArrayOf(-0.5, 0.12, -0.33),
                doubleArrayOf(-0.44, 0.73, -0.13)
            ),
            activationFunction = Softmax()
        )
    )

    val input = doubleArrayOf(1.0, 2.0, 3.0, 2.5)
    val inputs = arrayOf(
        doubleArrayOf(1.0, 2.0, 3.0, 2.5),
        doubleArrayOf(2.0, 5.0, -1.0, 2.0),
        doubleArrayOf(-1.5, 2.7, 3.3, -0.8)
    )

    val target: DoubleArray = doubleArrayOf(1.0, 0.0, 0.0)
    // True labels (one-hot encoded for classification)
    val targets = arrayOf(
        doubleArrayOf(1.0, 0.0, 0.0),
        doubleArrayOf(0.0, 1.0, 0.0),
        doubleArrayOf(0.0, 0.0, 1.0)
    )

    val epochs = 512

    nn.train(epochs = epochs, input = input, target = target)
//    nn.train(epochs = epochs, inputs = inputs, targets = targets)

    nn.cacheAsJson()

    val nn2 = NeuralNetwork(tag = "training")
    nn2.restoreFromJson()
    nn2.cacheAsJson(fileName = "training2")
    println()
}