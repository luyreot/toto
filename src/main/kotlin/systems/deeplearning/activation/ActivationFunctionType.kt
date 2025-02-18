package systems.deeplearning.activation

enum class ActivationFunctionType {
    ReLU,
    LeakyReLU,
    Sigmoid,
    Softmax,
    Tahn;

    fun getActivationFunctionInstance(): ActivationFunction {
        return when (this) {
            ReLU -> systems.deeplearning.activation.ReLU
            LeakyReLU -> systems.deeplearning.activation.LeakyReLU()
            Sigmoid -> systems.deeplearning.activation.Sigmoid
            Softmax -> systems.deeplearning.activation.Softmax()
            Tahn -> systems.deeplearning.activation.Tanh
        }
    }
}