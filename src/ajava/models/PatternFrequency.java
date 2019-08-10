package ajava.models;

import org.jetbrains.annotations.NotNull;

/**
 * A class which holds information about a single frequency, also difference in spots, between two occurrences of a pattern.
 */
public class PatternFrequency implements Comparable
{
    /**
     * The frequency itself.
     */
    private int freq;
    /**
     * How many times has this frequency occurred.
     */
    private int timesOccurred;
    /**
     * What is this frequency's probability.
     */
    private double probability;

    public PatternFrequency(int freq)
    {
        this.freq = freq;
        this.timesOccurred = 0;
        this.probability = 0d;
        incrementTimesOccurred();
    }

    /**
     * Increments the counter for how many times this frequency has occurred by 1.
     */
    public void incrementTimesOccurred()
    {
        this.timesOccurred++;
    }

    /**
     * Calculates this frequency's probability by dividing the times is has occurred by the total number of all other frequencies.
     *
     * @param total
     */
    public void calculateProbability(int total)
    {
        this.probability = (double) this.timesOccurred / total;
    }

    /**
     * Compare method for sorting PatternFrequency objects according to their probability.
     *
     * @param o
     * @return
     */
    @Override
    public int compareTo(@NotNull Object o)
    {
        return Double.compare(probability, ((PatternFrequency) o).getProbability());
    }

    // ==========================================================================================
    // Getter & Setter Methods
    // ==========================================================================================

    public int getFreq()
    {
        return freq;
    }

    public int getTimesOccurred()
    {
        return timesOccurred;
    }

    public double getProbability()
    {
        return probability;
    }

}
