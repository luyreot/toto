package logicOld.model.pattern

import logicOld.data.Drawings
import logicOld.util.Helper

/**
 * Adds an implementation of tracking patterns' index from [Drawings.drawings],
 * what it is, how often it occurs.
 */
abstract class PatternNumeric : PatternBase() {

    /**
     *      "Last frequency index"
     * Initialize upon creation.
     *
     * Serves as a tmp var for storing the last drawing list index for
     * a pattern - number or sequence of numbers, taken from [Drawings.drawings].
     *
     * This object should be created once with the specific pattern - number or sequence of numbers.
     * Any subsequent occurrences in [Drawings.drawings] should update this value.
     */
    abstract var lfi: Int

    /**
     * Holds a map of a frequency, number of drawings between
     */
    val frequencies = mutableMapOf<Int, PatternFrequency>()

    init {
        if (lfi < 0) {
            throw IllegalArgumentException("Last Frequency index is invalid! Current is $lfi")
        }
    }

    /**
     * Overloads the [occurred] method from parent.
     *
     * [lfi]
     * [lfiNew] should always increase with each call to this method.
     */
    fun occurred(lfiNew: Int) {
        // escape
        if (lfi > lfiNew) return

        val frequency = lfiNew - lfi
        if (frequencies.containsKey(frequency)) {
            frequencies[frequency]?.occurred()
        } else {
            frequencies[frequency] = PatternFrequency(frequency)
        }

        lfi = lfiNew
        super.occurred()
    }

    override fun calcProbability(total: Int) {
        super.calcProbability(total)

        if (occurrence <= 1 && frequencies.isEmpty()) return

        val freqTotal = frequencies.values
                .map { it.occurrence }
                .reduce(Int::plus)

        if (freqTotal + 1 != occurrence) {
            throw IllegalArgumentException("Something went wrong when generating the object's frequencies!")
        }

        frequencies.forEach { (_, v) -> v.calcProbability(freqTotal) }
    }

    fun sortFrequencies() = Helper.sortPatternMap(frequencies)

}