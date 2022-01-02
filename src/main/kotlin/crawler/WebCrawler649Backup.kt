package crawler

import extension.appendLine
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import util.Const
import util.Const.YEAR
import util.IO
import java.io.IOException

class WebCrawler649Backup {

    // region Web

    // A fake user agent so the web server thinks the robot is a normal web browser.
    private val userAgent: String =
        "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1"

    // The max html file size to be read, doesn't work if the number is too low aka the page is too large
    private val maxBodySize: Int = 10048000
    private val url: String = "https://bgtoto.com/6ot49_arhiv.php/"

    // Alternative css query: "span[class*=ball-white]"
    private val documentQuery =
        "body > table > tbody > tr > td > table > tbody > tr > td > table > tbody > tr > td > div > center > table > tbody > tr > td > table > tbody > tr"

    // endregion Web

    // region File contents

    private val contentBuilder = StringBuilder()
    private val currentYearPath = Const.PATH_TXT + YEAR

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
        var saveToFile = false
        var drawingIndex = drawingCount

        val document = readPage()

        if (document == null) {
            println("ERROR! HTML Document is empty")
            return
        }

        // Gets the individual numbers as a list of elements
        val numbers: Elements = document.select(documentQuery)
        if (numbers.isEmpty()) {
            println("ERROR! Didn't select any elements")
            return
        }

        val numbersReversed = numbers.reversed()

        // 0: <td>1</td>
        // 1: <td>8,11,14,16,34,39</td>
        // 2: <td></td>
        try {
            numbersReversed.forEach { drawing ->
                if (drawing.children().size == 3) {
                    val drawingNumber = drawing.child(0).toString().replace("<td>", "").replace("</td>", "").toInt()
                    if (drawingNumber == drawingIndex) {
                        val drawingNumbers = drawing.child(1).toString().replace("<td>", "").replace("</td>", "").trim()
                        contentBuilder.appendLine(drawingNumbers)
                        drawingIndex += 1
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (saveToFile.not() && contentBuilder.isNotBlank()) {
            saveToFile = true
        }

        if (saveToFile) {
            println("INFO: Saving new drawings...")
            IO.saveTxtFile(currentYearPath, contentBuilder.toString())
            return
        }

        println("INFO: No new drawings were added")
    }

    private fun readPage(): Document? {
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
            WebCrawler649Backup().crawl()
        }
    }

}