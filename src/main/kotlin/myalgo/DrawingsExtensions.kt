package myalgo

import myalgo.model.Drawing
import myalgo.model.UniqueDrawing

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