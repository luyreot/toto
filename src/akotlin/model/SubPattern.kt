package akotlin.model

import java.util.*

abstract class SubPattern : Pattern() {

    abstract var lastFrequencyIndex: Int

    val frequencyMap: TreeMap<Int, FrequencyPattern> = TreeMap()

    fun addFrequency(newFrequencyIndex: Int) {
        if (newFrequencyIndex < lastFrequencyIndex) return
        val key = newFrequencyIndex - lastFrequencyIndex
        if (frequencyMap.containsKey(key)) {
            frequencyMap[key]?.incrementTimesOccurred()
        } else {
            frequencyMap[key] = FrequencyPattern(key)
            lastFrequencyIndex = newFrequencyIndex
        }
    }

}