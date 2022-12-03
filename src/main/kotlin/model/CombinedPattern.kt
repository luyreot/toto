package model

data class CombinedPattern(
    val groupPattern: TotoNumbers,
    var count: Int
) {

    val lowHighs = mutableMapOf<TotoNumbers, Int>()

    val oddEvens = mutableMapOf<TotoNumbers, Int>()

    override fun equals(
        other: Any?
    ): Boolean = when {
        this === other -> true

        javaClass != other?.javaClass -> false

        else -> groupPattern.equals((other as? CombinedPattern)?.groupPattern)
    }

    override fun hashCode(): Int = groupPattern.hashCode()
}