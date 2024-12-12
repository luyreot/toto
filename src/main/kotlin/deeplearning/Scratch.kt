package deeplearning

// region definitions

interface Neuron {
    var value: Float
}

interface NeuronActive : Neuron {
    fun calculateValue()
}

open class NeuronInput(
    override var value: Float
) : Neuron {
    constructor(value: Int) : this(value.toFloat())
    constructor(value: Double) : this(value.toFloat())
}

class NeuronOutput(
    override var value: Float = Float.NaN,
    val bias: Float,
    val links: Array<Neuron>,
    val weights: Array<Weight>
) : NeuronActive {
    constructor(bias: Int, links: Array<Neuron>, weights: Array<Weight>) :
            this(bias = bias.toFloat(), links = links, weights = weights)

    constructor(bias: Double, links: Array<Neuron>, weights: Array<Weight>) :
            this(bias = bias.toFloat(), links = links, weights = weights)

    // step function
    override fun calculateValue() {
        require(weights.size == input.size)

        // dot product
        value = weights.map { it.value }.zip(links.map { it.value }, Float::times).sum() + bias
    }
}

class Weight(
    val value: Float
) {
    constructor(value: Int) : this(value.toFloat())
    constructor(value: Double) : this(value.toFloat())
}

// endregion definition

val input: Array<Neuron> = arrayOf(
    NeuronInput(value = 1),
    NeuronInput(value = 2),
    NeuronInput(value = 3),
    NeuronInput(value = 2.5)
)

val weights: Array<Array<Weight>> = arrayOf(
    arrayOf(
        Weight(0.2),
        Weight(0.8),
        Weight(-0.5),
        Weight(1.0)
    ),
    arrayOf(
        Weight(0.5),
        Weight(-0.91),
        Weight(0.26),
        Weight(-0.5)
    ),
    arrayOf(
        Weight(-0.26),
        Weight(-0.27),
        Weight(0.17),
        Weight(0.87)
    )
)

val output: Array<NeuronOutput> = arrayOf(
    NeuronOutput(bias = 2, links = input, weights = weights[0]),
    NeuronOutput(bias = 3, links = input, weights = weights[1]),
    NeuronOutput(bias = 0.5, links = input, weights = weights[2])
)

fun calculateOutput() {
    output.forEach { outputNeuron -> outputNeuron.calculateValue() }

    output.forEach { println(it.value) }
}