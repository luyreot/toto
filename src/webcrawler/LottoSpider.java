package webcrawler;

import utils.Const;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * A crawler main (spider) class that initializes the web crawl.
 */
public class LottoSpider
{
    // the contents of that file
    private StringBuilder fileContentsBuilder;
    // the last saved lotto drawing index from the file, eg. 77th, 88th etc.
    // this is used so the crawler knows which will be the next one to be crawled
    private int previousDrawingsCount;

    public LottoSpider()
    {
        this.fileContentsBuilder = new StringBuilder();
        this.previousDrawingsCount = 0;
    }

    /**
     * Tries to load an already existing file with the file name from set in the constructor.
     * Loads its contents if it does exist, also finds and sets the last lotto drawing, eg. 77th ot 88th.
     */
    public void loadFile()
    {
        try
        {
            fileContentsBuilder.append(new Scanner(new File(Const.getCurrentYearTxtFilePath())).useDelimiter("\\Z").next());
            // counts the lines in the txt file
            previousDrawingsCount = fileContentsBuilder.toString().split("\r\n|\r|\n").length;

            System.out.println(previousDrawingsCount + " drawings for ".concat(Const.CURRENT_YEAR));

            fileContentsBuilder.append("\n");
        } catch (FileNotFoundException e)
        {
            System.out.println("File with name - ".concat(Const.CURRENT_YEAR).concat(", does not exists! Stopping the crawl!"));
        }
    }

    /**
     * Check the provided url for updates and adds the new contents.
     */
    public void search()
    {
        LottoSpiderLeg leg = new LottoSpiderLeg();
        while (leg.testUrl(Const.getCurrentYearPageUrl().concat(Integer.toString(++previousDrawingsCount))))
        {
            fileContentsBuilder.append(leg.getWinningNumbersAsString());
            fileContentsBuilder.append("\n");
        }
    }

    public String getFileContentsAsString()
    {
        return fileContentsBuilder.toString();
    }
}
