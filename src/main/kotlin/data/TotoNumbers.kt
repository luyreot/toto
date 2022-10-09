package data

import model.TotoNumber
import model.TotoType
import util.IO
import util.PATH_TXT_6x49

/**
 * Holds a list of all drawn numbers.
 */
class TotoNumbers(
    private val totoType: TotoType
) {

    val numbers: List<TotoNumber>
        get() = numbersCache

    private val numbersCache = mutableListOf<TotoNumber>()

    fun loadTotoNumbers(
        vararg years: Int
    ) {
        if (numbersCache.isNotEmpty()) numbersCache.clear()

        val shouldLoadAllNumbers: Boolean = years.isEmpty()
        if (shouldLoadAllNumbers) {
            loadAllTotoNumbers()
        } else {
            loadTotoNumbersForYears(*years)
        }

        validateTotoNumbers()
    }

    private fun loadAllTotoNumbers() {
        when (totoType) {
            TotoType.D_6X49 -> {
                IO.getFiles(PATH_TXT_6x49)?.let { files ->
                    files.forEach { file ->
                        addTotoNumbers(
                            year = file.name.toInt(),
                            fileContents = IO.getTxtFileContents(file)
                        )
                    }
                } ?: throw IllegalArgumentException("Did not load any files!")
            }

            else -> throw IllegalArgumentException("${totoType.name} is not supported!")
        }
    }

    private fun loadTotoNumbersForYears(
        vararg years: Int
    ) {
        years.forEach { year ->
            addTotoNumbers(
                year = year,
                fileContents = IO.getTxtFileContents(PATH_TXT_6x49 + year)
            )
        }
    }

    private fun addTotoNumbers(
        year: Int,
        fileContents: List<String>
    ) {
        // Index represents the toto issue.
        // Index starts at 0 but in actuality the 0 line is the first issue for that year.
        // Line represents each separate drawing, ie. '1,12,34,35,44,49'.
        fileContents.forEachIndexed { issue, drawing ->
            val drawnNumbers: List<String> = drawing.split(",")

            if (drawnNumbers.size != totoType.drawingSize)
                throw IllegalArgumentException("Drawing is not ${totoType.name}!")

            drawnNumbers.forEachIndexed { position, number ->
                numbersCache.add(
                    TotoNumber(
                        number = number.toInt(),
                        position = position,
                        year = year,
                        issue = issue + 1
                    )
                )
            }
        }
    }

    private fun validateTotoNumbers() {
        if (numbersCache.isEmpty()) throw IllegalArgumentException("Drawings are empty!")

        if (numbersCache.any { it.issue == 0 }) throw IllegalArgumentException("There is a zero issue drawing!")

        if (numbersCache.any { it.position > totoType.drawingSize - 1 })
            throw IllegalArgumentException("There is an incorrect position for ${totoType.name}!")

        val listSize: Int = numbersCache.size
        val setSize: Int = numbersCache.toSet().size
        if (listSize != setSize) throw IllegalArgumentException("There is an invalid drawing!")
    }
}