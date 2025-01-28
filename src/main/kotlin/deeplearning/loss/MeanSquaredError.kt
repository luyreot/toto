package deeplearning.loss

import kotlin.math.pow

/**
 * Mean Squared Error (MSE)
 * Used for regression problems.
 */
object MeanSquaredError : LossFunction {

    override val type: LossFunctionType = LossFunctionType.MeanSquaredError

    fun calculateLoss(predictions: DoubleArray, targets: DoubleArray): Double {
        return targets.zip(predictions) { target, pred ->
            (target - pred).pow(2)
        }.average()
    }

    fun calculateLossDerivative(predictions: DoubleArray, targets: DoubleArray): DoubleArray {
        return predictions.indices.map { i ->
            -2 * (targets[i] - predictions[i])
        }.toDoubleArray()
    }

    /**
     * Predicted and actual are 2D arrays.
     *
     * Compute the error for each input in the batch, then average across the batch.
     * Iterate through the batch: Compute the MSE for each input and accumulate the results.
     * Average across batch: Compute the mean across all the inputs in the batch.
     *
     * It's assumed that predicted and actual are the same size, with dimensions [batch_size][num_classes].
     */
    fun calculateLoss(predictions: Array<DoubleArray>, targets: Array<DoubleArray>): Double {
        // Ensure the sizes of predicted and actual match
        require(predictions.size == targets.size) { "Batch sizes of predicted and actual must match" }

        val totalLoss = predictions.indices.sumOf { batchIndex ->
            targets[batchIndex].zip(predictions[batchIndex]) { target, pred ->
                (target - pred).pow(2)
            }.average()
        }
        return totalLoss / predictions.size // Average loss across the batch
    }

    fun calculateLossDerivative(predictions: Array<DoubleArray>, targets: Array<DoubleArray>): Array<DoubleArray> {
        // Ensure the sizes of predicted and actual match
        require(predictions.size == targets.size) { "Batch sizes of predicted and actual must match" }

        return Array(predictions.size) { batchIndex ->
            predictions[batchIndex].indices.map { i ->
                -2 * (targets[batchIndex][i] - predictions[batchIndex][i])
            }.toDoubleArray()
        }
    }
}