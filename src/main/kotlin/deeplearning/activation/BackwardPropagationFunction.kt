package deeplearning.activation

sealed interface BackwardPropagationFunction {
    fun backward(input: DoubleArray): DoubleArray
    fun backward(inputs: Array<DoubleArray>): Array<DoubleArray>
}