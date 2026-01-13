package crawler

import model.TotoType
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import util.Constants.PAGE_URL_5x35
import util.Constants.PAGE_URL_6x42
import util.Constants.PAGE_URL_6x49
import util.Constants.PAGE_YEAR
import util.Constants.PATH_TXT_5x35
import util.Constants.PATH_TXT_6x42
import util.Constants.PATH_TXT_6x49
import util.IO
import util.Logger
import java.io.IOException

class WebCrawler {

    private companion object {
        // A fake user agent so the web server thinks the robot is a normal web browser.
        const val USER_AGENT: String =
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1"

        // The max html file size to be read, doesn't work if the number is too low aka the page is too large
        const val MAX_BODY_SIZE: Int = 10048000

        // Alternative css query: "span[class*=ball-white]"
        const val DOCUMENT_QUERY: String =
            "div.tir_numbers > div.row > div.col-sm-6.text-right.nopadding > span.ball-white"
    }


    fun crawl(totoType: TotoType) {
        // Load past drawings
        val filePath: String = when (totoType) {
            TotoType.T_5X35 -> PATH_TXT_5x35 + PAGE_YEAR
            TotoType.T_6X42 -> PATH_TXT_6x42 + PAGE_YEAR
            TotoType.T_6X49 -> PATH_TXT_6x49 + PAGE_YEAR
        }
        val pastDrawings: List<String> = IO.getTxtFileContents(filePath)
        val contentBuilder = StringBuilder()
        pastDrawings.forEach { contentBuilder.appendLine(it) }
        var currentDrawing: Int = if (totoType == TotoType.T_5X35) {
            pastDrawings.size / 2
        } else {
            pastDrawings.size
        }
        /*
        if (currentDrawing == 0) {
            currentDrawing = 1
        } else {
            currentDrawing += 1
        }
        */
        Logger.p("INFO: Current drawings count for $totoType - $PAGE_YEAR - $currentDrawing")

        // Fetch new drawings from web
        val pageUrl: String = when (totoType) {
            TotoType.T_5X35 -> "$PAGE_URL_5x35$PAGE_YEAR-"
            TotoType.T_6X42 -> "$PAGE_URL_6x42$PAGE_YEAR-"
            TotoType.T_6X49 -> "$PAGE_URL_6x49$PAGE_YEAR-"
        }
        var saveToFile = false
        do {
            val document: Document? = readPage(pageUrl + currentDrawing)

            if (document == null) {
                Logger.p("ERROR! HTML Document is empty")
                break
            }

            // Gets the individual numbers as a list of elements
            val numbers: Elements = document.select(DOCUMENT_QUERY)
            if (numbers.isEmpty()) {
                Logger.p("ERROR! Didn't select any elements")
                break
            }

            if (totoType == TotoType.T_5X35) {
                val drawingsCount = numbers.size / 5
                val drawings = mutableListOf<Elements>()
                for (i in 0 until drawingsCount) {
                    drawings.add(Elements(numbers.slice(i * 5..i * 5 + 4)))
                }
                drawings.forEach { drawing ->
                    contentBuilder.appendLine(drawing.text().replace(" ", ","))
                }
            } else {
                val drawing = numbers.text().replace(" ", ",")
                contentBuilder.appendLine(drawing)
            }

            if (saveToFile.not()) {
                saveToFile = true
            }

            currentDrawing += 1
        } while (true)

        if (saveToFile) {
            Logger.p("INFO: Saving new drawings...")
            IO.saveTxtFile(filePath, contentBuilder.toString())
            return
        }

        Logger.p("INFO: No new drawings were added")
    }

    private fun readPage(url: String): Document? {
        try {
            val connection = Jsoup.connect(url).userAgent(USER_AGENT).apply {
                execute()
            }

            /* Crawler doesn't return a contentType
            if (!connection.response().contentType().contains("text/html")) {
                Logme.p("ERROR! Retrieved something other than HTML at $url")
                return null
            }*/

            return when (val statusCode = connection.response().statusCode()) {
                0, 200 -> {
                    Logger.p("SUCCESS! Received web page at $url")
                    connection.maxBodySize(MAX_BODY_SIZE).get()
                }

                else -> {
                    Logger.p("ERROR! Response status code for $url - $statusCode")
                    null
                }
            }
        } catch (ioe: IOException) {
            Logger.p("ERROR! Failed getting the page body at $url")
            Logger.p(ioe.message)
        }

        return null
    }
}