package ajava.main;

import ajava.data.FilterNumberSequences;
import ajava.data.GenerateNumberSequences;
import ajava.data.MarkovAlg;
import ajava.data.Stats;
import ajava.utils.TxtFileManager;

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
