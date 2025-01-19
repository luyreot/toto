package deeplearning

import deeplearning.activation.ReLU
import deeplearning.activation.Softmax
import deeplearning.model.LayerDense
import deeplearning.model.NeuralNetwork
import deeplearning.model.Neuron

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
        // Hidden Layers
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
            activationFunction = Softmax(),
            activationFunctionDerivative = ReLU // TODO
        )
    )
    val inputs = arrayOf(
        doubleArrayOf(1.0, 2.0, 3.0, 2.5),
        doubleArrayOf(2.0, 5.0, -1.0, 2.0),
        doubleArrayOf(-1.5, 2.7, 3.3, -0.8)
    )
    val output = nn.forward(inputs)
    output.forEach { row -> println(row.joinToString(", ")) }
}