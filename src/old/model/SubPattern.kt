package old.model

abstract class SubPattern : Pattern() {

    abstract var lastFrequencyIndex: Int

    var frequencies = mutableMapOf<Int, FrequencyPattern>()

    fun addFrequency(newFrequencyIndex: Int) {
        if (newFrequencyIndex < lastFrequencyIndex) return
        val key = newFrequencyIndex - lastFrequencyIndex
        if (frequencies.containsKey(key)) {
            frequencies[key]!!.incrementTimesOccurred()
        } else {
            frequencies[key] = FrequencyPattern(key)
            lastFrequencyIndex = newFrequencyIndex
        }
    }

}