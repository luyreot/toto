package deeplearning.model

class Neuron(
    val bias: Bias
) {
    constructor(bias: Int) : this(Bias(bias.toDouble()))
    constructor(bias: Float) : this(Bias(bias.toDouble()))
    constructor(bias: Double) : this(Bias(bias))
}