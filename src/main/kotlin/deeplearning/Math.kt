package deeplearning

object Math {

    fun factorial(n: Int): Double = if (n == 0) 1.0 else n * factorial(n - 1)
}