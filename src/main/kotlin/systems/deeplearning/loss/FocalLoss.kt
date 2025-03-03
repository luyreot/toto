package systems.deeplearning.loss

import kotlin.math.ln
import kotlin.math.pow

/**
 * Focal Loss downweights easy negatives and focuses on hard positives.
 * [gamma] controls focus strength (higher = more focus on hard samples).
 * Helps in imbalanced classification where most outputs are 0s.
 */
data class FocalLoss(
    var targetThreshold: Double = 1.0,
    var gamma: Double = 2.0
) : LossFunction {

    override val type: LossFunctionType = LossFunctionType.FocalLoss

    fun calculateLoss(predictions: DoubleArray, targets: DoubleArray): Double {
        require(predictions.size == targets.size) { "Predictions and targets must have the same size." }

        val epsilon = 1e-7  // Prevent log(0) issues
        var loss = 0.0

        for (i in predictions.indices) {
            val target = targets[i]
            val p = predictions[i].coerceIn(epsilon, 1.0 - epsilon)  // Clamping to avoid log(0)

            val pT = if (target >= targetThreshold) p else (1 - p)  // pT = p if y=1, else 1-p
            val focalWeight = (1 - pT).pow(gamma)  // Apply focal weighting

            loss += -focalWeight * (target * ln(p) + (1 - target) * ln(1 - p))  // Standard BCE weighted
        }

        // Return mean loss
        return loss / predictions.size
    }

    fun calculateLoss(predictions: Array<DoubleArray>, targets: Array<DoubleArray>): Double {
        require(predictions.size == targets.size) { "Predictions and targets must have the same number of samples." }

        val batchSize = predictions.size
        var totalLoss = 0.0

        for (i in predictions.indices) {
            totalLoss += calculateLoss(predictions[i], targets[i])
        }

        // Return mean batch loss
        return totalLoss / batchSize
    }

    fun calculateGradient(predictions: DoubleArray, targets: DoubleArray): DoubleArray {
        require(predictions.size == targets.size) { "Predictions and targets must have the same size." }

        val epsilon = 1e-7
        return DoubleArray(predictions.size) { i ->
            val target = targets[i]
            val p = predictions[i].coerceIn(epsilon, 1.0 - epsilon)

            val pT = if (target >= targetThreshold) p else (1 - p)
            val focalWeight = (1 - pT).pow(gamma)

            val gradient = focalWeight * (p - target) // Adjusted gradient

            gradient
        }
    }

    fun calculateGradient(predictions: Array<DoubleArray>, targets: Array<DoubleArray>): Array<DoubleArray> {
        require(predictions.size == targets.size) { "Predictions and targets must have the same number of samples." }

        return Array(predictions.size) { i ->
            calculateGradient(predictions[i], targets[i])
        }
    }
}