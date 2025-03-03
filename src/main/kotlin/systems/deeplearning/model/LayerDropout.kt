package systems.deeplearning.model

import kotlin.random.Random

/**
 * TODO - Optimization
 *
 * Add scaling to forward pass:
 * if (Random.nextDouble() > dropoutRate) 1.0 else 0.0
 * input[col] * mask[col] / (1.0 - dropoutRate)
 *
 * Add scaling to backward pass:
 * lossGradient[col] * mask[col] / (1.0 - dropoutRate)
 */
class LayerDropout(
    override val tag: String,
    override val layerType: LayerType,
    var dropoutRate: Double,
    var dropoutValue: Double = 0.0
) : Layer {

    lateinit var mask: BooleanArray
    lateinit var maskBatch: Array<BooleanArray>

    override fun forward(input: DoubleArray): DoubleArray {
        if (dropoutRate == 0.0) return input

        mask = BooleanArray(input.size) { Random.nextDouble() > dropoutRate }
        return input.mapIndexed { index, value -> if (mask[index]) value else dropoutValue }.toDoubleArray()
    }

    override fun forward(inputs: Array<DoubleArray>): Array<DoubleArray> {
        if (dropoutRate == 0.0) return inputs

        maskBatch = Array(inputs.size) { row ->
            BooleanArray(inputs[row].size) { Random.nextDouble() > dropoutRate }
        }

        return Array(inputs.size) { row ->
            DoubleArray(inputs[row].size) { col ->
                if (maskBatch[row][col]) inputs[row][col] else dropoutValue
            }
        }
    }

    override fun backward(lossGradient: DoubleArray): DoubleArray {
        if (dropoutRate == 0.0) return lossGradient

        return lossGradient.mapIndexed { index, grad -> if (mask[index]) grad else dropoutValue }.toDoubleArray()
    }

    override fun backward(lossGradients: Array<DoubleArray>): Array<DoubleArray> {
        if (dropoutRate == 0.0) return lossGradients

        return Array(lossGradients.size) { row ->
            DoubleArray(lossGradients[row].size) { col ->
                if (maskBatch[row][col]) lossGradients[row][col] else dropoutValue
            }
        }
    }
}