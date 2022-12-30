package data

import model.Frequency

interface PatternStats<T> {
    val patterns: Map<T, Int>
    val frequencies: Map<T, List<Frequency>>
}