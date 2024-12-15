package deeplearning

import kotlin.math.ln
import kotlin.math.pow

/**
 * Loss functions measure how well the network's predictions match the actual target values.
 */
object LossFunctions {

    /**
     * Categorical Cross-Entropy
     * Used for classification problems where outputs are probabilities.
     */
    fun categoricalCrossEntropy(predicted: DoubleArray, actual: DoubleArray): Double {
        return -actual.zip(predicted) { target, pred ->
            target * ln(pred.coerceAtLeast(1e-15)) // Avoid log(0)
        }.sum()
    }

    /**
     * The predicted and actual values will each be an array of arrays (a 2D array), where:
     * - each row in predicted corresponds to the model's predicted probabilities for one input.
     * - each row in actual corresponds to the true one-hot encoded target for that input.
     *
     * Sum the individual losses: Compute the cross-entropy for each pair (predRow, targetRow) and sum the results.
     * Average the loss: Divide the total loss by the number of samples in the batch.
     *
     * * It's assumed that predicted and actual are the same size, with dimensions [batch_size][num_classes].
     */
    fun categoricalCrossEntropyBatch(predicted: Array<DoubleArray>, actual: Array<DoubleArray>): Double {
        var totalLoss = 0.0
        for ((predRow, targetRow) in predicted.zip(actual)) {
            totalLoss += -targetRow.zip(predRow) { target, pred ->
                target * ln(pred.coerceAtLeast(1e-15)) // Avoid log(0)
            }.sum()
        }
        return totalLoss / predicted.size // Average loss across the batch
    }

    /**
     * Mean Squared Error (MSE)
     * Used for regression problems.
     */
    fun meanSquaredError(predicted: DoubleArray, actual: DoubleArray): Double {
        return actual.zip(predicted) { target, pred ->
            (target - pred).pow(2)
        }.average()
    }

    /**
     * predicted and actual are 2D arrays.
     *
     * Compute the error for each input in the batch, then average across the batch.
     * Iterate through the batch: Compute the MSE for each input and accumulate the results.
     * Average across batch: Compute the mean across all the inputs in the batch.
     *
     * It's assumed that predicted and actual are the same size, with dimensions [batch_size][num_classes].
     */
    fun meanSquaredErrorBatch(predicted: Array<DoubleArray>, actual: Array<DoubleArray>): Double {
        var totalLoss = 0.0
        for ((predRow, targetRow) in predicted.zip(actual)) {
            totalLoss += targetRow.zip(predRow) { target, pred ->
                (target - pred).pow(2)
            }.average() // MSE for each input
        }
        return totalLoss / predicted.size // Average MSE across the batch
    }
}