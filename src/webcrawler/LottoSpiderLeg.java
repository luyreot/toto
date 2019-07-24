package webcrawler;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * A crawler secondary (spider leg) class that reads a html page and locates the needed information.
 */
public class LottoSpiderLeg
{
    // A fake USER_AGENT so the web server thinks the robot is a normal web browser.
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";

    private Document htmlDocument;
    private StringBuilder winningNumbersBuilder = new StringBuilder();

    boolean testUrl(String url)
    {
        return crawl(url) && didGetWinningNumbers();
    }

    /**
     * The method where the actual crawl happens.
     * Returns true if the crawl was successful.
     * Also loads the html content in a class variable to be read by another method.
     *
     * @param url The page url to be crawled
     * @return true if the crawl was successful,false if it wasn't
     */
    private boolean crawl(String url)
    {
        try
        {
            Connection connection = Jsoup.connect(url).userAgent(USER_AGENT);
            // the max html file size to be read, doesn't work if the number is too low aka the page is too large
            final int maxBodySize = 10048000;
            htmlDocument = connection.maxBodySize(maxBodySize).get();

            // 200 is the HTTP OK status code indicating that everything is great.
            if (connection.response().statusCode() == 200)
            {
                System.out.println("Success! Received web page at ".concat(url));
                return true;
            }
            if (!connection.response().contentType().contains("text/html"))
            {
                System.out.println("ERROR! Retrieved something other than HTML at ".concat(url));
                return false;
            }
        } catch (IOException ioe)
        {
            System.out.println("ERROR! HTTP request was not successful at ".concat(url));
            return false;
        }
        return false;
    }

    /**
     * Reads the web page and looks for the specific content - in this case a lotto number combination.
     */
    private boolean didGetWinningNumbers()
    {
        // Defensive coding. This method should only be used after a successful crawl.
        if (htmlDocument == null)
        {
            System.out.println("ERROR! Html Document is empty.");
            return false;
        }

        //Elements numbers = this.htmlDocument.select("span[class*=ball-white]");
        Elements numbers = htmlDocument.select("div.tir_numbers > div.row > div.col-sm-6.text-right.nopadding > span.ball-white");
        winningNumbersBuilder = new StringBuilder();

        for (int i = 0; i < numbers.size(); i++)
            winningNumbersBuilder.append(i == numbers.size() - 1 ? numbers.get(i).text() : numbers.get(i).text().concat(","));

        return winningNumbersBuilder.length() > 0;
    }

    String getWinningNumbersAsString()
    {
        return winningNumbersBuilder.toString();
    }
}
