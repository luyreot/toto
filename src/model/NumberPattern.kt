package model

data class NumberPattern(val number: Int, override var lastFrequencyIndex: Int) : SubPattern() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NumberPattern

        return number == other.number
    }

    override fun hashCode(): Int {
        return number.hashCode()
    }

}