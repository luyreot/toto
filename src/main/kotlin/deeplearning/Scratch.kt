package deeplearning

import deeplearning.model.NeuralNetwork
import deeplearning.model.cacheAsJson
import deeplearning.model.restoreFromJson
import deeplearning.util.Data.appearedInDraw
import deeplearning.util.Data.getDrawFeatures
import deeplearning.util.Data.loadDrawings
import deeplearning.util.Data.normalizeFeatures
import model.TotoType

fun trainNeuralNetwork(totoType: TotoType) {
    val drawings = loadDrawings(totoType)
    val dataSize = if (totoType == TotoType.T_5X35) 1024 else 512
    val drawingsSubset = drawings.takeLast(dataSize)
    val windowSize = 32

    val nn = NeuralNetwork(label = "training-${totoType.name}")

    nn.restoreFromJson()
    val epochs = nn.epoch
    /*
    val inputLayerNeurons = 3 // Number of features
    val hiddenLayerNeurons = 512
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
        // Output Layer
        LayerDense(
            tag = "Output Layer",
            layerType = LayerType.OUTPUT,
            activationFunction = Sigmoid,
            numNeurons = outputLayerNeurons,
            numInputs = hiddenLayerNeurons
        )
    )
    val epochs = 100
    */

    for (epoch in 0..epochs) {
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

//            nn.updateLearningRate(0.1)
            nn.train(epoch = epoch, inputs = normalizedInput, targets = target)
            nn.cacheAsJson()
        }
    }
}

