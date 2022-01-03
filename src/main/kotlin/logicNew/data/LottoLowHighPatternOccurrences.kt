package logicNew.data

import kotlinx.coroutines.coroutineScope
import logicNew.extensions.clear
import logicNew.model.LottoNumber
import logicNew.model.LottoPattern
import logicNew.model.LottoType

/**
 * Holds information about occurrences of low/high patterns for each lotto drawing.
 *
 * An low/high pattern for a lotto drawing will look like this:
 * 0 - <= 25, 1 - > 25 (when the lotto type is 6x49)
 * 5,14,22,25,34,49 -> 0,0,0,0,1,1
 */
class LottoLowHighPatternOccurrences(
    private val lottoType: LottoType,
    private val lottoNumbers: LottoNumbers
) {

    val patterns: Map<LottoPattern, Int>
        get() = patternsCache

    private val patternsCache = mutableMapOf<LottoPattern, Int>()

    suspend fun calculateLottoLowHighPatternOccurrences() = coroutineScope {
        lottoNumbers.numbers
            .sortedWith(compareBy<LottoNumber> { it.year }
                .thenBy { it.issue }.thenBy { it.position })
            .let { sortedLottoNumbers ->

                val tmpLottoNumbers = IntArray(
                    when (lottoType) {
                        LottoType.D_6X49 -> 6
                        LottoType.D_6X42 -> 6
                        LottoType.D_5X35 -> 5
                    }
                )
                sortedLottoNumbers.forEachIndexed { index, lottoNumber ->
                    val position = lottoNumber.position

                    tmpLottoNumbers[position] = lottoNumber.number

                    // Skip the very first item from the list
                    // Save on the last item of the list or when the lotto number position becomes 0
                    if ((index != 0 && position == 0) || index == sortedLottoNumbers.size - 1) {
                        // Already got the lotto numbers of a single drawing
                        val lowHighPattern = LottoPattern(
                            pattern = convertLottoNumbersToLowHighPattern(tmpLottoNumbers.copyOf())
                        )

                        // Save the pattern in the map
                        patternsCache.merge(lowHighPattern, 1, Int::plus)

                        // Reset the tmp array for the next lotto drawing
                        tmpLottoNumbers.clear()
                    }
                }
            }

        validateLottoLowHighPatternOccurrences()
    }

    private fun convertLottoNumbersToLowHighPattern(
        numbers: IntArray
    ): IntArray {
        val midPoint: Int = getLowHighMidPoint()

        for (i in numbers.indices) {
            numbers[i] = if (numbers[i] <= midPoint) 0 else 1
        }

        return numbers
    }

    private fun getLowHighMidPoint(): Int = when (lottoType) {
        // Can also be 24, but with 25 we achieve the correct probability % from the LottoMetrix website
        LottoType.D_6X49 -> 25
        LottoType.D_6X42 -> 21
        LottoType.D_5X35 -> 18
    }

    private fun validateLottoLowHighPatternOccurrences() {
        // Size of the lotto numbers should be the same as the total sum of the patterns
        val lowHighPatternSize = patternsCache.values.sum()
        val lottoNumberSize = lottoNumbers.numbers.count { it.position == 0 }
        if (lowHighPatternSize != lottoNumberSize)
            throw IllegalArgumentException("Pattern size is incorrect!")

        // No odd/even pattern should have an occurrence of 0
        if (patternsCache.values.any { it == 0 })
            throw IllegalArgumentException("Invalid pattern occurrence value!")

        // All odd/even patterns should contain 0 or 1 values
        val anyInvalidPatterns = patternsCache.keys.any { oddEvenPattern ->
            oddEvenPattern.pattern.any { arrayItem -> arrayItem != 0 && arrayItem != 1 }
        }
        if (anyInvalidPatterns)
            throw IllegalArgumentException("Invalid odd/even pattern!")
    }
}