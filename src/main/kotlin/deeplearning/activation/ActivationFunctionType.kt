package deeplearning.activation

enum class ActivationFunctionType {
    ReLU,
    LeakyReLU,
    Sigmoid,
    Softmax,
    Tahn;

    fun getActivationFunctionInstance(): ActivationFunction {
        return when (this) {
            ReLU -> deeplearning.activation.ReLU
            LeakyReLU -> deeplearning.activation.LeakyReLU()
            Sigmoid -> deeplearning.activation.Sigmoid
            Softmax -> deeplearning.activation.Softmax()
            Tahn -> deeplearning.activation.Tanh
        }
    }
}