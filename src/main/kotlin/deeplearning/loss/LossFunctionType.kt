package deeplearning.loss

enum class LossFunctionType {
    BinaryCrossEntropy,
    WeightedBinaryCrossEntropy,
    CategoricalCrossEntropy,
    MeanSquaredError;

    fun getLossFunctionType(): LossFunction {
        return when (this) {
            BinaryCrossEntropy -> deeplearning.loss.BinaryCrossEntropy
            WeightedBinaryCrossEntropy -> deeplearning.loss.WeightedBinaryCrossEntropy
            CategoricalCrossEntropy -> deeplearning.loss.CategoricalCrossEntropy
            MeanSquaredError -> deeplearning.loss.MeanSquaredError
        }
    }
}