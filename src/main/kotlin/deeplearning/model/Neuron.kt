package deeplearning.model

class Neuron(
    var bias: Double
) {
    constructor(bias: Int) : this(bias.toDouble())
    constructor(bias: Float) : this(bias.toDouble())
}