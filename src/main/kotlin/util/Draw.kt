package util

import model.TotoType

fun loadDrawings(totoType: TotoType): List<Draw> {
    val drawings = mutableListOf<Draw>()

    IO.getFiles(totoType.filePath)?.sorted()?.forEach { file ->
        IO.getTxtFileContents(file).forEachIndexed { index, line ->
            val numbers: IntArray = line.split(",").map { it.toInt() }.toIntArray()

            require(numbers.size == totoType.size) { "Drawing is not ${totoType.name}! Size is ${numbers.size}" }
            require(numbers.all { it in 1..totoType.totalNumbers }) { "Illegal number for ${totoType.name}! - $numbers" }

            drawings.add(
                Draw(
                    year = file.name.toInt(),
                    id = index + 1,
                    numbers = numbers
                )
            )
        }
    }

    return drawings
}

data class Draw(
    val year: Int,
    val id: Int,
    val numbers: IntArray
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Draw

        if (year != other.year) return false
        if (id != other.id) return false
        if (!numbers.contentEquals(other.numbers)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = year
        result = 31 * result + id
        result = 31 * result + numbers.contentHashCode()
        return result
    }
}