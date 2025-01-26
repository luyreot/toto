package deeplearning.loss

import kotlin.math.ln
import kotlin.math.pow

/**
 * Loss functions measure how well the network's predictions match the actual target values.
 */
object LossFunctions {

    fun binaryCrossEntropyLoss(prediction: Double, target: Double): Double {
        val epsilon = 1e-7 // To avoid log(0)
        val clampedPrediction = prediction.coerceIn(epsilon, 1.0 - epsilon)
        return -(target * ln(clampedPrediction) + (1 - target) * ln(1 - clampedPrediction))
    }

    fun binaryCrossEntropyGradient(prediction: Double, target: Double): Double {
        val epsilon = 1e-7
        val clampedPrediction = prediction.coerceIn(epsilon, 1.0 - epsilon)
        return clampedPrediction - target
    }

    fun binaryCrossEntropyLoss(predictions: DoubleArray, targets: DoubleArray): Double {
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

    fun binaryCrossEntropyGradient(predictions: DoubleArray, targets: DoubleArray): DoubleArray {
        require(predictions.size == targets.size) { "Predictions and targets must have the same size." }

        val epsilon = 1e-7
        return DoubleArray(predictions.size) { i ->
            val clampedPrediction = predictions[i].coerceIn(epsilon, 1.0 - epsilon)
            clampedPrediction - targets[i]
        }
    }

    /**
     * Categorical Cross-Entropy
     * Used for classification problems where outputs are probabilities.
     *
     * zip: The zip function combines the predicted and actual arrays element by element,
     * allowing you to iterate over both arrays simultaneously.
     * - predicted[i] is the predicted probability for class i.
     * - actual[i] is the true label, either 1 (for the correct class) or 0 (for other classes).
     * coerceAtLeast(1e-15) is a safety mechanism that ensures the predicted probability (pred) never becomes zero.
     * Since the logarithm of 0 is undefined (i.e., it would result in -Infinity),
     * this adjustment prevents potential runtime errors.
     * By coercing values to at least 1e-15, you avoid the case where ln(0) is computed,
     * and instead, it approximates the logarithm for very small values.
     * The ln function is applied to the predicted probability. The natural logarithm of the predicted value
     * is multiplied by the actual value (target), which is either 1 (for the correct class) or 0 (for all other classes).
     * If the actual label is 1, this contributes to the loss calculation.
     * If the actual label is 0, the corresponding term doesn't contribute, since multiplying by 0 results in 0.
     * sum(): After calculating the log loss for each class, .sum() adds up all the terms to compute the total loss.
     * Negative sign: The negative sign is outside the zip function to follow the formula of Categorical Cross-Entropy,
     * which is the negative sum of the products of the true label and the log of the predicted probability.
     *
     * Using a one-hot vector this formula could be simplified to: -ln(predicted[index].coerceAtLeast(1e-15)),
     * where the index corresponds to the index of the 1 in the one-hot vector.
     */
    fun categoricalCrossEntropy(predictions: DoubleArray, targets: DoubleArray): Double {
        return -targets.zip(predictions) { target, pred ->
            target * ln(pred.coerceAtLeast(1e-15)) // Avoid log(0)
        }.sum()
    }

    /**
     * Computes the gradient with respect to the predicted probabilities.
     */
    fun categoricalCrossEntropyDerivative(predictions: DoubleArray, targets: DoubleArray): DoubleArray {
        return predictions.indices.map { i ->
            val pred = predictions[i].coerceAtLeast(1e-15)  // Ensure no division by zero
            if (targets[i] == 1.0) {
                -1.0 / pred  // If y_i is 1, we compute -1 / p_i
            } else {
                0.0  // If y_i is 0, the derivative is 0
            }
        }.toDoubleArray()
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
    fun categoricalCrossEntropyBatch(predictions: Array<DoubleArray>, targets: Array<DoubleArray>): Double {
        // Ensure the sizes of predicted and actual match
        require(predictions.size == targets.size) { "Batch sizes of predicted and actual must match" }

        val totalLoss = predictions.indices.sumOf { batchIndex ->
            -targets[batchIndex].zip(predictions[batchIndex]) { target, pred ->
                target * ln(pred.coerceAtLeast(1e-15)) // Avoid log(0)
            }.sum()
        }
        return totalLoss / predictions.size // Return average loss across the batch
    }

    fun categoricalCrossEntropyDerivativeBatch(
        predicted: Array<DoubleArray>,
        actual: Array<DoubleArray>
    ): Array<DoubleArray> {
        // Ensure the sizes of predicted and actual match
        require(predicted.size == actual.size) { "Batch sizes of predicted and actual must match" }

        return Array(predicted.size) { batchIndex ->
            predicted[batchIndex].indices.map { i ->
                val pred = predicted[batchIndex][i].coerceAtLeast(1e-15) // Avoid division by zero
                if (actual[batchIndex][i] == 1.0) {
                    -1.0 / pred // If y_i is 1, compute -1 / p_i
                } else {
                    0.0 // If y_i is 0, derivative is 0
                }
            }.toDoubleArray()
        }
    }

    /**
     * The loss gradient is the key ingredient for the backward pass.
     * It is used to update the weights in the network so that the predictions get closer to the targets over time.
     */
    fun categoricalCrossEntropyGradient(predictions: DoubleArray, targets: DoubleArray): DoubleArray {
        val epsilon = 1e-15 // To avoid division by zero
        return predictions.indices.map { i ->
            -targets[i] / (predictions[i] + epsilon)
        }.toDoubleArray()
    }

    fun categoricalCrossEntropyGradientBatch(
        predicted: Array<DoubleArray>,
        actual: Array<DoubleArray>
    ): Array<DoubleArray> {
        val epsilon = 1e-15 // To avoid division by zero
        return Array(predicted.size) { i ->
            DoubleArray(predicted[i].size) { j ->
                -actual[i][j] / (predicted[i][j] + epsilon)
            }
        }
    }

    /**
     * Mean Squared Error (MSE)
     * Used for regression problems.
     */
    fun meanSquaredError(predictions: DoubleArray, targets: DoubleArray): Double {
        return targets.zip(predictions) { target, pred ->
            (target - pred).pow(2)
        }.average()
    }

    fun meanSquaredErrorDerivative(predictions: DoubleArray, targets: DoubleArray): DoubleArray {
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
    fun meanSquaredErrorBatch(predictions: Array<DoubleArray>, targets: Array<DoubleArray>): Double {
        // Ensure the sizes of predicted and actual match
        require(predictions.size == targets.size) { "Batch sizes of predicted and actual must match" }

        val totalLoss = predictions.indices.sumOf { batchIndex ->
            targets[batchIndex].zip(predictions[batchIndex]) { target, pred ->
                (target - pred).pow(2)
            }.average()
        }
        return totalLoss / predictions.size // Average loss across the batch
    }

    fun meanSquaredErrorDerivativeBatch(
        predictions: Array<DoubleArray>,
        targets: Array<DoubleArray>
    ): Array<DoubleArray> {
        // Ensure the sizes of predicted and actual match
        require(predictions.size == targets.size) { "Batch sizes of predicted and actual must match" }

        return Array(predictions.size) { batchIndex ->
            predictions[batchIndex].indices.map { i ->
                -2 * (targets[batchIndex][i] - predictions[batchIndex][i])
            }.toDoubleArray()
        }
    }
}