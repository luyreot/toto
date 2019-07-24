package models;

/**
 * SuperPattern Sub-Class for storing a number and its stats. (1-49)
 */
public class NumberPattern extends SuperPattern
{
    /**
     * Int saving the number (1-49)
     * NOTE: is 0 when saving a pattern instead of a number.
     */
    private final int number;

    public NumberPattern(int number)
    {
        super();
        this.number = number;
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
            System.out.println(number + " is not ok.");
    }

    // ==========================================================================================
    // Getter & Setter Methods
    // ==========================================================================================

    public int getNumber()
    {
        return number;
    }

}
