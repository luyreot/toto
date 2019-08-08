package akotlin.webcrawler

import akotlin.extensions.appendDrawing
import akotlin.extensions.appendDrawingsList
import akotlin.utils.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.io.IOException

class TotoWebCrawler(contents: List<String>) {

    companion object {
        // A fake USER_AGENT so the web server thinks the robot is a normal web browser.
        private val USER_AGENT: String = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1"
        // The max html file size to be read, doesn't work if the number is too low aka the page is too large
        private val MAX_BODY_SIZE: Int = 10048000
    }

    private val contentBuilder = StringBuilder()
    private var drawingCount: Int = contents.size

    init {
        contentBuilder.appendDrawingsList(contents)
        println("INFO: Current drawings count for $CURRENT_YEAR - $drawingCount")
    }

    fun crawl() {
        val baseUrl = PAGE_BASE_URL.plus(CURRENT_YEAR).plus(DRAWING_PREFIX)
        var shouldContinueCrawling = true
        var didAddNewDrawing = false
        do {
            val document = readPage(baseUrl.plus(++drawingCount))
            if (document == null) {
                println("ERROR! HTML Document is empty")
                shouldContinueCrawling = false
            } else {
                // gets the individual numbers as a list of elements
                //val numbers: Elements = document.select("span[class*=ball-white]")
                val numbers: Elements = document.select("div.tir_numbers > div.row > div.col-sm-6.text-right.nopadding > span.ball-white")
                if (numbers.isEmpty()) {
                    println("ERROR! Didn't select any elements")
                    shouldContinueCrawling = false
                } else {
                    val drawing = numbers.text().replace(" ", ",")
                    contentBuilder.appendDrawing(drawing)
                    didAddNewDrawing = true
                }
            }
        } while (shouldContinueCrawling)
        if (didAddNewDrawing) {
            println("INFO: Saving new drawings...")
            saveTxtFile(getCurrentYearTxtFilePath(), contentBuilder.toString())
            return
        }
        println("INFO: No new drawings were added")
    }

    private fun readPage(url: String): Document? {
        try {
            val connection = Jsoup.connect(url).userAgent(USER_AGENT)
            val document = connection.maxBodySize(MAX_BODY_SIZE).get()
            if (connection.response().statusCode() == 200) {
                println("Success! Received web page at $url")
                return document
            }
            if (!connection.response().contentType().contains("text/html")) {
                println("ERROR! Retrieved something other than HTML at $url")
                return null
            }
        } catch (ioe: IOException) {
            println("ERROR! HTTP request was not successful at $url")
            return null
        }
        return null
    }

}