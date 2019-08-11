package akotlin.model

import java.util.*

data class NumberPattern(
        val number: Int,
        override var lastFrequencyIndex: Int = 0,
        override val frequencyMap: TreeMap<Int, FrequencyPattern> = TreeMap(),
        override var timesOccurred: Int = 1,
        override var probability: Double = 0.0
) : SubPattern()