package deeplearning.activation

/**
 * The defined functions below are used for doing the forward pass and the backward pass (_derivative).
 *
 * The gradient descent algorithm relies on the slope (or rate of change) of the activation function
 * to compute how the weights and biases should be updated.
 * During backpropagation, we calculate how the error propagates backward through the network.
 * The error gradient at each neuron depends on the rate of change of its activation function
 * (i.e., how sensitive the output is to changes in input).
 */
interface ActivationFunction {
    val type: ActivationFunctionType

    fun forward(input: DoubleArray): DoubleArray
    fun forward(inputs: Array<DoubleArray>): Array<DoubleArray>

    fun backward(input: DoubleArray): DoubleArray
    fun backward(inputs: Array<DoubleArray>): Array<DoubleArray>
}