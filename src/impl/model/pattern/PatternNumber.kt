package impl.model.pattern

import impl.model.drawing.Drawing

/**
 * Holds a specific number from a [Drawing].
 */
data class PatternNumber(val number: Int, override var lfi: Int) : PatternNumeric() {

    init {
        if (number < 0) {
            throw IllegalArgumentException("Numbers is invalid!! Current is $number")
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PatternNumber

        if (number != other.number) return false

        return true
    }

    override fun hashCode(): Int {
        return number.hashCode()
    }

}