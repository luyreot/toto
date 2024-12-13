package deeplearning

class NeuralNetwork(
    val layers: MutableList<Layer> = mutableListOf()
) {

    fun addLayer(layer: Layer) {
        layers.add(layer)
    }

    fun forward(input: DoubleArray): DoubleArray {
        var currentInput = input
        for (layer in layers) {
            currentInput = layer.forward(currentInput)
        }
        return currentInput
    }

    fun forward(inputs: Array<DoubleArray>): Array<DoubleArray> {
        var currentInputs = inputs
        for (layer in layers) {
            currentInputs = layer.forward(currentInputs)
        }
        return currentInputs
    }
}