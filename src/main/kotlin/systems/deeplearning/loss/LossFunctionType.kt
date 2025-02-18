package systems.deeplearning.loss

enum class LossFunctionType {
    BinaryCrossEntropy,
    WeightedBinaryCrossEntropy,
    CategoricalCrossEntropy,
    MeanSquaredError;

    fun getLossFunctionType(): LossFunction {
        return when (this) {
            BinaryCrossEntropy -> systems.deeplearning.loss.BinaryCrossEntropy
            WeightedBinaryCrossEntropy -> systems.deeplearning.loss.WeightedBinaryCrossEntropy
            CategoricalCrossEntropy -> systems.deeplearning.loss.CategoricalCrossEntropy
            MeanSquaredError -> systems.deeplearning.loss.MeanSquaredError
        }
    }
}