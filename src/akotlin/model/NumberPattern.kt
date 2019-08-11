package akotlin.model

data class NumberPattern(val number: Int, override var lastFrequencyIndex: Int) : SubPattern()