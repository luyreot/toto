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

    private val cookies = mutableMapOf<String, String>()

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
            // Alternative css query: "span[class*=ball-white]"
            val numbers: Elements =
                document.select("div.tir_numbers > div.row > div.col-sm-6.text-right.nopadding > span.ball-white")
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
            val connection = Jsoup
                .connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .header("Accept-Language", "bg-BG,bg;q=0.9,en-US;q=0.8,en;q=0.7")
                .cookies(cookies)
                .followRedirects(true)
                .timeout(15_000)
                .apply {
                    execute().also {
                        cookies.putAll(it.cookies())
                    }
                }

            /* Crawler doesn't return a contentType
            if (!connection.response().contentType().contains("text/html")) {
                Logme.p("ERROR! Retrieved something other than HTML at $url")
                return null
            }*/

            return when (val statusCode = connection.response().statusCode()) {
                0, 200 -> {
                    Logger.p("SUCCESS! Received web page at $url")
                    connection.maxBodySize(10048000).get() // Some big number
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