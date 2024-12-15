package deeplearning.util

import kotlin.math.exp
import kotlin.math.pow

object Math {

    fun factorial(n: Int): Double = if (n == 0) 1.0 else n * factorial(n - 1)

    /**
     * λ (lambda): The average rate of appearance for the number over a set of past draws (e.g., its frequency in the last 50 draws).
     * k: The expected occurrences in a single draw. For simplicity, we usually set k = 1, k=1 (the number appears once in the draw).
     */
    fun calculatePoissonProbability(frequency: Double, k: Int = 1): Double {
        return (frequency.pow(k) * exp(-frequency)) / factorial(k)
    }

    fun sigmoid(x: Double): Double = 1.0 / (1.0 + exp(-x))

    fun sigmoidDerivative(x: Double): Double {
        val sig = sigmoid(x)
        return sig * (1 - sig)
    }
}