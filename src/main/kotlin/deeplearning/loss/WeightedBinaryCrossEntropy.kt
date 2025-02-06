package deeplearning.loss

import kotlin.math.ln

object WeightedBinaryCrossEntropy : LossFunction {

    override val type: LossFunctionType = LossFunctionType.WeightedBinaryCrossEntropy

    fun calculateLoss(predictions: DoubleArray, targets: DoubleArray, weights: DoubleArray): Double {
        val epsilon = 1e-7
        return predictions.indices.sumOf { i ->
            val prediction = predictions[i].coerceIn(epsilon, 1 - epsilon) // Clamping to avoid log(0)
            val target = targets[i]
            val weight = weights[i] // Weight for each target
            -weight * (target * ln(prediction) + (1 - target) * ln(1 - prediction))
        } / predictions.size
    }

    fun calculateLoss(
        predictions: Array<DoubleArray>,
        targets: Array<DoubleArray>,
        weights: Array<DoubleArray>
    ): Double {
        val epsilon = 1e-7 // To prevent log(0)
        var totalLoss = 0.0

        for (i in predictions.indices) {
            for (j in predictions[i].indices) {
                val prediction = predictions[i][j].coerceIn(epsilon, 1 - epsilon)
                val target = targets[i][j]
                val weight = weights[i][j]
                totalLoss += -weight * (target * ln(prediction) + (1 - target) * ln(1 - prediction))
            }
        }
        return totalLoss / predictions.size // Average loss across all data points in the batch
    }

    fun calculateGradient(
        predictions: DoubleArray,
        targets: DoubleArray,
        weights: DoubleArray
    ): DoubleArray {
        val epsilon = 1e-7 // To prevent division by zero
        return DoubleArray(predictions.size) { j ->
            val prediction = predictions[j].coerceIn(epsilon, 1 - epsilon)
            val target = targets[j]
            val weight = weights[j]
            weight * (prediction - target) / (prediction * (1 - prediction))
        }
    }

    fun calculateGradient(
        predictions: Array<DoubleArray>,
        targets: Array<DoubleArray>,
        weights: Array<DoubleArray>
    ): Array<DoubleArray> {
        val epsilon = 1e-7 // To prevent division by zero
        return Array(predictions.size) { i ->
            DoubleArray(predictions[i].size) { j ->
                val prediction = predictions[i][j].coerceIn(epsilon, 1 - epsilon)
                val target = targets[i][j]
                val weight = weights[i][j]
                weight * (prediction - target) / (prediction * (1 - prediction))
            }
        }
    }
}