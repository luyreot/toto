package ajava.models;

/**
 * SuperPattern Sub-Class for storing a string pattern and its stats.
 */
public class StringPattern extends SuperPattern
{
    /**
     * String containing the pattern:
     * color pattern (eg. 0,1,1,2,3,4)
     * high/low pattern (eg. hhhlll)
     * odd/even pattern (eb. eeeooo)
     * NOTE: is null when saving a number instead of a pattern.
     */
    private final String pattern;

    public StringPattern(String pattern)
    {
        super();
        this.pattern = pattern;
    }

    /**
     * Prints a message whether the frequencies have been added correctly. For 10 times a pattern has occurred there must be 9 frequencies,
     * also 9 relations between each time the pattern has occurred - 1-2, 2-3, 3-4,..,8-9,9-10.
     */
    public void isTimesOccurredFrequenciesRelationOk()
    {
        // skip if there are not frequencies, when timesOccurred is only 1
        if (frequencies.size() == 0) return;
        // sum of frequencies mut be equal to timesOccurred - 1
        if (frequencies.values().stream().mapToInt(PatternFrequency::getTimesOccurred).sum() != timesOccurred - 1)
            System.out.println(pattern + " is not ok");
    }

    // ==========================================================================================
    // Getter & Setter Methods
    // ==========================================================================================

    public String getPattern()
    {
        return pattern;
    }

}
