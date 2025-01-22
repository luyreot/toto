package deeplearning.activation

enum class ActivationFunctionType {
    ReLU,
    Sigmoid,
    Softmax;

    fun getActivationFunctionInstance(): ActivationFunction {
        return when (this) {
            ReLU -> deeplearning.activation.ReLU
            Sigmoid -> deeplearning.activation.Sigmoid
            Softmax -> deeplearning.activation.Softmax()
        }
    }
}