package impl.crawler

import old.extensions.appendDrawingString
import old.extensions.appendDrawingsStringList
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import old.utils.*
import old.utils.Const.URL
import old.utils.Const.YEAR
import java.io.IOException

class WebCrawler(contents: List<String>) {

    // A fake user agent so the web server thinks the robot is a normal web browser.
    private val userAgent: String = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1"

    // The max html file size to be read, doesn't work if the number is too low aka the page is too large
    private val maxBodySize: Int = 10048000

    // Needed for retrieving the url of the drawing - http://www.toto.bg/results/6x49/2020-100
    private val drawingPrefix: String = "-"

    private val contentBuilder = StringBuilder()
    private var drawingCount: Int = contents.size


    init {
        contentBuilder.appendDrawingsStringList(contents)
        println("INFO: Current drawings count for $YEAR - $drawingCount")
    }

    fun crawl() {
        val baseUrl = URL + YEAR + drawingPrefix
        var shouldContinueCrawling = true
        var didAddNewDrawing = false
        do {
            val document = readPage(baseUrl.plus(++drawingCount))
            if (document == null) {
                println("ERROR! HTML Document is empty")
                shouldContinueCrawling = false
            } else {
                // Gets the individual numbers as a list of elements
                // Alternative css query: "span[class*=ball-white]"
                val numbers: Elements = document.select("div.tir_numbers > div.row > div.col-sm-6.text-right.nopadding > span.ball-white")
                if (numbers.isEmpty()) {
                    println("ERROR! Didn't select any elements")
                    shouldContinueCrawling = false
                } else {
                    val drawing = numbers.text().replace(" ", ",")
                    contentBuilder.appendDrawingString(drawing)
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
        lateinit var connection: Connection
        try {
            connection = Jsoup.connect(url).userAgent(userAgent)
            val statusCode = connection.response().statusCode()
            if (statusCode != 0 && statusCode != 200) {
                println("ERROR! Response status code for $url - $statusCode")
                return null
            }
            /* Crawler doesn't return a contentType
            if (!connection.response().contentType().contains("text/html")) {
                println("ERROR! Retrieved something other than HTML at $url")
                return null
            }
             */
            println("SUCCESS! Received web page at $url")
            return connection.maxBodySize(maxBodySize).get()
        } catch (ioe: IOException) {
            println("ERROR! Failed getting the page body at $url")
            println(ioe.message)
            return null
        }
    }

}