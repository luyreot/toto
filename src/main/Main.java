package main;

import data.FilterNumberSequences;
import data.GenerateNumberSequences;
import data.MarkovAlg;
import data.Stats;
import utils.Const;
import utils.IO;
import utils.TxtFileManager;
import webcrawler.LottoSpider;

public class Main {
    public static void main(String[] args) {
        updateYearList();

        String yearFilter = "2000";

        TxtFileManager.getInstance().loadData();
        TxtFileManager.getInstance().convertMappedDataToList(yearFilter);

        Stats stats = new Stats(yearFilter);
        stats.generate();
        stats.calculatePatternProbabilities();
        stats.sortPatternsInAscendingOrder();

        MarkovAlg markov = new MarkovAlg(stats);
        markov.train();
        markov.sortPatternChains();
        markov.getNextBestPatterns();
        markov.getNextBestNumbersForSequencing();

        GenerateNumberSequences generate = new GenerateNumberSequences();
        generate.generateNextSequenceViaMarkov(markov);

        FilterNumberSequences filter = new FilterNumberSequences(stats, markov, generate);
        filter.filterNumberSequences();
    }

    /**
     * Updates the current year with the latest lotto drawings.
     * Makes use of a {@link LottoSpider} and {@link webcrawler.LottoSpiderLeg}.
     */
    private static void updateYearList() {
        LottoSpider spider = new LottoSpider();
        spider.loadFile();
        spider.search();
        IO.saveFile(Const.getCurrentYearTxtFilePath(), spider.getFileContentsAsString());
    }

}
