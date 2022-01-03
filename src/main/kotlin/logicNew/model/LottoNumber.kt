package logicNew.model

/**
 * Represents a single [number] and its [position] in a lotto drawing.
 * Ie. the '11' in '7,11,22,26,29,30'.
 *
 * Also, holds the [year] and the [issue] number of that drawing.
 */
data class LottoNumber(
    val number: Int,
    val position: Int,
    val year: Int,
    val issue: Int
)