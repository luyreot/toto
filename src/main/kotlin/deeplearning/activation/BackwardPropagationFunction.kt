package deeplearning.activation

/**
 * The implementation must provide the mathematical derivative of the implemented function in [ForwardPropagationFunction].
 */
sealed interface BackwardPropagationFunction {
    fun backward(input: DoubleArray): DoubleArray
    fun backward(inputs: Array<DoubleArray>): Array<DoubleArray>
}