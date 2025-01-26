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
        // Ensure frequency is positive and k is non-negative
        if (frequency <= 0.0) {
            return 0.0
        }
        //require(frequency > 0) { "Frequency must be positive." }
        require(k >= 0) { "k must be non-negative." }
        return (frequency.pow(k) * exp(-frequency)) / factorial(k)
    }
}