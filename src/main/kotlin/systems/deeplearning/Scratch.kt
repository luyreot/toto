package systems.deeplearning

import model.TotoType
import systems.deeplearning.activation.ReLU
import systems.deeplearning.activation.Sigmoid
import systems.deeplearning.loss.BinaryCrossEntropy
import systems.deeplearning.model.*
import systems.deeplearning.optimization.Adam
import systems.deeplearning.util.Data.getDrawFeatures
import systems.deeplearning.util.Data.loadDrawings
import systems.deeplearning.util.Data.normalizeBasedOnMeanVariance
import systems.deeplearning.util.Util.generateRandomWeights
import systems.deeplearning.util.Util.generateRandomWeightsXavier

fun predictNeuralNetwork(totoType: TotoType) {
    val draws = loadDrawings(totoType)

    val subdraws = draws

    val lastIndex = subdraws.size - 1
    val results = mutableMapOf<Int, Double>()

    for (number in 1..totoType.totalNumbers) {
        val nn = NeuralNetwork(
            totoType = totoType,
            label = "training-${totoType.name}-$number",
            lossFunction = BinaryCrossEntropy,
            optimizationFunction = Adam(),
            sleep = false
        )
        nn.restoreFromJson()

        val features = getDrawFeatures(
            number = number,
            draws = subdraws,
            drawIndex = lastIndex
        ).let {
            doubleArrayOf(
                it.frequency,
                it.gapSinceLast.toDouble(),
                it.poissonProbability,
                it.inDraw.toDouble()
            )
        }

        val output = nn.forward(features)
        results[number] = output.first()
    }

    val numbersNotInNextDraw = results.filter { it.value == 0.0 }.map { it.key }
    val numbersInNextDraw = results.filter { it.value == 1.0 }.map { it.key }

    println("Numbers not in next draw:")
    println(numbersNotInNextDraw.toList())
    println("Numbers in next draw:")
    println(numbersInNextDraw.toList())
}

fun trainNeuralNetworkBatch(totoType: TotoType) {
    val nn = NeuralNetwork(
        totoType = totoType,
        label = "training-${totoType.name}",
        lossFunction = BinaryCrossEntropy,
        optimizationFunction = Adam(),
        sleep = true
    )

    val inputLayerNeurons = 4 // Number of features
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
            numInputs = inputLayerNeurons,
            weightInit = ::generateRandomWeights
        ),
        LayerDense(
            tag = "Hidden 2",
            layerType = LayerType.HIDDEN,
            activationFunction = ReLU,
            numNeurons = hiddenLayerNeurons,
            numInputs = hiddenLayerNeurons,
            weightInit = ::generateRandomWeights
        ),
        LayerDense(
            tag = "Hidden 3",
            layerType = LayerType.HIDDEN,
            activationFunction = ReLU,
            numNeurons = hiddenLayerNeurons,
            numInputs = hiddenLayerNeurons,
            weightInit = ::generateRandomWeights
        ),
        LayerDense(
            tag = "Hidden 4",
            layerType = LayerType.HIDDEN,
            activationFunction = ReLU,
            numNeurons = hiddenLayerNeurons,
            numInputs = hiddenLayerNeurons,
            weightInit = ::generateRandomWeights
        ),
        // Output Layer
        LayerDense(
            tag = "Output",
            layerType = LayerType.OUTPUT,
            activationFunction = Sigmoid,
            numNeurons = outputLayerNeurons,
            numInputs = hiddenLayerNeurons,
            weightInit = ::generateRandomWeights
        )
    )

//    nn.optimizeOutputLayerBiasesForBinaryImbalances()

    val draws = loadDrawings(totoType)
    val features = mutableListOf<Array<DoubleArray>>()
    val targets = mutableListOf<Array<DoubleArray>>()

    draws.indices.forEach { index ->
        if (index >= draws.size - 1) {
            return@forEach
        }

        val featuresArr = Array(totoType.totalNumbers) { doubleArrayOf() }
        val targetsArr = Array(totoType.totalNumbers) { doubleArrayOf() }

        for (number in 1..totoType.totalNumbers) {
            val drawFeatures = getDrawFeatures(
                number = number,
                draws = draws,
                drawIndex = index
            )

            if (drawFeatures.containsNan()) {
                return@forEach
            }

            val featuresArray = drawFeatures.toDoubleArray()
            normalizeBasedOnMeanVariance(featuresArray)
            val inNextDraw = if (draws[index + 1].numbers.contains(number)) 1.0 else 0.0

            featuresArr[number - 1] = featuresArray
            targetsArr[number - 1] = doubleArrayOf(inNextDraw)
        }

        features.add(featuresArr)
        targets.add(targetsArr)
    }

    val epochs = 100
    val epochStart = nn.epoch
    for (epoch in epochStart..epochs) {
        println("Epoch $epoch/$epochs")

        features.forEachIndexed { index, input ->
            println("Features Index - $index / ${features.size}")
            nn.train(epoch = epoch, inputs = input, targets = targets[index])
            nn.cacheAsJson()
        }
    }
}

fun trainNeuralNetworkForIndividualNumbers(totoType: TotoType) {
    val draws = loadDrawings(totoType)

    for (number in 1..totoType.totalNumbers) {
        println("Number - $number")

        val nn = NeuralNetwork(
            totoType = totoType,
            label = "training-${totoType.name}-$number",
            lossFunction = BinaryCrossEntropy,
            optimizationFunction = Adam(),
            sleep = true
        )

//        nn.restoreFromJson()
//        /*
        val inputLayerNeurons = 4 // Number of features
        val hiddenLayerNeurons = 256
        val outputLayerNeurons = 1

        nn.addLayers(
            // Input Layer is represented by the inputs array
            // Hidden Layer(s)
            LayerDense(
                tag = "Hidden 1",
                layerType = LayerType.HIDDEN,
                activationFunction = ReLU,
                numNeurons = hiddenLayerNeurons,
                numInputs = inputLayerNeurons,
                weightInit = ::generateRandomWeights
            ),
            LayerDense(
                tag = "Hidden 2",
                layerType = LayerType.HIDDEN,
                activationFunction = ReLU,
                numNeurons = hiddenLayerNeurons,
                numInputs = hiddenLayerNeurons,
                weightInit = ::generateRandomWeights
            ),
            LayerDense(
                tag = "Hidden 3",
                layerType = LayerType.HIDDEN,
                activationFunction = ReLU,
                numNeurons = hiddenLayerNeurons,
                numInputs = hiddenLayerNeurons,
                weightInit = ::generateRandomWeights
            ),
            LayerDense(
                tag = "Hidden 4",
                layerType = LayerType.HIDDEN,
                activationFunction = ReLU,
                numNeurons = hiddenLayerNeurons,
                numInputs = hiddenLayerNeurons,
                weightInit = ::generateRandomWeights
            ),
            LayerDense(
                tag = "Hidden 5",
                layerType = LayerType.HIDDEN,
                activationFunction = ReLU,
                numNeurons = hiddenLayerNeurons,
                numInputs = hiddenLayerNeurons,
                weightInit = ::generateRandomWeights
            ),
            LayerDense(
                tag = "Hidden 6",
                layerType = LayerType.HIDDEN,
                activationFunction = ReLU,
                numNeurons = hiddenLayerNeurons,
                numInputs = hiddenLayerNeurons,
                weightInit = ::generateRandomWeights
            ),
            // Output Layer
            LayerDense(
                tag = "Output",
                layerType = LayerType.OUTPUT,
                activationFunction = Sigmoid,
                numNeurons = outputLayerNeurons,
                numInputs = hiddenLayerNeurons,
                weightInit = ::generateRandomWeightsXavier
            )
        )
//        */
//        nn.optimizeOutputLayerBiasesForBinaryImbalances()

        val indexes = draws.mapIndexedNotNull { index, draw ->
            if (draw.numbers.contains(number)) index else null
        }

        val features = mutableListOf<DoubleArray>()
        val targets = mutableListOf<DoubleArray>()
        for (i in indexes.indices) {
            if (indexes[i] == 0) continue

            val index = indexes[i]

            var feature = getDrawFeatures(
                number = number,
                draws = draws,
                drawIndex = index - 1
            )
            if (!feature.containsNan()) {
                features.add(feature.toDoubleArray())
                targets.add(doubleArrayOf(1.0))
            }

            // last
            if (index == draws.size - 1) {
                break
            }

            feature = getDrawFeatures(
                number = number,
                draws = draws,
                drawIndex = index
            )
            if (!feature.containsNan()) {
                features.add(feature.toDoubleArray())
                targets.add(
                    doubleArrayOf(
                        if (draws[index + 1].numbers.contains(number)) 1.0 else 0.0
                    )
                )
            }
        }

        normalizeBasedOnMeanVariance(features)
//        smoothTargets(0.001, targets)

        val epochs = 100
        val epochStart = nn.epoch
        for (epoch in epochStart..epochs) {
            println("Epoch $epoch/$epochs")

            features.forEachIndexed { index, input ->
                println("Features Index - $index / ${features.size}")
                nn.train(epoch = epoch, inputs = input, targets = targets[index])
                nn.cacheAsJson()
            }

            break
        }
    }
}

