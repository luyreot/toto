package systems.deeplearning

import model.TotoType
import systems.deeplearning.activation.ReLU
import systems.deeplearning.activation.Tanh
import systems.deeplearning.loss.FocalLoss
import systems.deeplearning.model.*
import systems.deeplearning.optimization.Adam
import systems.deeplearning.optimization.LRegularizationType
import systems.deeplearning.util.Data.loadDrawings
import systems.deeplearning.util.Data.normalizeBasedOnMinMax
import systems.deeplearning.util.Util.generateRandomWeightsHe
import systems.deeplearning.util.Util.generateRandomWeightsXavier

fun trainNeuralNetworkBatch(totoType: TotoType) {
    val yearFilter = 2019
    val draws = loadDrawings(totoType).filter { it.year >= yearFilter - 1 }
    val batchSize = 10

    val featuresSingle = mutableListOf<DoubleArray>()
    val targetsSingle = mutableListOf<DoubleArray>()

    draws.indices.forEach { index ->
        if (draws[index].year < yearFilter) {
            return@forEach
        }

        val features = DoubleArray(totoType.totalNumbers) { 0.0 }
        val targets = DoubleArray(totoType.totalNumbers) { 0.0 }

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

//        normalizeBasedOnMeanVariance(features)
        featuresSingle.add(features)
        targetsSingle.add(targets)
    }

    featuresSingle.removeLast()
    normalizeBasedOnMinMax(featuresSingle)
    targetsSingle.removeFirst()

    val nn = NeuralNetwork(
        totoType = totoType,
        label = "training-${totoType.name}",
        lossFunction = FocalLoss(targetThreshold = 0.5, gamma = 2.0),
        optimizationFunction = Adam(),
        lRegularizationType = LRegularizationType.L2,
        sleep = true
    )

    nn.learningRate = 0.001
    nn.positiveOutputThreshold = 0.0

//    nn.restoreFromJson()

//    /*
    val hiddenLayer1Neurons = 512
    val hiddenLayer1Inputs = totoType.totalNumbers
    val dropoutLayer1Rate = 0.5
    val hiddenLayer2Neurons = 256
    val hiddenLayer2Inputs = hiddenLayer1Neurons
    val dropoutLayer2Rate = 0.4
    val hiddenLayer3Neurons = 128
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
//    nn.optimizeOutputLayerBiasesForBinaryImbalances()

    val epochs = 100
    val epochStart = nn.epoch
    for (epoch in epochStart..epochs) {
        println("Epoch $epoch/$epochs")

        for (i in featuresSingle.indices) {
            println("Features Index - $i / ${featuresSingle.size}")
            nn.train(epoch = epoch, inputs = featuresSingle[i], targets = targetsSingle[i])
            nn.cacheAsJson()
        }
    }
}