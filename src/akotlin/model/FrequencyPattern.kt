package akotlin.model

data class FrequencyPattern(
        val frequency: Int,
        override var timesOccurred: Int = 1,
        override var probability: Double = 0.0
) : Pattern()