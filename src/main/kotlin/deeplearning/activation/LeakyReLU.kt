package deeplearning.activation

class LeakyReLU(
    private val alpha: Double = 0.01
) : ActivationFunction {

    override val type: ActivationFunctionType = ActivationFunctionType.LeakyReLU

    override fun forward(input: DoubleArray): DoubleArray {
        return input.map { relu(it) }.toDoubleArray()
    }

    override fun forward(inputs: Array<DoubleArray>): Array<DoubleArray> {
        return inputs.map { dim1 -> dim1.map { dim2 -> relu(dim2) }.toDoubleArray() }.toTypedArray()
    }

    override fun backward(input: DoubleArray): DoubleArray {
        return input.map { reluDerivative(it) }.toDoubleArray()
    }

    override fun backward(inputs: Array<DoubleArray>): Array<DoubleArray> {
        return inputs.map { dim1 -> dim1.map { dim2 -> reluDerivative(dim2) }.toDoubleArray() }.toTypedArray()
    }

    // Math

    private fun relu(x: Double): Double = if (x > 0) x else alpha * x

    private fun reluDerivative(x: Double): Double = if (x > 0) 1.0 else alpha
}