package systems.deeplearning.model

class Features(
    val number: Int,
    val frequency: Double,
    val gapSinceLast: Int,
    val poissonProbability: Double,
    val inDraw: Int
)

fun Features.containsNan(): Boolean {
    if (frequency.isNaN()) return true
    if (poissonProbability.isNaN()) return true

    return false
}

fun Features.toDoubleArray(): DoubleArray = doubleArrayOf(
    frequency,
    gapSinceLast.toDouble(),
    poissonProbability,
    inDraw.toDouble()
)