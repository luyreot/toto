package deeplearning.loss

import kotlin.math.ln

object BinaryCrossEntropy : LossFunction {

    override val type: LossFunctionType = LossFunctionType.BinaryCrossEntropy

    fun calculateLoss(predictions: Double, targets: Double): Double {
        val epsilon = 1e-7 // To avoid log(0)
        val clampedPrediction = predictions.coerceIn(epsilon, 1.0 - epsilon)
        return -(targets * ln(clampedPrediction) + (1 - targets) * ln(1 - clampedPrediction))
    }

    fun calculateLoss(predictions: DoubleArray, targets: DoubleArray): Double {
        require(predictions.size == targets.size) { "Predictions and targets must have the same size." }

        val epsilon = 1e-7 // To avoid log(0)
        var totalLoss = 0.0

        for (i in predictions.indices) {
            val clampedPrediction = predictions[i].coerceIn(epsilon, 1.0 - epsilon)
            totalLoss += -(targets[i] * ln(clampedPrediction) +
                    (1 - targets[i]) * ln(1 - clampedPrediction))
        }

        return totalLoss / predictions.size // Return average loss for the batch
    }

    fun calculateLoss(predictions: Array<DoubleArray>, targets: Array<DoubleArray>): Double {
        require(predictions.size == targets.size) { "Predictions and targets must have the same number of samples (batch size)." }
        val epsilon = 1e-7 // To avoid log(0)

        var totalLoss = 0.0
        for (i in predictions.indices) {
            require(predictions[i].size == targets[i].size) { "Prediction and target vectors must have the same size for sample $i." }
            for (j in predictions[i].indices) {
                val clampedPrediction = predictions[i][j].coerceIn(epsilon, 1.0 - epsilon)
                totalLoss += -(targets[i][j] * kotlin.math.ln(clampedPrediction) +
                        (1 - targets[i][j]) * kotlin.math.ln(1 - clampedPrediction))
            }
        }

        return totalLoss / predictions.size // Average loss across the batch
    }

    fun calculateGradient(predictions: Double, targets: Double): Double {
        val epsilon = 1e-7
        val clampedPrediction = predictions.coerceIn(epsilon, 1.0 - epsilon)
        return clampedPrediction - targets
    }

    fun calculateGradient(predictions: DoubleArray, targets: DoubleArray): DoubleArray {
        require(predictions.size == targets.size) { "Predictions and targets must have the same size." }

        val epsilon = 1e-7
        return DoubleArray(predictions.size) { i ->
            val clampedPrediction = predictions[i].coerceIn(epsilon, 1.0 - epsilon)
            clampedPrediction - targets[i]
        }
    }

    fun calculateGradient(predictions: Array<DoubleArray>, targets: Array<DoubleArray>): Array<DoubleArray> {
        require(predictions.size == targets.size) { "Predictions and targets must have the same number of samples (batch size)." }
        val epsilon = 1e-7 // To avoid division by zero or log(0)

        return Array(predictions.size) { i ->
            require(predictions[i].size == targets[i].size) { "Prediction and target vectors must have the same size for sample $i." }
            DoubleArray(predictions[i].size) { j ->
                val clampedPrediction = predictions[i][j].coerceIn(epsilon, 1.0 - epsilon)
                clampedPrediction - targets[i][j]
            }
        }
    }
}