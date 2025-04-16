package systems.deeplearning.activation

import kotlin.math.exp

object Tanh : ActivationFunction {

    override val type: ActivationFunctionType = ActivationFunctionType.Tanh

    override fun forward(input: DoubleArray): DoubleArray {
        return DoubleArray(input.size) { i -> tanh(input[i]) }
    }

    override fun forward(inputs: Array<DoubleArray>): Array<DoubleArray> {
        return Array(inputs.size) { i -> DoubleArray(inputs[i].size) { j -> tanh(inputs[i][j]) } }
    }

    override fun backward(input: DoubleArray): DoubleArray {
        return DoubleArray(input.size) { i -> tanhDerivative(input[i]) }
    }

    override fun backward(inputs: Array<DoubleArray>): Array<DoubleArray> {
        return Array(inputs.size) { i -> DoubleArray(inputs[i].size) { j -> tanhDerivative(inputs[i][j]) } }
    }

    private fun tanh(x: Double): Double = (2 / (1 + exp(-2 * x))) - 1

    private fun tanhDerivative(x: Double): Double = 1 - x * x
}