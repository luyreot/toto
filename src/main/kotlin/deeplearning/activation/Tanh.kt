package deeplearning.activation

import kotlin.math.exp

object Tanh : ActivationFunction {

    override val type: ActivationFunctionType = ActivationFunctionType.Tahn

    override fun forward(input: DoubleArray): DoubleArray {
        return input.map { tanh(it) }.toDoubleArray()
    }

    override fun forward(inputs: Array<DoubleArray>): Array<DoubleArray> {
        return inputs.map { dim1 -> dim1.map { dim2 -> tanh(dim2) }.toDoubleArray() }.toTypedArray()
    }

    override fun backward(input: DoubleArray): DoubleArray {
        return input.map { tanhDerivative(it) }.toDoubleArray()
    }

    override fun backward(inputs: Array<DoubleArray>): Array<DoubleArray> {
        return inputs.map { dim1 -> dim1.map { dim2 -> tanhDerivative(dim2) }.toDoubleArray() }.toTypedArray()
    }

    private fun tanh(x: Double): Double = (2 / (1 + exp(-2 * x))) - 1

    private fun tanhDerivative(x: Double): Double = 1 - x * x
}