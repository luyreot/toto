package deeplearning

import deeplearning.activation.ReLU
import deeplearning.activation.Sigmoid
import deeplearning.loss.WeightedBinaryCrossEntropy
import deeplearning.model.LayerDense
import deeplearning.model.LayerType
import deeplearning.model.NeuralNetwork
import deeplearning.model.cacheAsJson
import deeplearning.optimization.SGD
import deeplearning.util.Data.getDrawFeatures
import deeplearning.util.Data.loadDrawings
import deeplearning.util.Data.normalizeBasedOnMeanVariance
import model.TotoType

fun trainNeuralNetwork(totoType: TotoType) {
    val nn = NeuralNetwork(
        totoType = totoType,
        label = "training-${totoType.name}",
        lossFunction = WeightedBinaryCrossEntropy,
        optimizationFunction = SGD,
        sleep = false
    )

//    nn.restoreFromJson()
//    /*
    val inputLayerNeurons = 3 // Number of features
    val hiddenLayerNeurons = 128
    val outputLayerNeurons = 1

    nn.addLayers(
        // Input Layer is represented by the inputs array
        // Hidden Layer(s)
        LayerDense(
            tag = "Hidden 1",
            layerType = LayerType.HIDDEN,
            activationFunction = ReLU,
            numNeurons = hiddenLayerNeurons,
            numInputs = inputLayerNeurons
        ),
        LayerDense(
            tag = "Hidden 2",
            layerType = LayerType.HIDDEN,
            activationFunction = ReLU,
            numNeurons = hiddenLayerNeurons,
            numInputs = hiddenLayerNeurons
        ),
        LayerDense(
            tag = "Hidden 3",
            layerType = LayerType.HIDDEN,
            activationFunction = ReLU,
            numNeurons = hiddenLayerNeurons,
            numInputs = hiddenLayerNeurons
        ),
        LayerDense(
            tag = "Hidden 4",
            layerType = LayerType.HIDDEN,
            activationFunction = ReLU,
            numNeurons = hiddenLayerNeurons,
            numInputs = hiddenLayerNeurons
        ),
        LayerDense(
            tag = "Hidden 5",
            layerType = LayerType.HIDDEN,
            activationFunction = ReLU,
            numNeurons = hiddenLayerNeurons,
            numInputs = hiddenLayerNeurons
        ),
        LayerDense(
            tag = "Hidden 6",
            layerType = LayerType.HIDDEN,
            activationFunction = ReLU,
            numNeurons = hiddenLayerNeurons,
            numInputs = hiddenLayerNeurons
        ),
        // Output Layer
        LayerDense(
            tag = "Output",
            layerType = LayerType.OUTPUT,
            activationFunction = Sigmoid,
            numNeurons = outputLayerNeurons,
            numInputs = hiddenLayerNeurons
        )
    )
//    */
//    nn.optimizeOutputLayerBiasesForBinaryImbalances()

    val drawings = loadDrawings(totoType)

    val number = 1
    val indexes = drawings.mapIndexedNotNull { index, draw ->
        if (draw.numbers.contains(number)) index else null
    }

    val features = mutableListOf<DoubleArray>()
    val targets = mutableListOf<DoubleArray>()
    for (i in indexes.indices) {
        if (indexes[i] == 0) continue

        val index = indexes[i]

        getDrawFeatures(
            number = number,
            draws = drawings,
            drawIndex = index - 1,
            windowSize = Int.MAX_VALUE
        ).let {
            features.add(
                doubleArrayOf(
                    it.frequency,
                    it.gapSinceLast.toDouble(),
                    it.poissonProbability,
//                    it.inDraw.toDouble()
                )
            )
        }
        targets.add(doubleArrayOf(1.0))

        // last
        if (index == drawings.size - 1) {
            break
        }

        getDrawFeatures(
            number = number,
            draws = drawings,
            drawIndex = index,
            windowSize = Int.MAX_VALUE
        ).let {
            features.add(
                doubleArrayOf(
                    it.frequency,
                    it.gapSinceLast.toDouble(),
                    it.poissonProbability,
//                    it.inDraw.toDouble()
                )
            )
        }
        targets.add(
            doubleArrayOf(
                if (drawings[index + 1].numbers.contains(number)) 1.0 else 0.0
            )
        )

        // last
//        if (index + 1 == drawings.size - 1) {
//            break
//        }
//
//        getDrawFeatures(
//            number = number,
//            draws = drawings,
//            drawIndex = index + 1,
//            windowSize = Int.MAX_VALUE
//        ).let {
//            features.add(
//                doubleArrayOf(
//                    it.frequency,
//                    it.gapSinceLast.toDouble(),
//                    it.poissonProbability,
//                    it.inDraw.toDouble()
//                )
//            )
//        }
//        targets.add(
//            doubleArrayOf(
//                if (drawings[index + 2].numbers.contains(number)) 1.0 else 0.0
//            )
//        )
    }
    normalizeBasedOnMeanVariance(features)
//    smoothTargets(0.001, targets)

    val epochs = 100
    val epochStart = nn.epoch
    for (epoch in epochStart..epochs) {
        println("Epoch $epoch/$epochs")

        features.forEachIndexed { index, input ->
            nn.train(epoch = epoch, inputs = input, targets = targets[index])
            nn.cacheAsJson()
        }
    }
}

