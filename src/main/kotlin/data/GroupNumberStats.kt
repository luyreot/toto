package data

import extensions.sortByValueDescending
import model.TotoType

/**
 * Holds a map for each number (ie. 1-49) with information
 * on how many times another number (ie. 1-49) has occurred within the same drawing,
 * forming a combination pair of those two.
 *
 * For example, the numbers 1 and 11 have occurred 23 times withing the same drawing.
 */
class GroupNumberStats(
    private val totoType: TotoType,
    private val drawings: Drawings,
    private val fromYear: Int? = null
) {

    val groups: Map<Int, Map<Int, Int>>
        get() = groupsCache
    private val groupsCache = mutableMapOf<Int, MutableMap<Int, Int>>()

    val averageGroupOccurrence: Map<Int, Int>
        get() = averageGroupOccurrenceCache
    private val averageGroupOccurrenceCache = mutableMapOf<Int, Int>()

    fun calculateStats() {
        val drawings = if (fromYear == null) drawings.drawings else drawings.drawingsSubset

        drawings.map { it.numbers }.forEach { numbers ->

            // Iterate over the drawing numbers
            for (one in numbers.indices) {
                for (two in numbers.indices) {
                    // Skip the same index
                    if (one == two) continue

                    // Create an empty map
                    if (groupsCache.containsKey(numbers[one]).not()) {
                        groupsCache[numbers[one]] = mutableMapOf()
                    }

                    // Add an occurrence of a number pair
                    if (groupsCache[numbers[one]]?.containsKey(numbers[two]) == false) {
                        groupsCache[numbers[one]]?.put(numbers[two], 1)
                    }

                    // Increment the occurrence of a number pair
                    groupsCache[numbers[one]]?.merge(numbers[two], 1, Int::plus)

                }
            }
        }

        groupsCache.forEach { (number, groups) ->
            averageGroupOccurrenceCache[number] = groups.values.sum() / groups.size
        }

        sortResults()
        validateResults()
    }

    private fun sortResults() {
        groupsCache.forEach {
            it.value.sortByValueDescending()
        }

        groupsCache.toSortedMap(compareBy { it }).let {
            groupsCache.clear()
            groupsCache.putAll(it)
        }
    }

    private fun validateResults() {
        if (groupsCache.size != totoType.totalNumbers)
            throw IllegalArgumentException("Not all numbers have been recorded!")

        groupsCache.forEach {
            if (it.value.size == totoType.totalNumbers - 1)
                return@forEach

            if (fromYear != null) {
                throw IllegalArgumentException("Not all numbers have been recorded for ${it.key}!")
            } else {
                // For a drawing subset it is possible that not all number pair could be generated
                println("Not all numbers have been recorded for ${it.key}!")
            }
        }
    }

    companion object {
        const val NUMBERS_PER_GROUP_PER_DRAWING: Int = 3
    }
}