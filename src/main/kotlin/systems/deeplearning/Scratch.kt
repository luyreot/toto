package systems.deeplearning

import extension.replaceBrackets
import model.TotoType
import systems.deeplearning.activation.ReLU
import systems.deeplearning.activation.Sigmoid
import systems.deeplearning.loss.WeightedBinaryCrossEntropy
import systems.deeplearning.model.*
import systems.deeplearning.optimization.Adam
import systems.deeplearning.optimization.LRegularizationType
import systems.deeplearning.util.*
import systems.deeplearning.util.Util.generateRandomWeightsHe
import systems.deeplearning.util.Util.generateRandomWeightsXavier
import util.UniqueIntArray
import util.loadDrawings
import kotlin.random.Random

private const val yearFilter = 2019

private fun getWindowSize(totoType: TotoType): Int {
    val size = 10
    return if (totoType == TotoType.T_5X35) size * 2 else size
}

private fun getTopNNumber(totoType: TotoType): Int = when (totoType) {
    TotoType.T_6X49 -> 42
    TotoType.T_6X42 -> TODO()
    TotoType.T_5X35 -> 28
}

private fun getNumberOfBuckets(totoType: TotoType): Int = when (totoType) {
    TotoType.T_6X49 -> 7
    TotoType.T_6X42 -> TODO()
    TotoType.T_5X35 -> 7
}

private fun getTopNPerBucket(totoType: TotoType): Int = when (totoType) {
    TotoType.T_6X49 -> 6
    TotoType.T_6X42 -> TODO()
    TotoType.T_5X35 -> 4
}

private fun getRecentWindow(totoType: TotoType): Int = when (totoType) {
    TotoType.T_6X49 -> 30
    TotoType.T_6X42 -> TODO()
    TotoType.T_5X35 -> 20
}

fun trainNetwork(totoType: TotoType) {
    val label = "training-${totoType.name}"

    val windowSize = getWindowSize(totoType)
    val inputSize = totoType.totalNumbers * windowSize
    val outputSize = totoType.totalNumbers

    val dense1Input = inputSize
    val dense1Output = 256

    val dense2Input = dense1Output
    val dense2Output = 128

    val dense3Input = dense2Output
    val dense3Output = 32

    val dense4Input = dense3Output
    val dense4Output = outputSize

    val nn = NeuralNetwork(
        totoType = totoType,
        label = label,
        lossFunction = WeightedBinaryCrossEntropy,
        optimizationFunction = Adam(),
        lRegularizationType = LRegularizationType.L2,
        sleep = false
    )

    nn.addLayers(
        LayerDense(
            tag = "Hidden 1",
            layerType = LayerType.HIDDEN,
            activationFunction = ReLU,
            numNeurons = dense1Output,
            numInputs = dense1Input,
            weightInit = ::generateRandomWeightsHe
        ),
        LayerDense(
            tag = "Hidden 2",
            layerType = LayerType.HIDDEN,
            activationFunction = ReLU,
            numNeurons = dense2Output,
            numInputs = dense2Input,
            weightInit = ::generateRandomWeightsHe
        ),
        LayerDense(
            tag = "Hidden 3",
            layerType = LayerType.HIDDEN,
            activationFunction = ReLU,
            numNeurons = dense3Output,
            numInputs = dense3Input,
            weightInit = ::generateRandomWeightsHe
        ),
        LayerDense(
            tag = "Output",
            layerType = LayerType.OUTPUT,
            activationFunction = Sigmoid,
            numNeurons = dense4Output,
            numInputs = dense4Input,
            weightInit = ::generateRandomWeightsXavier
        )
    )

    nn.learningRate = 0.0005
    nn.positiveTargetThreshold = 0.5
    nn.positiveOutputThreshold = 0.1

//    nn.restoreFromJson("$label-r0")
    nn.label += "-r0"

    val draws = loadDrawings(totoType).filter { it.year >= yearFilter - 1 }.map { it.numbers.toList() }
    val trainingData = generateTrainingSamples(totoType.totalNumbers, draws, windowSize)

    val epochs = 2
    val epochStart = if (nn.epoch == 1) nn.epoch else nn.epoch + 1

    for (epoch in epochStart..epochs) {
        println("Epoch $epoch/$epochs")

        for (i in trainingData.indices) {
            println("Features Index - $i / ${trainingData.size - 1}")
            nn.train(epoch = epoch, inputs = trainingData[i].input, targets = trainingData[i].target, print = false)
            nn.cacheAsJson()

            println("---")
        }
    }
}

fun analyzeNetwork(totoType: TotoType) {
    val label = when (totoType) {
        TotoType.T_6X49 -> "training-${totoType.name}-window30-epoch4"
        TotoType.T_6X42 -> TODO()
        TotoType.T_5X35 -> "training-${totoType.name}-window20-epoch4"
    }
    println("Label - $label")

    val nn = NeuralNetwork(
        totoType = totoType,
        label = label,
        lossFunction = WeightedBinaryCrossEntropy,
        optimizationFunction = Adam(),
        lRegularizationType = LRegularizationType.L2,
        sleep = false
    )
    nn.restoreFromJson()

    val draws = loadDrawings(totoType).filter { it.year >= yearFilter - 1 }.map { it.numbers.toList() }

    println("---")

    println("Top N (${getTopNNumber(totoType)}) results:")
    evaluateTopNModelPerformance(
        totoType = totoType,
        draws = draws,
        model = { input -> nn.forward(input) },
        windowSize = getWindowSize(totoType),
        topN = getTopNNumber(totoType)
    )

    println("---")

    println("Top N (${getTopNNumber(totoType)}) rescored results:")
    evaluateRescoredTopNModelPerformance(
        totoType = totoType,
        draws = draws,
        model = { input -> nn.forward(input) },
        windowSize = getWindowSize(totoType),
        topN = getTopNNumber(totoType),
        recentWindow = getRecentWindow(totoType)
    )

    println("---")

    println("Bucketized Top N (${getNumberOfBuckets(totoType) * getTopNPerBucket(totoType)}) results:")
    evaluateBucketizedTopN(
        totoType = totoType,
        draws = draws,
        model = { input -> nn.forward(input) },
        windowSize = getWindowSize(totoType),
        numBuckets = getNumberOfBuckets(totoType),
        topPerBucket = getTopNPerBucket(totoType)
    )
}

fun predictNumbers(totoType: TotoType): List<Int> {
    val label = when (totoType) {
        TotoType.T_6X49 -> "training-${totoType.name}-window30-epoch4"
        TotoType.T_6X42 -> TODO()
        TotoType.T_5X35 -> "training-${totoType.name}-window20-epoch4"
    }
    println("Label - $label")

    val windowSize = getWindowSize(totoType)

    val nn = NeuralNetwork(
        totoType = totoType,
        label = label,
        lossFunction = WeightedBinaryCrossEntropy,
        optimizationFunction = Adam(),
        lRegularizationType = LRegularizationType.L2,
        sleep = false
    )
    nn.restoreFromJson()

    val allDraws = loadDrawings(totoType)
    val draws = allDraws.filter { it.year >= yearFilter - 1 }.map { it.numbers.toList() }

    val input = generateLatestInputOnly(totoType.totalNumbers, draws, windowSize)
    val output = nn.forward(input)

//    predictViaTopN(totoType, output)
//    predictViaRescoredTopN(totoType, output, draws)
//    predictViaBucketizedTopN(totoType, output, draws)
//    return

    val numbersToUse = when (totoType) {
        TotoType.T_6X49 -> predictViaRescoredTopN(totoType, output, draws)
        TotoType.T_6X42 -> TODO()
        TotoType.T_5X35 -> predictViaRescoredTopN(totoType, output, draws)
    }

    return numbersToUse
}

private fun predictViaTopN(totoType: TotoType, output: DoubleArray): List<Int> {
    val topNPredictions = getTopNPredictions(output, getTopNNumber(totoType))
    println("Top N (${getTopNNumber(totoType)}) numbers:")
    println(topNPredictions.sorted().toString().replaceBrackets())

    return topNPredictions
}

private fun predictViaRescoredTopN(totoType: TotoType, output: DoubleArray, draws: List<List<Int>>): List<Int> {
    val topNPredictions = getTopNPredictions(output, getTopNNumber(totoType))
    val frequencyWeights = calculateRecentFrequencyWeights(draws, totoType.totalNumbers, getRecentWindow(totoType))
    val poissonWeights = calculatePoissonGapWeights(draws, totoType.totalNumbers)
    val coOccurrenceWeights = calculateCoOccurrenceBoost(
        draws,
        totoType.totalNumbers,
        topNPredictions,
        getRecentWindow(totoType)
    )

    val blendedWeights = blendWeights(
        frequencyWeights = frequencyWeights,
        poissonWeights = poissonWeights,
        coOccurrenceWeights = coOccurrenceWeights,
        frequencyFactor = 0.5,
        poissonFactor = 0.05,
        coOccurrenceFactor = 0.5
    )

    val rescoredTopNPredictions = getRescoredTopN(output, getTopNNumber(totoType), blendedWeights)
    println("Top N (${getTopNNumber(totoType)}) rescored numbers:")
    println(rescoredTopNPredictions.sorted().toString().replaceBrackets())

    return rescoredTopNPredictions
}

private fun predictViaBucketizedTopN(totoType: TotoType, output: DoubleArray, draws: List<List<Int>>): Set<Int> {
    val selected = bucketizedTopN(totoType, output, getNumberOfBuckets(totoType), getTopNPerBucket(totoType)).toSet()
    println("Bucketized Top N (${getNumberOfBuckets(totoType) * getTopNPerBucket(totoType)}) results:")
    println(selected.sorted().toString().replaceBrackets())

    return selected
}

fun generateCombinations(combinationSize: Int, numberPool: List<Int>, totalCombinations: Int): Set<UniqueIntArray> {
    val combinations = mutableSetOf<UniqueIntArray>()

    combinations.add(UniqueIntArray(numberPool.take(combinationSize).sorted().toIntArray()))
    combinations.add(UniqueIntArray(numberPool.takeLast(combinationSize).sorted().toIntArray()))

    val middlePool = numberPool.drop(combinationSize).dropLast(combinationSize)
    if (middlePool.size < combinationSize) {
        throw IllegalArgumentException("Not enough numbers (${numberPool.size}) in the original pool to generate combinations.")
    }
    if (middlePool.size == combinationSize) {
        combinations.add(UniqueIntArray(middlePool.sorted().toIntArray()))
    } else {
        while (combinations.size < totalCombinations) {
            val pool = middlePool.toMutableList()
            for (i in 0 until combinationSize) {
                val j = Random.nextInt(i, pool.size)
                pool[i] = pool[j].also { pool[j] = pool[i] }
            }
            val candidate = pool.take(combinationSize).sorted().toIntArray()
            combinations.add(UniqueIntArray(candidate))
        }
    }

    println("Generated combinations:")
    combinations.forEach {
        println(it.array.contentToString().replaceBrackets())
    }

    return combinations
}