package util

import kotlin.random.Random

object Math {

    fun fisherYatesShuffle(random: Random, list: MutableList<Int>) {
        for (i in list.size - 1 downTo 1) {
            val j = random.nextInt(i + 1)
            list[i] = list[j].also { list[j] = list[i] } // Swap
        }
    }
}