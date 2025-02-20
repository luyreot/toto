package systems.deeplearning.model

import systems.deeplearning.activation.ActivationFunction

/**
 * TODO - Change interface name
 *
 * Good practices.
 *
 * Input data
 * Scale data to a range, i.e. between -0.1 and 0.1 or -1 and 1.
 *
 * Weights
 * Initialize the weights in a down-scaled way, randomly, i.e. between -0.1 and 0.1 or -1 and 1.
 * This is done because we hope the values tend to be between those ranges.
 * If weights are big, i.e. 5, 10, etc., the data that goes through the network might get bigger and in the end 'explode'.
 *
 * Biases
 * Tend to init as 0. Sometimes the neurons will not fire, not produce an output. This means that the network is dead.
 * Then, init the biases with a non-zero value.
 * */
interface ActiveLayer : Layer {
    var learningRate: Double

    val biases: DoubleArray
    val weights: Array<DoubleArray>
    val verifyInputs: Boolean

    val activationFunction: ActivationFunction
    val activationFunctionDerivative: ActivationFunction

    val l1RegularizationLambda: Double
    val l2RegularizationLambda: Double

    fun applyL1Regularization()
    fun applyL2Regularization()
}