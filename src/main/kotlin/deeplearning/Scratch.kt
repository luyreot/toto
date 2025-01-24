package deeplearning

import deeplearning.activation.ReLU
import deeplearning.activation.Softmax
import deeplearning.model.LayerDense
import deeplearning.model.LayerType
import deeplearning.model.NeuralNetwork
import deeplearning.model.cacheAsJson

fun testNeuralNetwork() {
    val nn = NeuralNetwork(tag = "training")
    nn.addLayers(
        // Input Layer is the inputs array
        // Hidden Layer(s)
        LayerDense(
            layerType = LayerType.HIDDEN,
            activationFunction = ReLU,
            numNeurons = 4,
            numInputs = 4
        ),
        LayerDense(
            layerType = LayerType.HIDDEN,
            activationFunction = ReLU,
            numNeurons = 5,
            numInputs = 4
        ),
        // Output Layer
        LayerDense(
            layerType = LayerType.OUTPUT,
            activationFunction = Softmax(),
            numNeurons = 3,
            numInputs = 5
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

    nn.updateLearningRate(0.1)
    nn.train(epochs = epochs, input = input, target = target)
//    nn.train(epochs = epochs, inputs = inputs, targets = targets)

    nn.cacheAsJson()
}