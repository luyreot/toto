package logicNew.model

/**
 * Represents a single [number] and its [position] in a toto drawing.
 * Ie. the '11' in '7,11,22,26,29,30'.
 *
 * Also, holds the [year] and the [issue] number of that drawing.
 */
data class TotoNumber(
    val number: Int,
    val position: Int,
    val year: Int,
    val issue: Int
)