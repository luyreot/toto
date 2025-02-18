package systems.deeplearning.loss

/**
 * Loss functions measure how well the network's predictions match the actual target values.
 */
interface LossFunction {
    val type: LossFunctionType
}