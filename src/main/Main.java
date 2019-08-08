package main;

import data.FilterNumberSequences;
import data.GenerateNumberSequences;
import data.MarkovAlg;
import data.Stats;
import utils.TxtFileManager;

public class Main {

    public void main(String[] args) {
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

}
