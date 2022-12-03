package data

import extensions.clear
import extensions.greaterOrEqual
import model.TotoNumber
import model.TotoNumbers
import model.TotoType
import util.FileConstants.PATH_TXT_6x49
import util.IO

/**
 * Holds a list of all drawn numbers.
 */
class TotoDrawnNumbers(
    private val totoType: TotoType,
    private val fromYear: Int? = null
) {

    val numbers: List<TotoNumber>
        get() = numbersCache

    private val numbersCache = mutableListOf<TotoNumber>()

    val allDrawings: List<TotoNumbers>
        get() = allDrawingsCache

    private val allDrawingsCache = mutableListOf<TotoNumbers>()

    val drawingsSubset: List<TotoNumbers>
        get() = drawingsSubsetCache

    private val drawingsSubsetCache = mutableListOf<TotoNumbers>()

    fun loadTotoNumbers() {
        if (numbersCache.isNotEmpty()) numbersCache.clear()

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

        validateTotoNumbers()
    }

    fun extractDrawings() {
        numbersCache.sortedWith(compareBy<TotoNumber> { it.year }.thenBy { it.issue }.thenBy { it.position }).let { sortedTotoNumbers ->
            val drawing = IntArray(totoType.drawingSize)

            sortedTotoNumbers.forEach { totoNumber ->
                drawing[totoNumber.position] = totoNumber.number

                if (totoNumber.position != totoType.drawingSize - 1) return@forEach

                allDrawingsCache.add(TotoNumbers((drawing.clone())))

                if (totoNumber.year.greaterOrEqual(fromYear, false)) {
                    drawingsSubsetCache.add(TotoNumbers((drawing.clone())))
                }

                drawing.clear()
            }
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
        if (numbersCache.isEmpty())
            throw IllegalArgumentException("Drawings are empty!")

        if (numbersCache.any { it.issue == 0 })
            throw IllegalArgumentException("There is a zero issue drawing!")

        if (numbersCache.any { it.position > totoType.drawingSize - 1 })
            throw IllegalArgumentException("There is an incorrect position for ${totoType.name}!")

        val listSize: Int = numbersCache.size
        val setSize: Int = numbersCache.toSet().size
        if (listSize != setSize)
            throw IllegalArgumentException("There is an invalid drawing!")
    }
}