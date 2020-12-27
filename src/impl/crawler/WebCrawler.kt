package impl.crawler

import impl.extension.appendLine
import impl.util.Const.PATH_TXT
import impl.util.Const.URL
import impl.util.Const.YEAR
import impl.util.IO
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.io.IOException

class WebCrawler {

    // region Web

    // A fake user agent so the web server thinks the robot is a normal web browser.
    private val userAgent: String = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1"
    // The max html file size to be read, doesn't work if the number is too low aka the page is too large
    private val maxBodySize: Int = 10048000
    // http://www.toto.bg/results/6x49/2020-100
    private val drawingPrefix: String = "-"
    private val currentYearPath = PATH_TXT + YEAR
    // Alternative css query: "span[class*=ball-white]"
    private val documentQuery = "div.tir_numbers > div.row > div.col-sm-6.text-right.nopadding > span.ball-white"

    // endregion Web

    // region File contents

    private val contentBuilder = StringBuilder()
    // Track the current drawing
    private val drawingCount: Int

    // endregion File contents

    init {
        val currentYearDrawings = IO.getTxtFileContents(currentYearPath)
        currentYearDrawings.forEach { contentBuilder.appendLine(it) }
        drawingCount = currentYearDrawings.size
        println("INFO: Current drawings count for $YEAR - $drawingCount")
    }

    fun crawl() {
        val pageUrl = URL + YEAR + drawingPrefix
        var saveToFile = false
        var drawingIndex = drawingCount

        do {
            drawingIndex += 1
            val drawingUrl = pageUrl + drawingIndex
            val document = readPage(drawingUrl)

            if (document == null) {
                println("ERROR! HTML Document is empty")
                break
            }

            // Gets the individual numbers as a list of elements
            val numbers: Elements = document.select(documentQuery)
            if (numbers.isEmpty()) {
                println("ERROR! Didn't select any elements")
                break
            }

            val drawing = numbers.text().replace(" ", ",")
            contentBuilder.appendLine(drawing)
            if (saveToFile.not()) {
                saveToFile = true
            }

        } while (true)


        if (saveToFile) {
            println("INFO: Saving new drawings...")
            IO.saveTxtFile(currentYearPath, contentBuilder.toString())
            return
        }

        println("INFO: No new drawings were added")
    }

    private fun readPage(url: String): Document? {
        try {
            val connection = Jsoup.connect(url).userAgent(userAgent)
            val statusCode = connection.response().statusCode()

            /* Crawler doesn't return a contentType
            if (!connection.response().contentType().contains("text/html")) {
                println("ERROR! Retrieved something other than HTML at $url")
                return null
            }*/

            return when (statusCode) {
                0, 200 -> {
                    println("SUCCESS! Received web page at $url")
                    connection.maxBodySize(maxBodySize).get()
                }

                else -> {
                    println("ERROR! Response status code for $url - $statusCode")
                    null
                }
            }
        } catch (ioe: IOException) {
            println("ERROR! Failed getting the page body at $url")
            println(ioe.message)
        }

        return null
    }

    companion object {
        fun updateDrawings() {
            WebCrawler().crawl()
        }
    }

}