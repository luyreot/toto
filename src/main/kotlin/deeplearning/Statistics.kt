package deeplearning

import deeplearning.Math.factorial
import kotlin.math.exp
import kotlin.math.pow

object Statistics {

    /**
     * λ (lambda): The average rate of appearance for the number over a set of past draws (e.g., its frequency in the last 50 draws).
     * k: The expected occurrences in a single draw. For simplicity, we usually set k = 1, k=1 (the number appears once in the draw).
     */
    fun calculatePoissonProbability(frequency: Double, k: Int = 1): Double {
        return (frequency.pow(k) * exp(-frequency)) / factorial(k)
    }
}