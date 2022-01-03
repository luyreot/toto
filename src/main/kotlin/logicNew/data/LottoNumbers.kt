package logicNew.data

import logicNew.model.LottoNumber
import logicNew.model.LottoType
import util.IO
import util.PATH_TXT_6x49

/**
 * Holds a list of all drawn numbers.
 */
class LottoNumbers(
    private val lottoType: LottoType
) {

    val numbers: List<LottoNumber>
        get() = numbersCache

    private val numbersCache = mutableListOf<LottoNumber>()

    fun loadLottoNumbers(
        vararg years: Int
    ) {
        if (numbersCache.isNotEmpty()) numbersCache.clear()

        val shouldLoadAllNumbers: Boolean = years.isEmpty()
        if (shouldLoadAllNumbers) {
            loadAllLottoNumbers()
        } else {
            loadLottoNumbersForYears(*years)
        }

        validateLottoNumbers()
    }

    private fun loadAllLottoNumbers() {
        when (lottoType) {
            LottoType.D_6X49 -> {
                IO.getFiles(PATH_TXT_6x49)?.let { files ->
                    files.forEach { file ->
                        addLottoNumbers(
                            year = file.name.toInt(),
                            fileContents = IO.getTxtFileContents(file)
                        )
                    }
                } ?: throw IllegalArgumentException("Did not load any files!")
            }

            else -> throw IllegalArgumentException("${lottoType.name} is not supported!")
        }
    }

    private fun loadLottoNumbersForYears(
        vararg years: Int
    ) {
        years.forEach { year ->
            addLottoNumbers(
                year = year,
                fileContents = IO.getTxtFileContents(PATH_TXT_6x49 + year)
            )
        }
    }

    private fun addLottoNumbers(
        year: Int,
        fileContents: List<String>
    ) {
        // Index represents the lotto issue.
        // Index starts at 0 but in actuality the 0 line is the first issue for that year.
        // Line represents each separate drawing, ie. '1,12,34,35,44,49'.
        fileContents.forEachIndexed { issue, drawing ->
            val drawnNumbers: List<String> = drawing.split(",")

            when (lottoType) {
                LottoType.D_6X49 -> if (drawnNumbers.size != 6)
                    throw IllegalArgumentException("Drawing is not ${lottoType.name}!")

                LottoType.D_6X42 -> if (drawnNumbers.size != 6)
                    throw IllegalArgumentException("Drawing is not ${lottoType.name}!")

                LottoType.D_5X35 -> if (drawnNumbers.size != 5)
                    throw IllegalArgumentException("Drawing is not ${lottoType.name}!")
            }

            drawnNumbers.forEachIndexed { position, number ->
                numbersCache.add(
                    LottoNumber(
                        number = number.toInt(),
                        position = position,
                        year = year,
                        issue = issue + 1
                    )
                )
            }
        }
    }

    private fun validateLottoNumbers() {
        if (numbersCache.isEmpty()) throw IllegalArgumentException("Drawings are empty!")

        if (numbersCache.any { it.issue == 0 }) throw IllegalArgumentException("There is a zero issue drawing!")

        when (lottoType) {
            LottoType.D_6X49 -> if (numbersCache.any { it.position > 5 })
                throw IllegalArgumentException("There is an incorrect position for ${lottoType.name}!")

            LottoType.D_6X42 -> if (numbersCache.any { it.position > 5 })
                throw IllegalArgumentException("There is an incorrect position for ${lottoType.name}!")

            LottoType.D_5X35 -> if (numbersCache.any { it.position > 4 })
                throw IllegalArgumentException("There is an incorrect position for ${lottoType.name}!")
        }

        val listSize: Int = numbersCache.size
        val setSize: Int = numbersCache.toSet().size
        if (listSize != setSize) throw IllegalArgumentException("There is an invalid drawing!")
    }
}