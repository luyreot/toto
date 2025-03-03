package systems.deeplearning.model

import org.json.JSONArray
import org.json.JSONObject
import systems.deeplearning.activation.ActivationFunctionType
import systems.deeplearning.loss.FocalLoss
import systems.deeplearning.loss.LossFunctionType
import systems.deeplearning.optimization.LRegularizationType
import systems.deeplearning.optimization.OptimizationFunctionType
import util.IO

// Network
private const val KEY_LEARNING_RATE = "learningRate"
private const val KEY_POSITIVE_TARGET_THRESHOLD = "positiveTargetThreshold"
private const val KEY_POSITIVE_OUTPUT_THRESHOLD = "positiveOutputThreshold"
private const val KEY_LOSS = "loss"
private const val KEY_LOSS_FUNCTION = "lossFunction"
private const val KEY_LOSS_FUNCTION_TYPE = "lossFunctionType"
private const val KEY_OPTIMIZATION_FUNCTION = "optimizationFunction"
private const val KEY_L_REGULARIZATION_TYPE = "lRegularizationType"
private const val KEY_EPOCH = "epoch"
private const val KEY_LAYERS = "layers"

// Layer
private const val KEY_LAYER_TAG = "tag"
private const val KEY_LAYER_TYPE = "type"

// LayerDense
private const val KEY_BIASES = "biases"
private const val KEY_WEIGHTS = "weights"
private const val KEY_ACTIVATION_FUNCTION = "activationFunction"
private const val KEY_ACTIVATION_FUNCTION_DERIVATIVE = "activationFunctionDerivative"
private const val KEY_LAYER_LEARNING_RATE = "layerLearningRate"
private const val KEY_LAYER_L1_REGULARIZATION_LAMBDA = "l1RegularizationLambda"
private const val KEY_LAYER_L2_REGULARIZATION_LAMBDA = "l2RegularizationLambda"

// LayerDropout
private const val KEY_DROPOUT_RATE = "dropoutRate"
private const val KEY_DROPOUT_VALUE = "dropoutValue"

// Loss functions
private const val KEY_FOCAL_LOSS_THRESHOLD = "threshold"
private const val KEY_FOCAL_LOSS_GAMMA = "gamma"

fun NeuralNetwork.cacheAsJson(fileName: String = label) {
    val json = JSONObject()

    val jsonArrLayers = JSONArray()
    layers.forEach { layer ->
        val jsonLayer = JSONObject()

        jsonLayer.put(KEY_LAYER_TAG, layer.tag)
        jsonLayer.put(KEY_LAYER_TYPE, layer.layerType.name)

        if (layer is LayerDense) {
            val jsonArrBiases = JSONArray()
            layer.biases.forEach { jsonArrBiases.put(it) }
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
            jsonLayer.put(KEY_LAYER_L1_REGULARIZATION_LAMBDA, layer.l1RegularizationLambda)
            jsonLayer.put(KEY_LAYER_L2_REGULARIZATION_LAMBDA, layer.l2RegularizationLambda)
        }

        if (layer is LayerDropout) {
            jsonLayer.put(KEY_DROPOUT_RATE, layer.dropoutRate)
            jsonLayer.put(KEY_DROPOUT_VALUE, layer.dropoutValue)
        }

        jsonArrLayers.put(jsonLayer)
    }

    json.put(KEY_LAYERS, jsonArrLayers)
    json.put(KEY_LEARNING_RATE, learningRate)
    json.put(KEY_POSITIVE_TARGET_THRESHOLD, positiveTargetThreshold)
    json.put(KEY_POSITIVE_OUTPUT_THRESHOLD, positiveOutputThreshold)
    json.put(KEY_LOSS, loss)

    val lossFunctionJson = JSONObject()
    lossFunctionJson.put(KEY_LOSS_FUNCTION_TYPE, lossFunction.type.name)
    (lossFunction as? FocalLoss)?.let {
        lossFunctionJson.put(KEY_FOCAL_LOSS_THRESHOLD, it.targetThreshold)
        lossFunctionJson.put(KEY_FOCAL_LOSS_GAMMA, it.gamma)
    }
    json.put(KEY_LOSS_FUNCTION, lossFunctionJson)

    json.put(KEY_OPTIMIZATION_FUNCTION, optimizationFunction.type.name)
    json.put(KEY_L_REGULARIZATION_TYPE, lRegularizationType.name)
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

    val lossFunctionJson = json.getJSONObject(KEY_LOSS_FUNCTION)
    this.lossFunction = LossFunctionType.valueOf(lossFunctionJson.getString(KEY_LOSS_FUNCTION_TYPE))
        .getLossFunctionType()
    (this.lossFunction as? FocalLoss)?.let {
        it.targetThreshold = lossFunctionJson.getDouble(KEY_FOCAL_LOSS_THRESHOLD)
        it.gamma = lossFunctionJson.getDouble(KEY_FOCAL_LOSS_GAMMA)
    }

    this.optimizationFunction = OptimizationFunctionType.valueOf(json.getString(KEY_OPTIMIZATION_FUNCTION))
        .getOptimizationFunctionType()
    this.lRegularizationType = LRegularizationType.valueOf(json.getString(KEY_L_REGULARIZATION_TYPE))

    this.positiveTargetThreshold = json.getDouble(KEY_POSITIVE_TARGET_THRESHOLD)
    this.positiveOutputThreshold = json.getDouble(KEY_POSITIVE_OUTPUT_THRESHOLD)

    this.epoch = json.getInt(KEY_EPOCH)

    this.layers.clear()
    val jsonArrLayers = json.getJSONArray(KEY_LAYERS)
    this.layers.addAll(
        jsonArrLayers.map { JSONObject(it.toString()) }.map { jsonLayer ->
            val layerTag = jsonLayer.getString(KEY_LAYER_TAG)
            val layerType = LayerType.valueOf(jsonLayer.getString(KEY_LAYER_TYPE))

            when (layerType) {
                LayerType.INPUT -> TODO("Not implemented.")

                LayerType.DROPOUT -> {
                    val dropoutRate = jsonLayer.getDouble(KEY_DROPOUT_RATE)
                    val dropoutValue = jsonLayer.getDouble(KEY_DROPOUT_VALUE)

                    LayerDropout(
                        tag = layerTag,
                        layerType = layerType,
                        dropoutRate = dropoutRate,
                        dropoutValue = dropoutValue
                    )
                }

                LayerType.HIDDEN, LayerType.OUTPUT -> {
                    val biases = jsonLayer.getJSONArray(KEY_BIASES)
                        .map { it.toString().toDouble() }
                        .toDoubleArray()

                    val weights = mutableListOf<DoubleArray>()
                    val jsonArrWeights = jsonLayer.getJSONArray(KEY_WEIGHTS)
                    weights.addAll(
                        jsonArrWeights.map { jsonArrWeight ->
                            JSONArray(jsonArrWeight.toString()).map { it.toString().toDouble() }.toDoubleArray()
                        }
                    )

                    val activationFunction = ActivationFunctionType
                        .valueOf(jsonLayer.getString(KEY_ACTIVATION_FUNCTION))
                        .getActivationFunctionInstance()

                    val activationFunctionDerivative = ActivationFunctionType
                        .valueOf(jsonLayer.getString(KEY_ACTIVATION_FUNCTION_DERIVATIVE))
                        .getActivationFunctionInstance()

                    val learningRate = jsonLayer.getDouble(KEY_LAYER_LEARNING_RATE)
                    val l1RegularizationLambda = jsonLayer.getDouble(KEY_LAYER_L1_REGULARIZATION_LAMBDA)
                    val l2RegularizationLambda = jsonLayer.getDouble(KEY_LAYER_L2_REGULARIZATION_LAMBDA)

                    LayerDense(
                        tag = layerTag,
                        layerType = layerType,
                        biases = biases,
                        weights = weights.toTypedArray(),
                        activationFunction = activationFunction,
                        activationFunctionDerivative = activationFunctionDerivative,
                        learningRate = learningRate,
                        l1RegularizationLambda = l1RegularizationLambda,
                        l2RegularizationLambda = l2RegularizationLambda
                    )
                }
            }
        }
    )
}
