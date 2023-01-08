package util

import crawler.WebCrawler535
import crawler.WebCrawler642
import crawler.WebCrawler649
import model.Frequency
import model.Numbers

object TotoUtils {

    fun fetchNewDrawings() {
        WebCrawler649.updateDrawings()
        WebCrawler642.updateDrawings()
        WebCrawler535.updateDrawings()
    }

    fun getDrawingScore(
        drawingIndex: Int,
        drawing: IntArray,
        totoNumberOccurrences: Map<Int, Int>,
        frequencies: Map<Int, List<Frequency>>,
        averageFrequencies: Map<Int, Float>,
        drawings: List<Numbers>
    ): Int {
        var score = 0

        drawing.forEach { number ->
            // Sum the number of occurrences for each drawing's number
            score += totoNumberOccurrences[number] ?: 0

            // Check if there are stored frequencies for each number
            if (frequencies[number] == null) return@forEach

            // Get the current frequency
            var frequency: Int = -1
            for (i in drawingIndex - 1 downTo 0) {
                if (drawings[i].numbers.any { it == number }.not()) continue

                frequency = drawingIndex - i
                break
            }

            // Continue if no frequency was found
            if (frequency == -1) return@forEach

            // Continue if the frequency is lower than the average frequency for that number
            if (frequency < (averageFrequencies[number] ?: 0f)) return@forEach

            // Check if the found frequency exists in the stored frequencies list for that number
            frequencies[number]?.find { it.frequency == frequency }?.let {
                score += it.count
            }
        }

        return score
    }
}