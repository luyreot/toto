package data

import extensions.clear
import extensions.greaterOrEqual
import model.Number
import model.Numbers
import model.TotoType
import util.FileConstants.PATH_TXT_5x35
import util.FileConstants.PATH_TXT_6x42
import util.FileConstants.PATH_TXT_6x49
import util.IO
import util.PredictionTester

/**
 * Holds a list of all drawn numbers.
 */
class Drawings(
    private val totoType: TotoType,
    private val fromYear: Int? = null
) {

    val numbers: List<Number>
        get() = numbersCache
    private val numbersCache = mutableListOf<Number>()

    val drawings: List<Numbers>
        get() = drawingsCache
    private val drawingsCache = mutableListOf<Numbers>()

    val drawingsSubset: List<Numbers>
        get() = drawingsSubsetCache
    private val drawingsSubsetCache = mutableListOf<Numbers>()

    fun loadNumbers() {
        if (numbersCache.isNotEmpty()) numbersCache.clear()

        when (totoType) {
            TotoType.T_6X49 -> PATH_TXT_6x49
            TotoType.T_6X42 -> PATH_TXT_6x42
            TotoType.T_5X35 -> PATH_TXT_5x35
        }.let { path ->
            IO.getFiles(path)?.let { files ->
                files.sorted().forEach { file ->
                    addNumbers(
                        year = file.name.toInt(),
                        fileContents = IO.getTxtFileContents(file)
                    )
                }
            } ?: throw IllegalArgumentException("Did not load any files for $path!")
        }

        validateNumbers()
    }

    fun setUpDrawingsForTesting() {
        PredictionTester.apply {
            if (isTestingPredictions.not()) return

            nextDrawing = null

            val issue = startIssue + issueCounter

            var anyDrawingsLeftToTest = numbersCache.any { number ->
                number.year == startYear && number.issue >= issue
            }

            if (anyDrawingsLeftToTest) {
                numbersCache.filter { number ->
                    number.year == startYear && number.issue == issue
                }.let { numbers ->
                    if (numbers.isEmpty())
                        throw IllegalArgumentException("Cannot set next drawing for year $startYear and issue $issue!")

                    nextDrawing = IntArray(totoType.size)
                    numbers.sortedBy { it.position }.forEach { number ->
                        nextDrawing!![number.position] = number.number
                    }
                }

                numbersCache.removeIf { number ->
                    (number.year == startYear && number.issue >= issue) || number.year > startYear
                }

                return
            }

            // Transition into next year
            issueCounter = 0
            startIssue = 1
            startYear++

            anyDrawingsLeftToTest = numbersCache.any { number ->
                number.year == startYear && number.issue >= startIssue
            }

            if (anyDrawingsLeftToTest) {
                setUpDrawingsForTesting()
            } else {
                isTestingPredictions = false
            }
        }
    }

    fun extractDrawings() {
        numbersCache.sortedWith(compareBy<Number> { it.year }.thenBy { it.issue }.thenBy { it.position }).let { sortedNumbers ->
            val drawing = IntArray(totoType.size)

            sortedNumbers.forEach { number ->
                drawing[number.position] = number.number

                if (number.position != totoType.size - 1) return@forEach

                drawingsCache.add(Numbers(drawing.clone()))

                if (number.year.greaterOrEqual(fromYear, false)) {
                    drawingsSubsetCache.add(Numbers(drawing.clone()))
                }

                drawing.clear()
            }
        }
    }

    fun checkForDuplicateDrawings() {
        if (PredictionTester.isTestingPredictions) return

        val allDrawingsSize = drawingsCache.size
        val allDrawingsSetSize = drawingsCache.toSet().size
        if (allDrawingsSize != allDrawingsSetSize) {
            println("All > There is at least one duplicate drawing!")
            printDuplicatedDrawings(drawingsCache)
        }

        if (drawingsSubsetCache.isEmpty()) return
        val drawingsSubsetSize = drawingsSubsetCache.size
        val drawingsSubsetSetSize = drawingsSubsetCache.toSet().size
        if (drawingsSubsetSize != drawingsSubsetSetSize) {
            println()
            println("Subset > There is at least one duplicate drawing!")
            printDuplicatedDrawings(drawingsSubsetCache)
        }
    }

    private fun addNumbers(
        year: Int,
        fileContents: List<String>
    ) {
        // Index represents the toto issue.
        // Index starts at 0 but in actuality the 0 line is the first issue for that year.
        // Line represents each separate drawing, ie. '1,12,34,35,44,49'.
        fileContents.forEachIndexed { issue, drawing ->
            val drawnNumbers: List<String> = drawing.split(",")

            if (drawnNumbers.size != totoType.size)
                throw IllegalArgumentException("Drawing is not ${totoType.name}!")

            for (i in 0 until drawnNumbers.size - 1) {
                if (drawnNumbers[i].toInt() >= drawnNumbers[i + 1].toInt())
                    throw IllegalArgumentException("Illegal numbers - ${drawnNumbers[i]}, ${drawnNumbers[i + 1]}")
            }

            drawnNumbers.forEachIndexed { position, number ->
                when (totoType) {
                    TotoType.T_6X49 -> number.toInt().let {
                        if (it < 1 || it > 49) throw IllegalArgumentException("Illegal number for 6x49 - $number")
                    }

                    TotoType.T_6X42 -> number.toInt().let {
                        if (it < 1 || it > 42) throw IllegalArgumentException("Illegal number for 6x42 - $number")
                    }

                    TotoType.T_5X35 -> number.toInt().let {
                        if (it < 1 || it > 35) throw IllegalArgumentException("Illegal number for 5x35 - $number")
                    }
                }

                numbersCache.add(
                    Number(
                        number = number.toInt(),
                        position = position,
                        year = year,
                        issue = issue + 1
                    )
                )
            }
        }
    }

    private fun validateNumbers() {
        if (numbersCache.isEmpty())
            throw IllegalArgumentException("Drawings are empty!")

        if (numbersCache.any { it.issue == 0 })
            throw IllegalArgumentException("There is a zero issue drawing!")

        if (numbersCache.any { it.position > totoType.size - 1 })
            throw IllegalArgumentException("There is an incorrect position for ${totoType.name}!")

        val listSize: Int = numbersCache.size
        val setSize: Int = numbersCache.toSet().size
        if (listSize != setSize)
            throw IllegalArgumentException("There is an invalid drawing!")
    }

    private fun printDuplicatedDrawings(drawings: List<Numbers>) {
        val duplicateNumbers: Set<Numbers> = drawings.groupingBy { it }.eachCount().filter { it.value > 1 }.keys
        println("${duplicateNumbers.size} duplicated drawings.")

        numbersCache.sortedWith(compareBy<Number> { it.year }.thenBy { it.issue }.thenBy { it.position }).let { sortedNumbers ->
            val drawingArray = IntArray(totoType.size)

            sortedNumbers.forEach { number ->
                drawingArray[number.position] = number.number

                if (number.position != totoType.size - 1) return@forEach

                val drawing = Numbers(drawingArray)

                if (duplicateNumbers.contains(drawing).not()) return@forEach

                println("$drawing, year=${number.year}, issue=${number.issue}")
            }
        }
    }
}