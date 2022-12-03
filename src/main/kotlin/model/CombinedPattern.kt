package model

data class CombinedPattern(
    val groupPattern: Numbers,
    var count: Int
) {

    val lowHighs = mutableMapOf<Numbers, Int>()

    val oddEvens = mutableMapOf<Numbers, Int>()

    override fun equals(
        other: Any?
    ): Boolean = when {
        this === other -> true

        javaClass != other?.javaClass -> false

        else -> groupPattern == (other as? CombinedPattern)?.groupPattern
    }

    override fun hashCode(): Int = groupPattern.hashCode()
}