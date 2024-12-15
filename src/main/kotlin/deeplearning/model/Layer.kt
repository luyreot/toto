package deeplearning.model

import deeplearning.activation.BackwardPropagationFunction
import deeplearning.activation.ForwardPropagationFunction

interface Layer {
    val activationFunction: ForwardPropagationFunction
    val activationFunctionDerivative: BackwardPropagationFunction

    fun forward(input: DoubleArray): DoubleArray
    fun forward(inputs: Array<DoubleArray>): Array<DoubleArray>

    fun backward(input: DoubleArray): DoubleArray
    fun backward(inputs: Array<DoubleArray>): Array<DoubleArray>
}