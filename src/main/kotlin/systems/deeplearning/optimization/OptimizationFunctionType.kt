package systems.deeplearning.optimization

enum class OptimizationFunctionType {
    SGD,
    Momentum,
    RMSprop,
    Adam;

    fun getOptimizationFunctionType(): OptimizationFunction {
        return when (this) {
            SGD -> systems.deeplearning.optimization.SGD
            Momentum -> TODO()
            RMSprop -> TODO()
            Adam -> Adam()
        }
    }
}