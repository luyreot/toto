package deeplearning

import deeplearning.activation.ReLU
import deeplearning.activation.Softmax
import deeplearning.loss.LossFunctions
import deeplearning.model.LayerDense
import deeplearning.model.NeuralNetwork
import deeplearning.model.Neuron

const val LEARNING_RATE = 0.01

fun testNeuralNetwork() {
    val nn = NeuralNetwork()
    nn.addLayers(
        // Input Layer
        LayerDense(
            neurons = arrayOf(
                Neuron(2.0),
                Neuron(3.0),
                Neuron(0.5)
            ),
            weights = arrayOf(
                doubleArrayOf(0.2, 0.8, -0.5, 1.0),
                doubleArrayOf(0.5, -0.91, 0.26, -0.5),
                doubleArrayOf(-0.26, -0.27, 0.17, 0.87)
            ),
            activationFunction = ReLU
        ),
        // Hidden Layer(s)
        LayerDense(
            neurons = arrayOf(
                Neuron(1.2),
                Neuron(2.2),
                Neuron(0.5)
            ),
            weights = arrayOf(
                doubleArrayOf(0.2, 0.8, -0.5),
                doubleArrayOf(0.5, -0.91, 0.26),
                doubleArrayOf(-0.26, -0.27, 0.17)
            ),
            activationFunction = ReLU
        ),
        // Output Layer
        LayerDense(
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

    val epochs = 1000

    for (i in 0 until epochs) {
//        val output = nn.forward(input)
        val output = nn.forward(inputs)

//        println(output.joinToString(", "))
        output.forEach { row -> println(row.joinToString(", ")) }

//        val loss = LossFunctions.categoricalCrossEntropy(predicted = output, actual = target)
//        println("Epoch $i, Loss $loss")
        val losses = LossFunctions.categoricalCrossEntropyBatch(predicted = output, actual = targets)
        println("Epoch $i, Loss $losses")

//        val lossGradient = LossFunctions.categoricalCrossEntropyGradient(predicted = output, actual = target)
        val lossGradients = LossFunctions.categoricalCrossEntropyGradientBatch(predicted = output, actual = targets)

//        val gradients = nn.backward(lossGradient)
        val gradients = nn.backward(lossGradients)

//        sgdUpdate(weights, biases, gradients, learningRate) // Update weights/biases
    }
}