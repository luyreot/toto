package deeplearning.activation

import deeplearning.Math

data object Sigmoid : ActivationFunction {

    override fun forward(input: DoubleArray): DoubleArray {
        return input.map { Math.sigmoid(it) }.toDoubleArray()
    }

    override fun forward(inputs: Array<DoubleArray>): Array<DoubleArray> {
        return inputs.map { dim1 -> dim1.map { dim2 -> Math.sigmoid(dim2) }.toDoubleArray() }.toTypedArray()
    }

    override fun backward(input: DoubleArray): DoubleArray {
        return input.map { Math.sigmoidDerivative(it) }.toDoubleArray()
    }

    override fun backward(inputs: Array<DoubleArray>): Array<DoubleArray> {
        return inputs.map { dim1 ->
            dim1.map { dim2 ->
                Math.sigmoidDerivative(dim2)
            }.toDoubleArray()
        }.toTypedArray()
    }
}