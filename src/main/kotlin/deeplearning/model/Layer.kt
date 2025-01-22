package deeplearning.model

import deeplearning.activation.BackwardPropagationFunction
import deeplearning.activation.ForwardPropagationFunction

/**
 * Good practices.
 *
 * Input data
 * Scale data to a range, i.e. between -0.1 and 0.1 or -1 and 1.
 *
 * Weights
 * Initialize the weights in a down-scaled way, randomly, i.e. between -0.1 and 0.1 or -1 and 1.
 * This is done because we hope the values tend to be between those ranges.
 * If weights are big, i.e. 5, 10, etc., the data that goes through the network might get bigger and in the end 'explode'.
 *
 * Biases
 * Tend to init as 0. Sometimes the neurons will not fire, not produce an output. This means that the network is dead.
 * Then, init the biases with a non-zero value.
 *
 * Activation
 * Generally the output layer will have a different activation function than the input and hidden layers.
 *
 * Backpropagation
 * For each layer, you compute the gradients of the loss with respect to the weights, biases, and activations (input to the layer).
 * These gradients are used to update the weights during the optimization step (e.g., gradient descent).
 */
interface Layer {
    val activationFunction: ForwardPropagationFunction
    val activationFunctionDerivative: BackwardPropagationFunction

    fun forward(input: DoubleArray): DoubleArray
    fun forward(inputs: Array<DoubleArray>): Array<DoubleArray>

    /**
     * In the backward pass of a layer, the delta refers to the error term for the current layer.
     * It’s the gradient of the loss with respect to the output (activations) of the layer,
     * and this is passed from the output layer to the input layer, through all the layers in between.
     * It measures how much each neuron's activation should change in response to the loss.
     * This is essential because the goal of training a neural network is to minimize the loss by adjusting
     * the weights, biases, and activations in a direction that reduces the error.
     *
     * At the output layer:
     * The delta is directly related to the derivative of the loss with respect to the output of the layer.
     * At the hidden layers:
     * The delta at each hidden layer is computed by taking the weighted sum of the deltas from the next layer, multiplied by the derivative of the activation function.
     * For each layer, the delta is used to compute:
     * - The gradient of the weights (how much the weights should be adjusted).
     * - The gradient of the biases (how much the biases should be adjusted).
     * - The gradient of the activations (how much the input to this layer should be adjusted to propagate the error to the previous layer).
     *
     * [lossGradient] is effectively the delta of the output layer.
     */
    fun backward(lossGradient: DoubleArray): DoubleArray
    fun backward(lossGradients: Array<DoubleArray>): Array<DoubleArray>
}