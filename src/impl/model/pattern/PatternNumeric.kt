package impl.model.pattern

import impl.data.Drawing

/**
 * Adds an implementation of tracking patterns' index from [Drawing.drawings],
 * what it is, how often it occurs.
 */
abstract class PatternNumeric : PatternBase() {

    /**
     *      "Last frequency index"
     * Initialize upon creation.
     *
     * Serves as a tmp var for storing the last drawing list index for
     * a pattern - number or sequence of numbers, taken from [Drawing.drawings].
     *
     * This object should be created once with the specific pattern - number or sequence of numbers.
     * Any subsequent occurrences in [Drawing.drawings] should update this value.
     */
    abstract var lfi: Int

    /**
     * Holds a map of a frequency, number of drawings between
     */
    val frequencies = mutableMapOf<Int, PatternFrequency>()

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

}