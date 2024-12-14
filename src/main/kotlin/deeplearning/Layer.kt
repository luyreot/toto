package deeplearning

interface Layer {
    val activationFunction: ActivationFunction

    fun forward(input: DoubleArray): DoubleArray
    fun forward(inputs: Array<DoubleArray>): Array<DoubleArray>
}