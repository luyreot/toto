package models;

import org.jetbrains.annotations.NotNull;
import utils.MySort;

import java.util.HashMap;
import java.util.Map;

/**
 * A Super class for storing information on different types of patterns, numbers and their respective probabilities, frequencies of occurrence, etc.
 */
public class SuperPattern implements Comparable
{
    /**
     * Int saving how many times the pattern/number has occurred in checked drawings.
     */
    protected int timesOccurred;
    /**
     * The probability of this pattern/number.
     * Calculated by dividing the timesOccurred by the total sum of timesOccurred for all patterns.
     * When calculating for number divide the total sum by 6 because there are 6 numbers per drawing
     * in order to get the probability between 0 and 1, otherwise it will be between 0 and 6.
     */
    protected double probability;
    /**
     * Map containing Integers as keys, representing the different frequencies at which this pattern has occurred
     * and a Pattern Frequency object which holds additional information. See {@link PatternFrequency} for more info.
     */
    protected Map<Integer, PatternFrequency> frequencies;
    /**
     * The average frequency of this pattern/number.
     */
    protected double averageFrequency;
    /**
     * Helper int var used for calculating the frequency between two occurrences of the same pattern/number.
     */
    protected int lastSpot;

    // ==========================================================================================
    // Constructor Methods
    // ==========================================================================================

    public SuperPattern()
    {
        this.timesOccurred = 0;
        this.probability = 0d;
        this.frequencies = new HashMap<>();
        this.averageFrequency = 0d;
        this.lastSpot = 0;
    }

    // ==========================================================================================
    // Other Methods
    // ==========================================================================================

    /**
     * Increments the times occurred var by one each time this method is called.
     */
    public void incrementTimesOccurred()
    {
        this.timesOccurred++;
    }

    /**
     * Adds a new frequency to the frequencies map, if it already exist - merges it instead, by incrementing its count by 1.
     * The new frequency is calculated by finding the difference between the provided current position, also spot, and the last
     * position/spot of the pattern in the drawings. Every time we add new frequency we update the last spot variable.
     *
     * @param newSpot
     */
    public void addFrequency(int newSpot)
    {
        if (frequencies.containsKey(newSpot - lastSpot))
            frequencies.get(newSpot - lastSpot).incrementTimesOccurred();
        else
            frequencies.put(newSpot - lastSpot, new PatternFrequency(newSpot - lastSpot));
        setLastSpot(newSpot);
    }

    /**
     * Calculates the probability of this pattern by dividing the times it has occurred by the total sum of all other patterns' occurrences of the same type
     *
     * @param total
     */
    public void calculateProbability(int total)
    {
        probability = (double) timesOccurred / total;
    }

    /**
     * Calculates the probability of each single frequency for this pattern by dividing the times it has occurred by the total sum of occurrences of all other frequencies.
     * NOTE: for some patterns there will be no occurrences when the pattern has occurred only once so then we skip this step.
     */
    public void calculateFrequenciesProbabilities()
    {
        // skip if there are not frequencies, when timesOccurred is only 1
        if (frequencies.size() == 0) return;
        int sum = 0, size = 0;
        for (Map.Entry<Integer, PatternFrequency> entry : frequencies.entrySet())
        {
            sum += entry.getKey() * entry.getValue().getTimesOccurred();
            size += entry.getValue().getTimesOccurred();
        }
        // calculate the probabilities of each single frequency
        for (Map.Entry<Integer, PatternFrequency> entry : frequencies.entrySet())
            entry.getValue().calculateProbability(size);
        // calculate the average frequency
        averageFrequency = (double) sum / size;
    }

    /**
     * Prints whether the frequencies probabilities were calculated correctly. The total sum of all probabilities must be between 0.9 and 1.1,
     * also 1f, but due to the fact that we save them as doubles there are deviations sometimes.
     */
    public void areFrequenciesProbabilitiesOk()
    {
        if (frequencies.size() == 0) return;
        double sum = frequencies.values().stream().mapToDouble(PatternFrequency::getProbability).sum();
        if (sum < 0.9f && sum > 1.1f) System.out.println("Frequencies probabilities are not ok.");
    }

    /**
     * Sorts both frequencies maps by ascending order.
     */
    public void sortFrequenciesInAscendingOrder()
    {
        if (frequencies.size() == 0) return;
        frequencies = MySort.sortMapByValue(frequencies);
    }

    /**
     * Compares this pattern probability against another one's. Used for sorting map purposes.
     *
     * @param o
     * @return
     */
    @Override
    public int compareTo(@NotNull Object o)
    {
        return Double.compare(probability, ((SuperPattern) o).getProbability());
    }

    // ==========================================================================================
    // Getter & Setter Methods
    // ==========================================================================================

    public int getTimesOccurred()
    {
        return timesOccurred;
    }

    public double getProbability()
    {
        return probability;
    }

    public Map<Integer, PatternFrequency> getFrequencies()
    {
        return frequencies;
    }

    public double getAverageFrequency()
    {
        return averageFrequency;
    }

    public int getLastSpot()
    {
        return lastSpot;
    }

    public void setLastSpot(int lastSpot)
    {
        this.lastSpot = lastSpot;
    }
}
