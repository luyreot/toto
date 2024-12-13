package deeplearning

interface Layer {
    fun forward(input: DoubleArray): DoubleArray
    fun forward(inputs: Array<DoubleArray>): Array<DoubleArray>
}