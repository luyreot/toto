package systems.numbercorrelations

import systems.numbercorrelations.model.Drawing
import systems.numbercorrelations.model.UniqueDrawing

fun List<Drawing>.filterDrawingsAfterYear(year: Int): List<Drawing> = filter {
    it.year >= year
}

fun Drawing.toUniqueDrawing(): UniqueDrawing = UniqueDrawing(
    year = year,
    issue = issue,
    numbers = numbers,
    groupPattern = groupPattern,
    lowHighPattern = lowHighPattern,
    oddEvenPattern = oddEvenPattern
)