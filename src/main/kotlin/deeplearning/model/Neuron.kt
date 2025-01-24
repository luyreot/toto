package deeplearning.model

class Neuron(
    var bias: Double
) {
    constructor() : this(0.0)
    constructor(bias: Int) : this(bias.toDouble())
    constructor(bias: Float) : this(bias.toDouble())
}