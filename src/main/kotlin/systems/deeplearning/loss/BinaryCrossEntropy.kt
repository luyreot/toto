package systems.deeplearning.loss

import kotlin.math.ln

object BinaryCrossEntropy : LossFunction {

    override val type: LossFunctionType = LossFunctionType.BinaryCrossEntropy

    private var w1 = 2.0 // Weight for target = 1 errors
    private var w0 = 1.0  // Weight for target = 0 errors

    fun calculateLoss(predictions: DoubleArray, targets: DoubleArray): Double {
        require(predictions.size == targets.size) { "Predictions and targets must have the same size." }

        val epsilon = 1e-7 // Prevent log(0) instability
        var totalLoss = 0.0
        var totalCount = 0 // Count total elements for proper averaging

        for (i in predictions.indices) {
            val y = targets[i]
            val yPred = predictions[i].coerceIn(epsilon, 1.0 - epsilon)

            // Binary Cross-Entropy Loss
            val loss = -(y * ln(yPred) + (1 - y) * ln(1 - yPred))

            // Apply class weighting
            totalLoss += if (y == 1.0) w1 * loss else w0 * loss
            totalCount++
        }

        return totalLoss / totalCount // Properly averaged loss
    }

    fun calculateLoss(predictions: Array<DoubleArray>, targets: Array<DoubleArray>): Double {
        require(predictions.size == targets.size) { "Predictions and targets must have the same number of samples (batch size)." }

        val epsilon = 1e-7 // To avoid log(0) instability
        var totalLoss = 0.0
        var totalCount = 0 // Track total elements for proper averaging

        for (i in predictions.indices) {
            require(predictions[i].size == targets[i].size) { "Prediction and target vectors must have the same size for sample $i." }
            for (j in predictions[i].indices) {
                val y = targets[i][j]
                val yPred = predictions[i][j].coerceIn(epsilon, 1.0 - epsilon)

                // Binary Cross-Entropy Loss
                val loss = -(y * ln(yPred) + (1 - y) * ln(1 - yPred))

                // Apply class weighting
                totalLoss += if (y == 1.0) w1 * loss else w0 * loss
                totalCount++
            }
        }

        return totalLoss / totalCount // Properly averaged loss
    }

    fun calculateGradient(predictions: DoubleArray, targets: DoubleArray): DoubleArray {
        require(predictions.size == targets.size) { "Predictions and targets must have the same size." }

        val epsilon = 1e-7

        return DoubleArray(predictions.size) { i ->
            val y = targets[i]
            val yPred = predictions[i].coerceIn(epsilon, 1.0 - epsilon)

            // Compute weighted gradient
            val gradient = yPred - y
            if (y == 1.0) gradient * w1 else gradient * w0
        }
    }

    fun calculateGradient(predictions: Array<DoubleArray>, targets: Array<DoubleArray>): Array<DoubleArray> {
        require(predictions.size == targets.size) { "Predictions and targets must have the same number of samples (batch size)." }

        val epsilon = 1e-7 // Small value to prevent log(0) or division by zero
        val batchSize = predictions.size

        var ones = 0
        var zeros = 0

        val gradients = Array(batchSize) { i ->
            require(predictions[i].size == targets[i].size) { "Prediction and target vectors must have the same size for sample $i." }
            DoubleArray(predictions[i].size) { j ->
                val y = targets[i][j]
                val yPred = predictions[i][j].coerceIn(epsilon, 1.0 - epsilon)

                if (yPred > 0.5) ones++ else zeros++

                // Compute BCE gradient correctly
                val grad = (yPred - y) / (yPred * (1 - yPred))

                // Apply class weighting
                if (y == 1.0) w1 * grad else w0 * grad
            }
        }

        if (ones > 6) {
            targets
                .mapIndexedNotNull { index, target -> if (target.first() == 0.0) index else null }
                .forEach { index ->
                    gradients[index][0] = -10.0
                }
        }

        if (ones < 6) {
            targets
                .mapIndexedNotNull { index, target -> if (target.first() == 1.0) index else null }
                .forEach { index ->
                    gradients[index][0] = -10.0
                }
        }

        return gradients
    }
}