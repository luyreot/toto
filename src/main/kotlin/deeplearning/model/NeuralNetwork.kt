package deeplearning.model

/**
 * Goal is to:
 * - accept various types of data, including probabilities and occurrences of numbers.
 * - predict a future outcome, such as the likelihood of a specific number or combination of numbers occurring.
 *
 * This falls under the category of sequence prediction or time-series forecasting,
 * depending on how the data is structured and the patterns that will be learnt.
 */
class NeuralNetwork(
    val layers: MutableList<Layer> = mutableListOf()
) {

    fun addLayer(layer: Layer) {
        layers.add(layer)
    }

    fun addLayers(vararg layers: Layer) {
        this.layers.addAll(layers)
    }

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
}