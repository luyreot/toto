package data;

import models.NumberPattern;
import models.StringPattern;
import utils.MySort;
import utils.TxtFileManager;
import utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Calculates and stores statistical information on how ofter a number and pattern of different type has occurred for lotto drawings since a provided year.
 */
public class Stats
{
    private String yearFilter;
    private int drawingCounter;
    private Map<Integer, NumberPattern> numberPatterns;
    private Map<String, StringPattern> colorPatterns;
    private Map<String, StringPattern> highLowPatterns;
    private Map<String, StringPattern> oddEvenPatterns;

    public Stats(String yearFilter)
    {
        // if the year filter is all / less than the first year when there is collected data
        this.yearFilter = (yearFilter.equals("all") || yearFilter.compareTo("1958") < 0) ? "1958" : yearFilter;
        this.drawingCounter = 0;
        this.numberPatterns = new HashMap<>();
        this.colorPatterns = new HashMap<>();
        this.highLowPatterns = new HashMap<>();
        this.oddEvenPatterns = new HashMap<>();
    }

    /**
     * Generates the statistical data on how often a number/pattern has occurred in the collected lotto data.
     * The {@link #drawingCounter} variables is used for counting the sequential numerical order of each drawing.
     */
    public void generate()
    {
        for (String drawingStr : TxtFileManager.getInstance().getListerizedData())
        {
            int[] drawing = Utils.convertStringToIntegerArray(drawingStr);
            drawingCounter++; // count each drawing
            // process the number occurrences
            for (int number : drawing) processNumber(number);
            // process the color pattern
            processPattern(Utils.getColorPatternAsString(drawingStr), colorPatterns);
            // process the high/low pattern
            processPattern(Utils.getHighLowPattern(drawingStr), highLowPatterns);
            // process the odd/even pattern
            processPattern(Utils.getOddEvenPattern(drawingStr), oddEvenPatterns);
        }
        System.out.println("Total number of drawings since " + yearFilter + " - " + drawingCounter);
        checkPatternsTimesOccurredFrequenciesRelation();
    }

    /**
     * Processes a number (1-49) by either adding it the respective map by creating a new pattern object or updating an already existing one.
     *
     * @param number
     */
    private void processNumber(int number)
    {
        if (numberPatterns.containsKey(number))
        {
            // number is already in the map
            numberPatterns.get(number).incrementTimesOccurred();
            numberPatterns.get(number).addFrequency(drawingCounter);
        } else
        {
            // number must be added to the map
            NumberPattern pattern = new NumberPattern(number);
            pattern.incrementTimesOccurred();
            pattern.setLastSpot(drawingCounter);
            numberPatterns.put(number, pattern);
        }
    }

    /**
     * Processes a lotto drawing's pattern by either adding it the respective map by creating a new pattern object or updating an already existing one.
     *
     * @param pattern
     * @param patternMap
     */
    private void processPattern(String pattern, Map<String, StringPattern> patternMap)
    {
        if (patternMap.containsKey(pattern))
        {
            // pattern is already in the map
            patternMap.get(pattern).incrementTimesOccurred();
            patternMap.get(pattern).addFrequency(drawingCounter);
        } else
        {
            // pattern must be added to the map
            StringPattern patternObj = new StringPattern(pattern);
            patternObj.incrementTimesOccurred();
            patternObj.setLastSpot(drawingCounter);
            patternMap.put(pattern, patternObj);
        }
    }

    /**
     * Checks the generated statistical data for irregularities of the calculated frequencies for each type of pattern.
     */
    public void checkPatternsTimesOccurredFrequenciesRelation()
    {
        System.out.println("Checking patterns for irregularities.");
        numberPatterns.values().forEach(NumberPattern::isTimesOccurredFrequenciesRelationOk);
        colorPatterns.values().forEach(StringPattern::isTimesOccurredFrequenciesRelationOk);
        highLowPatterns.values().forEach(StringPattern::isTimesOccurredFrequenciesRelationOk);
        oddEvenPatterns.values().forEach(StringPattern::isTimesOccurredFrequenciesRelationOk);
    }

    /**
     * Calculates the probabilities for each unique pattern and prints out a message if there were any irregularities in the process.
     */
    public void calculatePatternProbabilities()
    {
        // number patterns
        int numberPatternsTotal = numberPatterns.values().stream().mapToInt(NumberPattern::getTimesOccurred).sum();
        for (Map.Entry<Integer, NumberPattern> entry : numberPatterns.entrySet())
        {
            entry.getValue().calculateProbability(numberPatternsTotal);
            entry.getValue().calculateFrequenciesProbabilities();
        }
        System.out.println(numberPatterns.values().stream().mapToDouble(NumberPattern::getProbability).sum() == 1f
                ? "Number patterns probability is ok." : "Number patterns probability is not ok.");
        numberPatterns.values().forEach(NumberPattern::areFrequenciesProbabilitiesOk);

        // color patterns
        int colorPatternsTotal = colorPatterns.values().stream().mapToInt(StringPattern::getTimesOccurred).sum();
        for (Map.Entry<String, StringPattern> entry : colorPatterns.entrySet())
        {
            entry.getValue().calculateProbability(colorPatternsTotal);
            entry.getValue().calculateFrequenciesProbabilities();
        }
        System.out.println(colorPatterns.values().stream().mapToDouble(StringPattern::getProbability).sum() == 1f
                ? "Color patterns probability is ok." : "Color patterns probability is not ok.");
        colorPatterns.values().forEach(StringPattern::areFrequenciesProbabilitiesOk);

        // high/low patterns
        int highLowPatternsTotal = highLowPatterns.values().stream().mapToInt(StringPattern::getTimesOccurred).sum();
        for (Map.Entry<String, StringPattern> entry : highLowPatterns.entrySet())
        {
            entry.getValue().calculateProbability(highLowPatternsTotal);
            entry.getValue().calculateFrequenciesProbabilities();
        }
        System.out.println(highLowPatterns.values().stream().mapToDouble(StringPattern::getProbability).sum() == 1f
                ? "High/Low patterns probability is ok." : "High/Low patterns probability is not ok.");
        highLowPatterns.values().forEach(StringPattern::areFrequenciesProbabilitiesOk);

        // odd/even patterns
        int oddEvenPatternsTotal = oddEvenPatterns.values().stream().mapToInt(StringPattern::getTimesOccurred).sum();
        for (Map.Entry<String, StringPattern> entry : oddEvenPatterns.entrySet())
        {
            entry.getValue().calculateProbability(oddEvenPatternsTotal);
            entry.getValue().calculateFrequenciesProbabilities();
        }
        System.out.println(oddEvenPatterns.values().stream().mapToDouble(StringPattern::getProbability).sum() == 1f
                ? "Odd/Even patterns probability is ok." : "Odd/Even patterns probability is not ok.");
        oddEvenPatterns.values().forEach(StringPattern::areFrequenciesProbabilitiesOk);
    }

    /**
     * Sorts each map by its values in ascending order.
     */
    public void sortPatternsInAscendingOrder()
    {
        System.out.println("Sorting all patterns by value in descending order.");
        numberPatterns = MySort.sortMapByValue(numberPatterns);
        numberPatterns.values().forEach(NumberPattern::sortFrequenciesInAscendingOrder);

        colorPatterns = MySort.sortMapByValue(colorPatterns);
        colorPatterns.values().forEach(StringPattern::sortFrequenciesInAscendingOrder);

        highLowPatterns = MySort.sortMapByValue(highLowPatterns);
        highLowPatterns.values().forEach(StringPattern::sortFrequenciesInAscendingOrder);

        oddEvenPatterns = MySort.sortMapByValue(oddEvenPatterns);
        oddEvenPatterns.values().forEach(StringPattern::sortFrequenciesInAscendingOrder);
    }

    // ==========================================================================================
    // Custom Getter Methods
    // ==========================================================================================

    /**
     * Returns a list of the top {@param targetSize} color patterns.
     *
     * @param targetSize
     * @return
     */
    public List<StringPattern> getTopColorPatterns(int targetSize)
    {
        List<StringPattern> list = new ArrayList<>();
        for (StringPattern pattern : colorPatterns.values())
        {
            list.add(pattern);
            if (list.size() == targetSize) break;
        }
        return list;
    }

    /**
     * @return The top/first color pattern, the one with the highest probability.
     */
    public StringPattern getTopColorPattern()
    {
        return colorPatterns.entrySet().iterator().next().getValue();
    }

    /**
     * @return The top/first high/low pattern, the one with the highest probability.
     */
    public StringPattern getTopHighLowPattern()
    {
        return highLowPatterns.entrySet().iterator().next().getValue();
    }

    /**
     * @return The top/first odd/even pattern, the one with the highest probability.
     */
    public StringPattern getTopOddEvenPattern()
    {
        return oddEvenPatterns.entrySet().iterator().next().getValue();
    }

    // ==========================================================================================
    // Getter & Setter Methods
    // ==========================================================================================

    public String getYearFilter()
    {
        return yearFilter;
    }

    public int getDrawingCounter()
    {
        return drawingCounter;
    }

    public Map<Integer, NumberPattern> getNumberPatterns()
    {
        return numberPatterns;
    }

    public Map<String, StringPattern> getColorPatterns()
    {
        return colorPatterns;
    }

    public Map<String, StringPattern> getHighLowPatterns()
    {
        return highLowPatterns;
    }

    public Map<String, StringPattern> getOddEvenPatterns()
    {
        return oddEvenPatterns;
    }

}
