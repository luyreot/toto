package data;

import utils.MySort;
import utils.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class filters a set of strings, number sequences of integer arrays, in various ways:
 * e.g. by each numbers probability, etc., and produces a map with the sequence as key and its calculated probability.
 */
public class FilterNumberSequences
{
    private Stats stats;
    private MarkovAlg markov;
    private GenerateNumberSequences generate;
    private Map<String, Double> bestNumberSequences;

    public FilterNumberSequences(Stats stats, MarkovAlg markov, GenerateNumberSequences generate)
    {
        this.stats = stats;
        this.markov = markov;
        this.generate = generate;
    }

    public void filterNumberSequences()
    {
        Map<String, Double> bns1 = sortSequencesByTotalProbability(generate.getSequences(), false);
        // TODO should i use two pair per drawing probability??
        Map<String, Integer> bns2 = sortSequencesByTwoPairProbability(generate.getSequences());
    }

    public Map<String, Integer> sortSequencesByTwoPairProbability(Set<String> sequences)
    {
        Map<String, Integer> map = new HashMap<>();
        sequences.forEach(x ->
        {
            int score = 0;
            int[] array = Utils.convertStringToIntegerArray(x);
            for (int n1 = 0; n1 < array.length; n1++)
                for (int n2 = n1 + 1; n2 < array.length; n2++)
                    score += markov.getNumberPatternsChainPerSameDrawing().get(array[n1]).get(array[n2]);
            map.put(x, score);
        });
        return MySort.sortMapByValue(map);
    }

    /**
     * Adds or Multiplies the probabilities for each number of a single number sequence together
     * and puts that sequence and its result in a map. At the end returns the map, sorted by value.
     *
     * @param sequences Set of number sequences
     * @param addition  True - use addition when calculating the probability, False - use multiplication
     * @return A unsorted map of sequences and their probabilities
     */
    public Map<String, Double> sortSequencesByTotalProbability(Set<String> sequences, boolean addition)
    {
        Map<String, Double> map = new HashMap<>();
        sequences.forEach(x ->
        {
            int[] array = Utils.convertStringToIntegerArray(x);
            double probability = addition ? 0d : 1d;
            for (int num : array)
            {
                if (addition)
                    probability += stats.getNumberPatterns().get(num).getProbability();
                else
                    probability *= stats.getNumberPatterns().get(num).getProbability();
            }
            map.put(x, probability);
        });
        return MySort.sortMapByValue(map);
    }

    // ==========================================================================================
    // Getter & Setter Methods
    // ==========================================================================================

    public Map<String, Double> getBestNumberSequences()
    {
        return bestNumberSequences;
    }

}
