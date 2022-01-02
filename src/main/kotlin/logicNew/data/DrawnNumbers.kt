package logicNew.data

import logicNew.model.drawing.DrawingType
import logicNew.model.drawing.DrawnNumber
import util.IO
import util.PATH_TXT_6x49

class DrawnNumbers(
    private val drawingType: DrawingType
) {

    val numbers: List<DrawnNumber>
        get() = numbersCache

    private val numbersCache = mutableListOf<DrawnNumber>()

    fun loadNumbers(
        vararg years: Int
    ) {
        if (numbersCache.isNotEmpty()) numbersCache.clear()

        val shouldLoadAllNumbers: Boolean = years.isEmpty()
        if (shouldLoadAllNumbers) {
            loadAllNumbers()
        } else {
            loadNumbersForYears(*years)
        }
    }

    fun validateNumbers() {
        val listSize: Int = numbersCache.size
        val setSize: Int = numbersCache.toSet().size
        if (listSize != setSize) throw IllegalArgumentException("There is an invalid drawing!")
    }

    fun checkDrawings() {
        if (numbersCache.isEmpty()) throw IllegalArgumentException("Drawings are empty!")
    }

    private fun loadAllNumbers() {
        IO.getFiles(PATH_TXT_6x49)?.let { files ->
            files.forEach { file ->
                addDrawnNumbers(
                    year = file.name.toInt(),
                    fileContents = IO.getTxtFileContents(file)
                )
            }
        } ?: throw IllegalArgumentException("Did not load any files!")
    }

    private fun loadNumbersForYears(
        vararg years: Int
    ) {
        years.forEach { year ->
            addDrawnNumbers(
                year = year,
                fileContents = IO.getTxtFileContents(PATH_TXT_6x49 + year)
            )
        }
    }

    private fun addDrawnNumbers(
        year: Int,
        fileContents: List<String>
    ) {
        // Index represents the drawing issue.
        // Index starts at 0 but in actuality the 0 line is the first issue for that year.
        // Line represents each separate drawing, ie. '1,12,34,35,44,49'.
        fileContents.forEachIndexed { issue, drawing ->
            val drawnNumbers: List<String> = drawing.split(",")

            when (drawingType) {
                DrawingType.D_6x49 -> if (drawnNumbers.size != 6)
                    throw IllegalArgumentException("Drawing is not ${drawingType.name}!")

                DrawingType.D_6x42 -> if (drawnNumbers.size != 6)
                    throw IllegalArgumentException("Drawing is not ${drawingType.name}!")

                DrawingType.D_5x35 -> if (drawnNumbers.size != 5)
                    throw IllegalArgumentException("Drawing is not ${drawingType.name}!")
            }

            drawnNumbers.forEachIndexed { position, number ->
                numbersCache.add(
                    DrawnNumber(
                        number = number.toInt(),
                        position = position,
                        year = year,
                        issue = issue + 1
                    )
                )
            }
        }
    }
}