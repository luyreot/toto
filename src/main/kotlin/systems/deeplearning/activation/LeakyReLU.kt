package systems.deeplearning.activation

class LeakyReLU(
    private val alpha: Double = 0.01
) : ActivationFunction {

    override val type: ActivationFunctionType = ActivationFunctionType.LeakyReLU

    override fun forward(input: DoubleArray): DoubleArray {
        return DoubleArray(input.size) { i -> leakyRelu(input[i]) }
    }

    override fun forward(inputs: Array<DoubleArray>): Array<DoubleArray> {
        return Array(inputs.size) { i -> DoubleArray(inputs[i].size) { j -> leakyRelu(inputs[i][j]) } }
    }

    override fun backward(input: DoubleArray): DoubleArray {
        return DoubleArray(input.size) { i -> leakyReluDerivative(input[i]) }
    }

    override fun backward(inputs: Array<DoubleArray>): Array<DoubleArray> {
        return Array(inputs.size) { i -> DoubleArray(inputs[i].size) { j -> leakyReluDerivative(inputs[i][j]) } }
    }

    // Math

    private fun leakyRelu(x: Double): Double = if (x > 0) x else alpha * x

    private fun leakyReluDerivative(x: Double): Double = if (x > 0) 1.0 else alpha
}