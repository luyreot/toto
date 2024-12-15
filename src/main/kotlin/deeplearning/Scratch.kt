package deeplearning

import deeplearning.activation.ReLU
import deeplearning.model.LayerDense
import deeplearning.model.NeuralNetwork
import deeplearning.model.Neuron

fun testNeuralNetwork() {
    val nn = NeuralNetwork()
    nn.addLayer(
        LayerDense(
            neurons = arrayOf(Neuron(2.0), Neuron(3.0), Neuron(0.5)),
            weights = arrayOf(
                arrayOf(0.2, 0.8, -0.5, 1.0),
                arrayOf(0.5, -0.91, 0.26, -0.5),
                arrayOf(-0.26, -0.27, 0.17, 0.87)
            ),
            activationFunction = ReLU
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