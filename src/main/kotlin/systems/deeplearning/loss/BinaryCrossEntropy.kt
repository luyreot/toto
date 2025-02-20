package systems.deeplearning.loss

import kotlin.math.ln

object BinaryCrossEntropy : LossFunction {

    override val type: LossFunctionType = LossFunctionType.BinaryCrossEntropy

    fun calculateLoss(predictions: DoubleArray, targets: DoubleArray): Double {
        require(predictions.size == targets.size) { "Predictions and targets must have the same size." }

        val epsilon = 1e-7 // Prevent log(0) instability
        var totalLoss = 0.0
        var totalCount = 0 // Count total elements for proper averaging

        for (i in predictions.indices) {
            val target = targets[i]
            val prediction = predictions[i].coerceIn(epsilon, 1.0 - epsilon)

            // Binary Cross-Entropy Loss
            val loss = -(target * ln(prediction) + (1 - target) * ln(1 - prediction))

            totalLoss += loss
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
                val target = targets[i][j]
                val prediction = predictions[i][j].coerceIn(epsilon, 1.0 - epsilon)

                // Binary Cross-Entropy Loss
                val loss = -(target * ln(prediction) + (1 - target) * ln(1 - prediction))

                totalLoss += loss
                totalCount++
            }
        }

        return totalLoss / totalCount // Properly averaged loss
    }

    fun calculateGradient(predictions: DoubleArray, targets: DoubleArray): DoubleArray {
        require(predictions.size == targets.size) { "Predictions and targets must have the same size." }

        val epsilon = 1e-7

        return DoubleArray(predictions.size) { i ->
            val target = targets[i]
            val prediction = predictions[i].coerceIn(epsilon, 1.0 - epsilon)
            val gradient = (prediction - target) / (prediction * (1 - prediction))

            gradient
        }
    }

    fun calculateGradient(predictions: Array<DoubleArray>, targets: Array<DoubleArray>): Array<DoubleArray> {
        require(predictions.size == targets.size) { "Predictions and targets must have the same number of samples (batch size)." }

        val epsilon = 1e-7 // Small value to prevent log(0) or division by zero
        val batchSize = predictions.size

        val gradients = Array(batchSize) { i ->
            require(predictions[i].size == targets[i].size) { "Prediction and target vectors must have the same size for sample $i." }
            DoubleArray(predictions[i].size) { j ->
                val target = targets[i][j]
                val prediction = predictions[i][j].coerceIn(epsilon, 1.0 - epsilon)
                val gradient = (prediction - target) / (prediction * (1 - prediction))

                gradient
            }
        }

        return gradients
    }
}