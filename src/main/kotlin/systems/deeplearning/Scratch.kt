package systems.deeplearning

import model.TotoType
import systems.deeplearning.activation.ReLU
import systems.deeplearning.activation.Tanh
import systems.deeplearning.loss.FocalLoss
import systems.deeplearning.model.*
import systems.deeplearning.optimization.Adam
import systems.deeplearning.optimization.LRegularizationType
import systems.deeplearning.util.Data.minMaxNormalizeByColumn
import systems.deeplearning.util.Data.normalizeBasedOnMinMax
import systems.deeplearning.util.Util.generateRandomWeightsHe
import systems.deeplearning.util.Util.generateRandomWeightsXavier
import util.loadDrawings

fun nnTrainDrawFullNumberSet(totoType: TotoType) {
    val yearFilter = 2019
    val draws = loadDrawings(totoType)
        .filter { it.year >= yearFilter - 1 }
//        .filterIndexed { index, _ -> index % 2 == 0 } // 5x35

    var featuresList = mutableListOf<DoubleArray>()
    val targetsList = mutableListOf<DoubleArray>()

    draws.indices.forEach { index ->
        if (draws[index].year < yearFilter) {
            return@forEach
        }

        val features = DoubleArray(totoType.totalNumbers) { 0.0 }
        val targets = DoubleArray(totoType.totalNumbers) { -1.0 }

        draws[index].numbers.forEach draw@{ number ->
            targets[number - 1] = 1.0

            for (num in 1..totoType.totalNumbers) {
                for (i in index - 1 downTo 0) {
                    if (num in draws[i].numbers) {
                        features[num - 1] = (index - i).toDouble()
                        break
                    }
                }
            }
        }

        featuresList.add(features)
        targetsList.add(targets)
    }

    featuresList.removeLast()
    featuresList = minMaxNormalizeByColumn(featuresList)
    targetsList.removeFirst()

    val nn = NeuralNetwork(
        totoType = totoType,
        label = "training-${totoType.name}",
        lossFunction = FocalLoss(targetThreshold = 0.0, gamma = 1.8),
        optimizationFunction = Adam(),
        lRegularizationType = LRegularizationType.L2,
        sleep = false
    )

//    nn.restoreFromJson()
//    /*
    nn.positiveTargetThreshold = 0.0
    nn.positiveOutputThreshold = 0.0

    val hiddenLayer1Neurons = 512
    val hiddenLayer1Inputs = totoType.totalNumbers
    val dropoutLayer1Rate = 0.4
    val hiddenLayer2Neurons = hiddenLayer1Neurons / 2
    val hiddenLayer2Inputs = hiddenLayer1Neurons
    val dropoutLayer2Rate = 0.2
    val hiddenLayer3Neurons = hiddenLayer2Neurons / 2
    val hiddenLayer3Inputs = hiddenLayer2Neurons
    val outputLayerNeurons = totoType.totalNumbers
    val outputLayerInputs = hiddenLayer3Neurons

    nn.addLayers(
        // Input Layer is represented by the inputs array
        // Hidden Layer(s)
        LayerDense(
            tag = "Hidden 1",
            layerType = LayerType.HIDDEN,
            activationFunction = ReLU,
            numNeurons = hiddenLayer1Neurons,
            numInputs = hiddenLayer1Inputs,
            weightInit = ::generateRandomWeightsHe
        ),
        LayerDropout(
            tag = "Dropout 1",
            layerType = LayerType.DROPOUT,
            dropoutRate = dropoutLayer1Rate
        ),
        LayerDense(
            tag = "Hidden 2",
            layerType = LayerType.HIDDEN,
            activationFunction = ReLU,
            numNeurons = hiddenLayer2Neurons,
            numInputs = hiddenLayer2Inputs,
            weightInit = ::generateRandomWeightsHe
        ),
        LayerDropout(
            tag = "Dropout 2",
            layerType = LayerType.DROPOUT,
            dropoutRate = dropoutLayer2Rate
        ),
        LayerDense(
            tag = "Hidden 3",
            layerType = LayerType.HIDDEN,
            activationFunction = ReLU,
            numNeurons = hiddenLayer3Neurons,
            numInputs = hiddenLayer3Inputs,
            weightInit = ::generateRandomWeightsHe
        ),
        // Output Layer
        LayerDense(
            tag = "Output",
            layerType = LayerType.OUTPUT,
            activationFunction = Tanh,
            numNeurons = outputLayerNeurons,
            numInputs = outputLayerInputs,
            weightInit = ::generateRandomWeightsXavier
        )
    )
//    */

    val epochs = 100
    val epochStart = nn.epoch

    for (epoch in epochStart..epochs) {
        println("Epoch $epoch/$epochs")

        for (i in featuresList.indices) {
            println("Features Index - $i / ${featuresList.size}")
            nn.train(epoch = epoch, inputs = featuresList[i], targets = targetsList[i])
            nn.cacheAsJson()
        }
    }
}

fun nnTestDrawFullNumberSet(totoType: TotoType) {
    val yearFilter = 2025
    val draws = loadDrawings(totoType).filter { it.year >= yearFilter - 1 }

    val nn = NeuralNetwork(
        totoType = totoType,
        label = "training-${totoType.name}",
        lossFunction = FocalLoss(),
        optimizationFunction = Adam(),
        lRegularizationType = LRegularizationType.L2,
        sleep = true
    )
    nn.restoreFromJson()
    nn.layers.removeIf { it is LayerDropout }

    for (index in draws.indices) {
        if (draws[index].year < yearFilter) continue

        val features = DoubleArray(totoType.totalNumbers) { 0.0 }
        val targets = DoubleArray(totoType.totalNumbers) { 0.0 }

        for (num in 1..totoType.totalNumbers) {
            for (i in index - 1 downTo 0) {
                if (num in draws[i].numbers) {
                    features[num - 1] = (index - i).toDouble()
                    break
                }
            }
        }

        val currentDraw = draws[index].numbers
        currentDraw.forEach { num ->
            targets[num - 1] = 1.0
        }

        normalizeBasedOnMinMax(features)

        val output = nn.forward(features)

        println("Current draw:\n${currentDraw.contentToString()}")
        println("Output:\n${output.contentToString()}")
        println("Target:\n${targets.contentToString()}")

        val outputNegativeCount = output.count { it < nn.positiveOutputThreshold }
        val outputPositiveCount = output.count { it >= nn.positiveOutputThreshold }

        val targetNegativeCount = targets.count { it < nn.positiveTargetThreshold }
        val targetPositiveCount = targets.count { it >= nn.positiveTargetThreshold }

        val outputNegativeMatches = targets.zip(output)
            .count { (t, o) -> t < nn.positiveTargetThreshold && o < nn.positiveOutputThreshold }
        val outputPositiveMatches = targets.zip(output)
            .count { (t, o) -> t >= nn.positiveTargetThreshold && o >= nn.positiveOutputThreshold }

        println("Predicted 0s: $outputNegativeCount")
        println("Predicted 1s: $outputPositiveCount")
        println("Matched 0s: $outputNegativeMatches/$targetNegativeCount")
        println("Matched 1s: $outputPositiveMatches/$targetPositiveCount")
        println("Matched:    ${outputNegativeMatches + outputPositiveMatches}/${targetNegativeCount + targetPositiveCount}")
        println("---")
    }
}

fun nnTrainIndividualNumber(totoType: TotoType, number: Int) {
    val yearFilter = 2019
    val draws = loadDrawings(totoType)
        .filter { it.year >= yearFilter - 1 }
//        .filterIndexed { index, _ -> index % 2 == 0 } // 5x35

    var featuresList = mutableListOf<DoubleArray>()
    val targetsList = mutableListOf<DoubleArray>()

    draws.indices.forEach { index ->
        if (draws[index].year < yearFilter) {
            return@forEach
        }

        val target = DoubleArray(1) { -0.1 }
        if (number in draws[index].numbers) {
            target[0] = 0.5
        }

        val feature = DoubleArray(1) { 0.0 }
        for (i in index - 1 downTo 0) {
            if (number in draws[i].numbers) {
                feature[0] = (index - i).toDouble()
                break
            }
        }

        featuresList.add(feature)
        targetsList.add(target)
    }

    featuresList.removeLast()
    featuresList = minMaxNormalizeByColumn(featuresList)
    targetsList.removeFirst()

    val nn = NeuralNetwork(
        totoType = totoType,
        label = "training-${totoType.name}-$number",
        lossFunction = FocalLoss(targetThreshold = 0.0, gamma = 2.0),
        optimizationFunction = Adam(),
        lRegularizationType = LRegularizationType.L2,
        sleep = true
    )

//    nn.restoreFromJson()
//    /*
    nn.positiveTargetThreshold = 0.0
    nn.positiveOutputThreshold = 0.0

    val hiddenLayer1Neurons = 512
    val hiddenLayer1Inputs = 1
    val dropoutLayer1Rate = 0.4
    val hiddenLayer2Neurons = hiddenLayer1Neurons / 2
    val hiddenLayer2Inputs = hiddenLayer1Neurons
    val dropoutLayer2Rate = 0.2
    val hiddenLayer3Neurons = hiddenLayer2Neurons / 2
    val hiddenLayer3Inputs = hiddenLayer2Neurons
    val outputLayerNeurons = 1
    val outputLayerInputs = hiddenLayer3Neurons

    nn.addLayers(
        // Input Layer is represented by the inputs array
        // Hidden Layer(s)
        LayerDense(
            tag = "Hidden 1",
            layerType = LayerType.HIDDEN,
            activationFunction = ReLU,
            numNeurons = hiddenLayer1Neurons,
            numInputs = hiddenLayer1Inputs,
            weightInit = ::generateRandomWeightsHe
        ),
        LayerDropout(
            tag = "Dropout 1",
            layerType = LayerType.DROPOUT,
            dropoutRate = dropoutLayer1Rate
        ),
        LayerDense(
            tag = "Hidden 2",
            layerType = LayerType.HIDDEN,
            activationFunction = ReLU,
            numNeurons = hiddenLayer2Neurons,
            numInputs = hiddenLayer2Inputs,
            weightInit = ::generateRandomWeightsHe
        ),
        LayerDropout(
            tag = "Dropout 2",
            layerType = LayerType.DROPOUT,
            dropoutRate = dropoutLayer2Rate
        ),
        LayerDense(
            tag = "Hidden 3",
            layerType = LayerType.HIDDEN,
            activationFunction = ReLU,
            numNeurons = hiddenLayer3Neurons,
            numInputs = hiddenLayer3Inputs,
            weightInit = ::generateRandomWeightsHe
        ),
        // Output Layer
        LayerDense(
            tag = "Output",
            layerType = LayerType.OUTPUT,
            activationFunction = Tanh,
            numNeurons = outputLayerNeurons,
            numInputs = outputLayerInputs,
            weightInit = ::generateRandomWeightsXavier
        )
    )
//    */

    val epochs = 100
    val epochStart = nn.epoch

    for (epoch in epochStart..epochs) {
        println("Epoch $epoch/$epochs")

        for (i in featuresList.indices) {
            println("Features Index - $i / ${featuresList.size}")
            nn.train(epoch = epoch, inputs = featuresList[i], targets = targetsList[i])
            nn.cacheAsJson()
        }
    }
}