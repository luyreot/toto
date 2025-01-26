package deeplearning.model

class Features(
    val number: Int,
    val frequency: Double,
    val gapSinceLast: Int,
    val poissonProbability: Double,
    val inDraw: Int
)