package systems.deeplearning.model

import model.TotoType
import systems.deeplearning.loss.*
import systems.deeplearning.optimization.Adam
import systems.deeplearning.optimization.LRegularizationType
import systems.deeplearning.optimization.OptimizationFunction
import systems.deeplearning.optimization.SGD
import kotlin.math.ln

/**
 * Goal is to:
 * - accept various types of data, including probabilities and occurrences of numbers.
 * - predict a future outcome, such as the likelihood of a specific number or combination of numbers occurring.
 *
 * This falls under the category of sequence prediction or time-series forecasting,
 * depending on how the data is structured and the patterns that will be learnt.
 *
 * TODO:
 * - Reduce the learning rate during training (e.g., decay schedule or step-based learning rate)
 */
class NeuralNetwork(
    val totoType: TotoType,
    val label: String,
    val layers: MutableList<Layer> = mutableListOf(),
    var lossFunction: LossFunction,
    var optimizationFunction: OptimizationFunction,
    var lRegularizationType: LRegularizationType,
    private var sleep: Boolean = true
) {

    var learningRate: Double = 0.001
    var positiveTargetThreshold: Double = 0.5
    var positiveOutputThreshold: Double = 1.0
    var loss: Double = 0.0
    var epoch: Int = 1

    private var sleepDuration: Long = 250

    init {
        (optimizationFunction as? Adam)?.learningRate = learningRate
    }

    fun addLayer(layer: Layer) {
        if (layer is ActiveLayer) {
            layer.learningRate = learningRate
        }
        layers.add(layer)
    }

    fun addLayers(vararg layers: Layer) {
        layers.filterIsInstance<ActiveLayer>().forEach { it.learningRate = learningRate }
        this.layers.addAll(layers)
    }

    /**
     * A good rule of thumb is to set the initial bias
     * b = ln(p / 1 - p), where p is the proportion of 1s in the dataset.
     */
    fun optimizeOutputLayerBiasesForBinaryImbalances() {
        verifyOutputLayer()
        layers.filterIsInstance<ActiveLayer>().find { it.layerType == LayerType.OUTPUT }?.let { outputLayer ->
            val p = totoType.size.toDouble() / totoType.totalNumbers
            val updatedBias = ln(p / (1 - p))
            for (i in outputLayer.biases.indices) {
                outputLayer.biases[i] = updatedBias
            }
        }
    }

    private fun verifyInputLayer() {
        val inputLayers = layers.count { it.layerType == LayerType.INPUT }
        if (inputLayers > 1) {
            throw IllegalArgumentException("Cannot have more than one input layer.")
        }
    }

    private fun verifyOutputLayer() {
        val outputLayers = layers.count { it.layerType == LayerType.OUTPUT }
        if (outputLayers < 1) {
            throw IllegalArgumentException("There must be one output layer.")
        }
        if (outputLayers > 1) {
            throw IllegalArgumentException("Cannot have more than one output layer.")
        }
    }

    // region training

    fun updateLearningRate(rate: Double) {
        learningRate = rate
        layers.filterIsInstance<ActiveLayer>().forEach { it.learningRate = rate }
    }

    fun train(
        epoch: Int,
        inputs: DoubleArray,
        targets: DoubleArray
    ) {
        loss = 0.0
        var lossGradients: DoubleArray
        this.epoch = epoch

        val output = forward(inputs)

        sleep()

        calculateLoss(predictions = output, targets = targets).let {
            loss = it.first
            lossGradients = it.second
        }

        println("Output:\n${output.contentToString()}")
        println("Target:\n${targets.contentToString()}")

        val targetNegativeCount = targets.count { it < positiveTargetThreshold }
        val targetPositiveCount = targets.count { it >= positiveTargetThreshold }

        if (targetNegativeCount + targetPositiveCount != targets.size) {
            throw IllegalArgumentException("Incorrect target negative ($targetNegativeCount), positive ($targetPositiveCount) and total (${targets.size}) count calculation.")
        }

        val outputNegativeCount = output.count { it < positiveOutputThreshold }
        val outputPositiveCount = output.count { it >= positiveOutputThreshold }

        val outputNegativeMatches = targets.zip(output)
            .count { (t, o) -> t < positiveTargetThreshold && o < positiveOutputThreshold }
        val outputPositiveMatches = targets.zip(output)
            .count { (t, o) -> t >= positiveTargetThreshold && o >= positiveOutputThreshold }

        println("Predicted 0s: $outputNegativeCount")
        println("Predicted 1s: $outputPositiveCount")
        println("Matched 0s: $outputNegativeMatches/$targetNegativeCount")
        println("Matched 1s: $outputPositiveMatches/$targetPositiveCount")
        println("Matched:    ${outputNegativeMatches + outputPositiveMatches}/${targetNegativeCount + targetPositiveCount}")

        println("Loss $loss")
        println("---")

        sleep()

        backward(lossGradients)

        sleep()

        optimize()

        sleep()
    }

    fun train(
        epoch: Int,
        inputs: Array<DoubleArray>,
        targets: Array<DoubleArray>
    ) {
        loss = 0.0
        var lossGradients: Array<DoubleArray>
        this.epoch = epoch

        val output = forward(inputs)

        sleep()

        calculateLoss(predictions = output, targets = targets).let {
            loss = it.first
            lossGradients = it.second
        }

        println("Batch size: ${output.size}")

        val outputString = output.joinToString("\n") { it.contentToString() }
        println("Output:\n$outputString")
        val targetString = targets.joinToString("\n") { it.contentToString() }
        println("Target:\n$targetString")

        var totalNegativeCount = 0
        var totalPositiveCount = 0
        var totalOutputNegativeCount = 0
        var totalOutputPositiveCount = 0
        var matchedNegativeCount = 0
        var matchedPositiveCount = 0

        for (i in output.indices) {
            val targetNegativeCount = targets[i].count { it < positiveTargetThreshold }
            val targetPositiveCount = targets[i].count { it >= positiveTargetThreshold }

            if (targetNegativeCount + targetPositiveCount != targets.size) {
                throw IllegalArgumentException("Incorrect target negative ($targetNegativeCount), positive ($targetPositiveCount) and total (index - $i, size - ${targets[i].size}) count calculation.")
            }

            val outputNegativeCount = output[i].count { it < positiveOutputThreshold }
            val outputPositiveCount = output[i].count { it >= positiveOutputThreshold }

            val outputNegativeMatches = targets[i].zip(output[i])
                .count { (t, o) -> t < positiveTargetThreshold && o < positiveOutputThreshold }
            val outputPositiveMatches = targets[i].zip(output[i])
                .count { (t, o) -> t >= positiveTargetThreshold && o >= positiveOutputThreshold }

            totalNegativeCount += targetNegativeCount
            totalPositiveCount += targetPositiveCount
            totalOutputNegativeCount += outputNegativeCount
            totalOutputPositiveCount += outputPositiveCount
            matchedNegativeCount += outputNegativeMatches
            matchedPositiveCount += outputPositiveMatches
        }

        println("Predicted 0s: $totalOutputNegativeCount")
        println("Predicted 1s: $totalOutputPositiveCount")
        println("Matched 0s: $matchedNegativeCount/$totalNegativeCount")
        println("Matched 1s: $matchedPositiveCount/$totalPositiveCount")
        println("Matched:    ${matchedNegativeCount + matchedPositiveCount}/${totalNegativeCount + totalPositiveCount}")

        println("Loss $loss")
        println("---")

        sleep()

        backward(lossGradients)

        sleep()

        optimize()

        sleep()
    }

    // endregion training

    // region forward propagation

    fun forward(input: DoubleArray): DoubleArray {
        var output = input.copyOf()
        for (layer in layers) {
            output = layer.forward(output).copyOf()
        }
        return output
    }

    fun forward(inputs: Array<DoubleArray>): Array<DoubleArray> {
        var output = inputs.map { it.copyOf() }.toTypedArray()
        for (layer in layers) {
            output = layer.forward(output).map { it.copyOf() }.toTypedArray()
        }
        return output
    }

    // endregion forward propagation

    // region back propagation

    private fun calculateLoss(
        predictions: DoubleArray,
        targets: DoubleArray
    ): Pair<Double, DoubleArray> = lossFunction.let { lf ->
        when (lf) {
            is BinaryCrossEntropy -> {
                val loss = lf.calculateLoss(predictions = predictions, targets = targets)
                sleep()
                val gradients = lf.calculateGradient(predictions = predictions, targets = targets)
                Pair(loss, gradients)
            }

            is CategoricalCrossEntropy -> {
                val loss = lf.calculateLoss(predictions = predictions, targets = targets)
                sleep()
                val gradients = lf.calculateGradient(predictions = predictions, targets = targets)
                Pair(loss, gradients)
            }

            is MeanSquaredError -> {
                val loss = lf.calculateLoss(predictions = predictions, targets = targets)
                sleep()
                val gradients = lf.calculateLossDerivative(predictions = predictions, targets = targets)
                Pair(loss, gradients)
            }

            is WeightedBinaryCrossEntropy -> {
                val weights = DoubleArray(targets.size) { i ->
                    if (targets[i] >= positiveTargetThreshold) 10.0 else 0.0
                }
                sleep()
                val loss = lf.calculateLoss(predictions = predictions, targets = targets, weights = weights)
                sleep()
                val gradients = lf.calculateGradient(predictions = predictions, targets = targets, weights = weights)
                Pair(loss, gradients)
            }

            is FocalLoss -> {
                val loss = lf.calculateLoss(predictions = predictions, targets = targets)
                sleep()
                val gradients = lf.calculateGradient(predictions = predictions, targets = targets)
                Pair(loss, gradients)
            }

            else -> throw IllegalArgumentException("Unknown loss function type ${lf.type}.")
        }
    }

    private fun calculateLoss(
        predictions: Array<DoubleArray>,
        targets: Array<DoubleArray>
    ): Pair<Double, Array<DoubleArray>> = lossFunction.let { lf ->
        when (lf) {
            is BinaryCrossEntropy -> {
                val loss = lf.calculateLoss(predictions = predictions, targets = targets)
                sleep()
                val gradients = lf.calculateGradient(predictions = predictions, targets = targets)
                Pair(loss, gradients)
            }

            is CategoricalCrossEntropy -> {
                val loss = lf.calculateLoss(predictions = predictions, targets = targets)
                sleep()
                val gradients = lf.calculateGradient(predictions = predictions, targets = targets)
                Pair(loss, gradients)
            }

            is MeanSquaredError -> {
                val loss = lf.calculateLoss(predictions = predictions, targets = targets)
                sleep()
                val gradients = lf.calculateLossDerivative(predictions = predictions, targets = targets)
                Pair(loss, gradients)
            }

            is WeightedBinaryCrossEntropy -> {
                val weights = Array(targets.size) { i ->
                    DoubleArray(targets[i].size) { j ->
                        if (targets[i][j] >= positiveTargetThreshold) 10.0 else 0.0
                    }
                }
                sleep()
                val loss = lf.calculateLoss(predictions = predictions, targets = targets, weights = weights)
                sleep()
                val gradients = lf.calculateGradient(predictions = predictions, targets = targets, weights = weights)
                Pair(loss, gradients)
            }

            is FocalLoss -> {
                val loss = lf.calculateLoss(predictions = predictions, targets = targets)
                sleep()
                val gradients = lf.calculateGradient(predictions = predictions, targets = targets)
                Pair(loss, gradients)
            }

            else -> throw IllegalArgumentException("Unknown loss function type ${lf.type}.")
        }
    }

    private fun backward(lossGradient: DoubleArray): DoubleArray {
        var gradient = lossGradient.copyOf()
        for (layer in layers.reversed()) {
            gradient = layer.backward(gradient).copyOf()
        }
        return gradient
    }

    private fun backward(lossGradients: Array<DoubleArray>): Array<DoubleArray> {
        var gradients = lossGradients.map { it.copyOf() }.toTypedArray()
        for (layer in layers.reversed()) {
            gradients = layer.backward(gradients).map { it.copyOf() }.toTypedArray()
        }
        return gradients
    }

    // region back propagation

    // region optimize

    private fun optimize() {
        if (lRegularizationType != LRegularizationType.NONE) {
            layers.filterIsInstance<LayerDense>().forEach { layer ->
                when (lRegularizationType) {
                    LRegularizationType.NONE -> {}
                    LRegularizationType.L1 -> layer.applyL1Regularization()
                    LRegularizationType.L2 -> layer.applyL2Regularization()
                }
            }
        }

        layers.filterIsInstance<LayerDense>().forEach { layer ->
            optimizationFunction.let { of ->
                when (of) {
                    is SGD -> of.optimize(layer, learningRate)
                    is Adam -> of.optimize(layer)
                    else -> throw IllegalArgumentException("Unknown optimizer  ${of.type}.")
                }
            }
        }
    }

    // endregion optimize

    private fun sleep() {
        if (!sleep) return
        try {
            Thread.sleep(sleepDuration)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}