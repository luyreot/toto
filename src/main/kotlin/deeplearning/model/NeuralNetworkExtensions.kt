package deeplearning.model

import deeplearning.activation.ActivationFunctionType
import deeplearning.loss.*
import org.json.JSONArray
import org.json.JSONObject
import util.IO

// Network
private const val KEY_LEARNING_RATE = "learningRate"
private const val KEY_LOSS = "loss"
private const val KEY_LOSS_FUNCTION = "lossFunction"
private const val KEY_EPOCH = "epoch"
private const val KEY_LAYERS = "layers"

// Layer
private const val KEY_LAYER_TAG = "tag"
private const val KEY_LAYER_TYPE = "type"
private const val KEY_BIASES = "biases"
private const val KEY_WEIGHTS = "weights"
private const val KEY_ACTIVATION_FUNCTION = "activationFunction"
private const val KEY_ACTIVATION_FUNCTION_DERIVATIVE = "activationFunctionDerivative"
private const val KEY_LAYER_LEARNING_RATE = "layerLearningRate"

fun NeuralNetwork.cacheAsJson(fileName: String = label) {
    val json = JSONObject()

    val jsonArrLayers = JSONArray()
    layers.forEach { layer ->
        val jsonLayer = JSONObject()

        jsonLayer.put(KEY_LAYER_TAG, layer.tag)
        jsonLayer.put(KEY_LAYER_TYPE, layer.layerType.name)

        val jsonArrBiases = JSONArray()
        layer.neurons.map { it.bias }.forEach { jsonArrBiases.put(it) }
        jsonLayer.put(KEY_BIASES, jsonArrBiases)

        val jsonArrWeights = JSONArray()
        layer.weights.forEach { weightArr ->
            val jsonArrWeight = JSONArray()
            weightArr.forEach { jsonArrWeight.put(it) }
            jsonArrWeights.put(jsonArrWeight)
        }
        jsonLayer.put(KEY_WEIGHTS, jsonArrWeights)

        jsonLayer.put(KEY_ACTIVATION_FUNCTION, layer.activationFunction.type.name)
        jsonLayer.put(KEY_ACTIVATION_FUNCTION_DERIVATIVE, layer.activationFunctionDerivative.type.name)

        jsonLayer.put(KEY_LAYER_LEARNING_RATE, layer.learningRate)

        jsonArrLayers.put(jsonLayer)
    }

    json.put(KEY_LAYERS, jsonArrLayers)
    json.put(KEY_LEARNING_RATE, learningRate)
    json.put(KEY_LOSS, loss)
    json.put(KEY_LOSS_FUNCTION, lossFunction.type.name)
    json.put(KEY_EPOCH, epoch)

    IO.saveTxtFile(
        fileName = "files/nn/$fileName.json",
        contents = json.toString()
    )
}

fun NeuralNetwork.restoreFromJson(fileName: String = label) {
    val fileContents = IO.getTxtFileContents(fileName = "files/nn/$fileName.json")
    val json = JSONObject(fileContents.first())

    this.learningRate = json.getDouble(KEY_LEARNING_RATE)
    this.loss = json.getDouble(KEY_LOSS)
    val lossFunctionType = LossFunctionType.valueOf(json.getString(KEY_LOSS_FUNCTION))

    this.lossFunction = when (lossFunctionType) {
        LossFunctionType.BinaryCrossEntropy -> BinaryCrossEntropy
        LossFunctionType.WeightedBinaryCrossEntropy -> WeightedBinaryCrossEntropy
        LossFunctionType.CategoricalCrossEntropy -> CategoricalCrossEntropy
        LossFunctionType.MeanSquaredError -> MeanSquaredError
    }

    this.epoch = json.getInt(KEY_EPOCH)

    this.layers.clear()
    val jsonArrLayers = json.getJSONArray(KEY_LAYERS)
    this.layers.addAll(
        jsonArrLayers.map { JSONObject(it.toString()) }.map { jsonLayer ->
            val layerTag = jsonLayer.getString(KEY_LAYER_TAG)
            val layerType = LayerType.valueOf(jsonLayer.getString(KEY_LAYER_TYPE))

            val neurons = jsonLayer.getJSONArray(KEY_BIASES)
                .map { it.toString().toDouble() }
                .map { Neuron(it) }.toTypedArray()

            val weights = mutableListOf<DoubleArray>()
            val jsonArrWeights = jsonLayer.getJSONArray(KEY_WEIGHTS)
            weights.addAll(
                jsonArrWeights.map { jsonArrWeight ->
                    JSONArray(jsonArrWeight.toString()).map { it.toString().toDouble() }.toDoubleArray()
                }
            )

            val activationFunction =
                ActivationFunctionType
                    .valueOf(jsonLayer.getString(KEY_ACTIVATION_FUNCTION))
                    .getActivationFunctionInstance()

            val activationFunctionDerivative =
                ActivationFunctionType
                    .valueOf(jsonLayer.getString(KEY_ACTIVATION_FUNCTION_DERIVATIVE))
                    .getActivationFunctionInstance()

            val learningRate = jsonLayer.getDouble(KEY_LAYER_LEARNING_RATE)

            LayerDense(
                tag = layerTag,
                layerType = layerType,
                neurons = neurons,
                weights = weights.toTypedArray(),
                activationFunction = activationFunction,
                activationFunctionDerivative = activationFunctionDerivative,
                learningRate = learningRate
            )
        }
    )
}
