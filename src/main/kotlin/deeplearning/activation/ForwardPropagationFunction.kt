package deeplearning.activation

sealed interface ForwardPropagationFunction {
    fun forward(input: DoubleArray): DoubleArray
    fun forward(inputs: Array<DoubleArray>): Array<DoubleArray>
}