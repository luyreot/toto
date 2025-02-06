package deeplearning.optimization

enum class OptimizationFunctionType {
    SGD,
    Momentum,
    RMSprop,
    Adam;

    fun getOptimizationFunctionType(): OptimizationFunction {
        return when (this) {
            SGD -> deeplearning.optimization.SGD
            Momentum -> TODO()
            RMSprop -> TODO()
            Adam -> TODO()
        }
    }
}