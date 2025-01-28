package deeplearning

import deeplearning.activation.ReLU
import deeplearning.activation.Sigmoid
import deeplearning.loss.WeightedBinaryCrossEntropy
import deeplearning.model.LayerDense
import deeplearning.model.LayerType
import deeplearning.model.NeuralNetwork
import deeplearning.model.cacheAsJson
import deeplearning.util.Data.appearedInDraw
import deeplearning.util.Data.getDrawFeatures
import deeplearning.util.Data.loadDrawings
import deeplearning.util.Data.normalizeFeatures
import model.TotoType

fun trainNeuralNetwork(totoType: TotoType) {
    val drawings = loadDrawings(totoType)
    val dataSize = if (totoType == TotoType.T_5X35) 2048 else 1024
    val drawingsSubset = drawings.takeLast(dataSize)
    val windowSize = 16

    val nn = NeuralNetwork(
        totoType = totoType,
        label = "training-${totoType.name}",
        lossFunction = WeightedBinaryCrossEntropy
    )

//    nn.restoreFromJson()
//    /*
    val inputLayerNeurons = 3 // Number of features
    val hiddenLayerNeurons = 256
    val outputLayerNeurons = 1

    nn.addLayers(
        // Input Layer is represented by the inputs array
        // Hidden Layer(s)
        LayerDense(
            tag = "Hidden Layer 1",
            layerType = LayerType.HIDDEN,
            activationFunction = ReLU,
            numNeurons = hiddenLayerNeurons,
            numInputs = inputLayerNeurons
        ),
        LayerDense(
            tag = "Hidden Layer 2",
            layerType = LayerType.HIDDEN,
            activationFunction = ReLU,
            numNeurons = hiddenLayerNeurons,
            numInputs = hiddenLayerNeurons
        ),
        LayerDense(
            tag = "Hidden Layer 3",
            layerType = LayerType.HIDDEN,
            activationFunction = ReLU,
            numNeurons = hiddenLayerNeurons,
            numInputs = hiddenLayerNeurons
        ),
        // Output Layer
        LayerDense(
            tag = "Output Layer",
            layerType = LayerType.OUTPUT,
            activationFunction = Sigmoid,
            numNeurons = outputLayerNeurons,
            numInputs = hiddenLayerNeurons
        )
    )
//    */

    val epochs = 100
    val epochStart = nn.epoch
    for (epoch in epochStart..epochs) {
        println("Epoch $epoch/$epochs")

        for (drawIndex in windowSize - 1 until drawingsSubset.size) {
            println("Draw index $drawIndex/${drawingsSubset.size}")

            if (drawIndex + 1 >= drawingsSubset.size) {
                break
            }

            val features: Array<DoubleArray> = Array(totoType.totalNumbers) { doubleArrayOf() }
            val target: Array<DoubleArray> = Array(totoType.totalNumbers) { doubleArrayOf() }

            for (number in 1..totoType.totalNumbers) {
                val numberFeatures: DoubleArray = getDrawFeatures(
                    number = number,
                    draws = drawingsSubset,
                    drawIndex = drawIndex,
                    windowSize = windowSize
                ).let { feature ->
                    doubleArrayOf(
                        feature.frequency,
                        feature.gapSinceLast.toDouble(),
                        feature.poissonProbability
                    )
                }
                features[number - 1] = numberFeatures

                val appearedInNextDraw = appearedInDraw(
                    number = number,
                    draw = drawingsSubset[drawIndex + 1]
                ).toDouble()
                target[number - 1] = doubleArrayOf(appearedInNextDraw)
            }

            val normalizedInput = normalizeFeatures(features)

            nn.train(epoch = epoch, inputs = normalizedInput, targets = target)
            nn.cacheAsJson()
        }
    }
}

