package data;

import utils.MySort;
import utils.TxtFileManager;
import utils.Utils;

import java.util.*;

/**
 * Class implementing the Markov algorithm for generating text based on already trained text chains form a data set.
 */
public class MarkovAlg
{
    private Stats stats;
    private Map<String, Map<String, Integer>> colorPatternsChain;
    private Map<String, Map<String, Integer>> highLowPatternsChain;
    private Map<String, Map<String, Integer>> oddEvenPatternsChain;
    private Map<Integer, Map<Integer, Integer>> numberPatternsChainPerSameDrawing;
    private Map<Integer, Map<Integer, Integer>> numberPatternsChainPerConsecutiveDrawings;
    private String nextColorPattern, nextHighLowPattern, nextOddEvenPattern;
    private List<Integer> nextBestNumbers;

    public MarkovAlg(Stats stats)
    {
        this.stats = stats;
        this.colorPatternsChain = new HashMap<>();
        this.highLowPatternsChain = new HashMap<>();
        this.oddEvenPatternsChain = new HashMap<>();
        this.numberPatternsChainPerSameDrawing = new HashMap<>();
        this.numberPatternsChainPerConsecutiveDrawings = new HashMap<>();
        for (int i = 1; i <= 49; i++)
        {
            this.numberPatternsChainPerSameDrawing.put(i, new HashMap<>());
            this.numberPatternsChainPerConsecutiveDrawings.put(i, new HashMap<>());
        }
    }

    /**
     * Trains each pattern type chain - color, high/low, odd/even, by going through each string drawing and adding it the map.
     * Then it creates a link between each two (current and previous) and increments that set connection by 1 in the respective sub-map.
     */
    public void train()
    {
        System.out.println("Training for Markov Text Chain Generation for string patterns.");
        String prevColorPattern = null, prevHighLowPattern = null, prevOddEvenPattern = null;
        int[] prevDrawing = null;
        for (String drawingStr : TxtFileManager.getInstance().getListerizedData())
        {
            // patterns
            prevColorPattern = processPattern(prevColorPattern, Utils.getColorPatternAsString(drawingStr), colorPatternsChain);
            prevHighLowPattern = processPattern(prevHighLowPattern, Utils.getHighLowPattern(drawingStr), highLowPatternsChain);
            prevOddEvenPattern = processPattern(prevOddEvenPattern, Utils.getOddEvenPattern(drawingStr), oddEvenPatternsChain);
            // numbers
            processNumberPatternPerSameDrawing(Utils.convertStringToIntegerArray(drawingStr));
            prevDrawing = processNumberPatternPerConsecutiveDrawings(prevDrawing, (Utils.convertStringToIntegerArray(drawingStr)));
        }
    }

    /**
     * Sorts all pattern chain sub-maps by their value, also integer - how many times a particular set of 2 patterns has occurred.
     */
    public void sortPatternChains()
    {
        System.out.println("Sorting pattern chains by value in descending order.");
        for (Map.Entry<String, Map<String, Integer>> entry : colorPatternsChain.entrySet())
            entry.setValue(MySort.sortMapByValue(entry.getValue()));
        for (Map.Entry<String, Map<String, Integer>> entry : highLowPatternsChain.entrySet())
            entry.setValue(MySort.sortMapByValue(entry.getValue()));
        for (Map.Entry<String, Map<String, Integer>> entry : oddEvenPatternsChain.entrySet())
            entry.setValue(MySort.sortMapByValue(entry.getValue()));
        for (Map.Entry<Integer, Map<Integer, Integer>> entry : numberPatternsChainPerSameDrawing.entrySet())
            entry.setValue(MySort.sortMapByValue(entry.getValue()));
        for (Map.Entry<Integer, Map<Integer, Integer>> entry : numberPatternsChainPerConsecutiveDrawings.entrySet())
            entry.setValue(MySort.sortMapByValue(entry.getValue()));
    }

    /**
     * Processes a pattern for a particular chain (color, high/low, odd/even).
     * The previous pattern will be null only the very first time this method is called for a particular pattern chain.
     * It is null because the pattern of the very first drawing in the data set sequences is the current one being processed.
     * After the initial call the previous pattern is used to make the set of 2 (previous + current).
     * That's why the method returns the last/current pattern that was processed.
     *
     * @param prevPattern   The previously processed pattern
     * @param currPattern   The current pattern being processed
     * @param patternsChain The chain which the patterns belong to.
     * @return The current pattern that was just processed
     */
    private String processPattern(String prevPattern, String currPattern, Map<String, Map<String, Integer>> patternsChain)
    {
        if (prevPattern == null)
        {
            patternsChain.put(currPattern, new HashMap<>());
            return currPattern;
        }
        patternsChain.get(prevPattern).merge(currPattern, 1, Integer::sum);
        if (!patternsChain.containsKey(currPattern)) patternsChain.put(currPattern, new HashMap<>());
        return currPattern;
    }

    /**
     * Processes an int array drawing while counting how often each pair of integers occurs and stores that value in a map.
     * (1,2) but also (2,1), these count values should be the same for both map entries 1 and 2.
     *
     * @param drawing
     */
    private void processNumberPatternPerSameDrawing(int[] drawing)
    {
        for (int i = 0; i < drawing.length; i++)
            for (int j = 0; j < drawing.length; j++)
            {
                if (i == j) continue;
                numberPatternsChainPerSameDrawing.get(drawing[i]).merge(drawing[j], 1, Integer::sum);
                numberPatternsChainPerSameDrawing.get(drawing[j]).merge(drawing[i], 1, Integer::sum);
            }
    }

    /**
     * Processes a current int array drawing while comparing each of its numbers with each number of the previous drawing
     * and counts how many times each pair has occurred in a map.
     * Returns the current drawing so it can be used as the previous one the next time when this method is called.
     *
     * @param prevDrawing
     * @param currDrawing
     * @return
     */
    private int[] processNumberPatternPerConsecutiveDrawings(int[] prevDrawing, int[] currDrawing)
    {
        if (prevDrawing == null) return currDrawing;
        for (int iPrev = 0; iPrev < prevDrawing.length; iPrev++)
            for (int jCurr = 0; jCurr < currDrawing.length; jCurr++)
                numberPatternsChainPerConsecutiveDrawings.get(prevDrawing[iPrev]).merge(currDrawing[jCurr], 1, Integer::sum);
        return currDrawing;
    }

    /**
     * See {@link #getNextBestPatterns()}
     */
    public void getNextBestPatterns()
    {
        nextColorPattern = getNextBestPattern(Utils.getColorPatternAsString(TxtFileManager.getInstance().getListerizedData().get(TxtFileManager.getInstance().getListerizedData().size() - 1)), colorPatternsChain, "color");
        nextHighLowPattern = getNextBestPattern(Utils.getHighLowPattern(TxtFileManager.getInstance().getListerizedData().get(TxtFileManager.getInstance().getListerizedData().size() - 1)), highLowPatternsChain, "high/low");
        nextOddEvenPattern = getNextBestPattern(Utils.getOddEvenPattern(TxtFileManager.getInstance().getListerizedData().get(TxtFileManager.getInstance().getListerizedData().size() - 1)), oddEvenPatternsChain, "odd/even");
    }

    /**
     * Return the next best pattern as string for a provided pattern type (color, high/low or odd/even).
     *
     * @param lastDrawnPattern Last drawn lotto pattern of a particular type
     * @param patternChain     The pattern chain that will be checked for the last drawn pattern
     * @param patternType      color | high/low | odd/even
     * @return The next best pattern
     */
    public String getNextBestPattern(String lastDrawnPattern, Map<String, Map<String, Integer>> patternChain, String patternType)
    {
        if (!patternChain.containsKey(lastDrawnPattern) || patternChain.get(lastDrawnPattern).size() == 0)
        {
            System.out.println("Last drawn " + patternType + " pattern doesn't exist in the chain or its map is empty.");
            return null;
        }
        System.out.println("Last drawn " + patternType + " pattern: " + lastDrawnPattern);
        // get a list with one or more pattern with the highest(same) probability for a particular pattern chain
        List<String> topPatternChainResults = getPatternsWithHighestProbability(patternChain.get(lastDrawnPattern));
        if (topPatternChainResults.size() == 1)
        {   // when the list has only one element just return it
            System.out.println("Next Best " + patternType + " pattern - " + topPatternChainResults.get(0));
            return topPatternChainResults.get(0);
        }
        // when the list has 2 or more elements find out the best one and return it
        // compare each list element's probability using the information from the Stats class
        List<String> topResults = new ArrayList<>();
        String topResult = "";
        for (String res : topPatternChainResults)
        {
            if (topResults.isEmpty())
            {   // store a reference to the first list element
                topResults.add(res);
                topResult = res;
                continue;
            }
            // check which element has the highest probability, when 2 or more have the same - add them all in the list and then return one at random
            if (patternType.equals("color"))
            {
                if (stats.getColorPatterns().get(res).getProbability() == stats.getColorPatterns().get(topResult).getProbability())
                {
                    topResults.add(res);
                } else if (stats.getColorPatterns().get(res).getProbability() > stats.getColorPatterns().get(topResult).getProbability())
                {
                    topResults.clear();
                    topResults.add(res);
                    topResult = res;
                }
            } else if (patternType.equals("high/low"))
            {
                if (stats.getHighLowPatterns().get(res).getProbability() == stats.getHighLowPatterns().get(topResult).getProbability())
                {
                    topResults.add(res);
                } else if (stats.getHighLowPatterns().get(res).getProbability() > stats.getHighLowPatterns().get(topResult).getProbability())
                {
                    topResults.clear();
                    topResults.add(res);
                    topResult = res;
                }
            } else if (patternType.equals("odd/even"))
            {
                if (stats.getOddEvenPatterns().get(res).getProbability() == stats.getOddEvenPatterns().get(topResult).getProbability())
                {
                    topResults.add(res);
                } else if (stats.getOddEvenPatterns().get(res).getProbability() > stats.getOddEvenPatterns().get(topResult).getProbability())
                {
                    topResults.clear();
                    topResults.add(res);
                    topResult = res;
                }
            }
        }
        System.out.println("Choosing one " + patternType + " pattern out of " + topPatternChainResults.size() + " - " + topPatternChainResults.toString());
        topResult = topResults.size() == 1 ? topResults.get(0) : topResults.get(new Random().nextInt(topResults.size()));
        System.out.println("Choosing Top " + patternType + " pattern - " + topResult);
        return topResult;
    }

    /**
     * Returns a list of strings containing the pattern(s) with the highest probability of a provided map.
     *
     * @param patternsMap
     * @return
     */
    private List<String> getPatternsWithHighestProbability(Map<String, Integer> patternsMap)
    {
        // NOTE: map is already sorted by value in descending order, highest int is first
        List<String> topResults = new ArrayList<>();
        Map.Entry<String, Integer> topEntry = null; // stores the very first map element that has the highest integer
        for (Map.Entry<String, Integer> entry : patternsMap.entrySet())
        {
            if (topEntry == null)
            {   // stored a reference to the first map element
                topEntry = entry;
                topResults.add(entry.getKey());
                continue;
            }
            // if the 2nd map element doesn't have the same integer value as the first just exit the loop
            if (topEntry.getValue() != entry.getValue()) break;
            // add every map element that has the same integer value as the first one
            topResults.add(entry.getKey());
        }
        return topResults;
    }

    /**
     * Get the last drawing and for each of its numbers get a 6 top numbers to be drawn next, with the highest probability, and store them in a set.
     */
    public void getNextBestNumbersForSequencing()
    {
        // TODO don't use set do duplicates will be allowed and some numbers will higher probability
        Set<Integer> numberSet = new HashSet<>();
        int[] lastDrawing = Utils.convertStringToIntegerArray(TxtFileManager.getInstance().getListerizedData().get(TxtFileManager.getInstance().getListerizedData().size() - 1));
        for (int num : lastDrawing) numberSet.addAll(getTopNumbersForConsecutiveDrawings(num));
        nextBestNumbers = new ArrayList<>(numberSet);
    }

    /**
     * See {@link #getNextBestNumbersForSequencing()}
     *
     * @param number
     * @return
     */
    private Set<Integer> getTopNumbersForConsecutiveDrawings(int number)
    {
        int counter = 0;
        Set<Integer> results = new HashSet<>();
        Map.Entry<Integer, Integer> lastAddedEntry = null;
        for (Map.Entry<Integer, Integer> entry : numberPatternsChainPerConsecutiveDrawings.get(number).entrySet())
        {
            if (results.isEmpty())
            {
                results.add(entry.getKey());
                lastAddedEntry = entry;
                counter++;
                continue;
            }
            // TODO should only the first 6 numbers count??
            if (counter == 6)
                if (entry.getValue() == lastAddedEntry.getValue())
                {
                    results.add(entry.getKey());
                    lastAddedEntry = entry;
                    continue;
                } else break;
            results.add(entry.getKey());
            lastAddedEntry = entry;
            counter++;
        }
        return results;
    }

    // ==========================================================================================
    // Getter & Setter Methods
    // ==========================================================================================

    public Map<String, Map<String, Integer>> getColorPatternsChain()
    {
        return colorPatternsChain;
    }

    public Map<String, Map<String, Integer>> getHighLowPatternsChain()
    {
        return highLowPatternsChain;
    }

    public Map<String, Map<String, Integer>> getOddEvenPatternsChain()
    {
        return oddEvenPatternsChain;
    }

    public Map<Integer, Map<Integer, Integer>> getNumberPatternsChainPerSameDrawing()
    {
        return numberPatternsChainPerSameDrawing;
    }

    public Map<Integer, Map<Integer, Integer>> getNumberPatternsChainPerConsecutiveDrawings()
    {
        return numberPatternsChainPerConsecutiveDrawings;
    }

    public String getNextColorPattern()
    {
        return nextColorPattern;
    }

    public String getNextHighLowPattern()
    {
        return nextHighLowPattern;
    }

    public String getNextOddEvenPattern()
    {
        return nextOddEvenPattern;
    }

    public List<Integer> getNextBestNumbers()
    {
        return nextBestNumbers;
    }

}
