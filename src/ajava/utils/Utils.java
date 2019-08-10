package ajava.utils;

import java.awt.*;
import java.util.*;
import java.util.List;

public final class Utils
{
    // ==========================================================================================
    // Color Pattern Methods
    // ==========================================================================================

    /**
     * Return a color that is corresponding to a pattern id (0-4).
     * Used for visual representation of color patterns.
     * Check {@link #generateCompleteColorPatternTemplate()} method for more info.
     *
     * @param patternID
     * @return Returns the color corresponding to the pattern id or black as default.
     */
    public static Color getColor(int patternID)
    {
        switch (patternID)
        {
            case 0:
                return Color.YELLOW;
            case 1:
                return Color.CYAN;
            case 2:
                return Color.GRAY;
            case 3:
                return Color.GREEN;
            case 4:
                return Color.MAGENTA;
            default:
                return Color.BLACK;
        }
    }

    /**
     * Private static map variable that stores the color pattern template. Used when requesting a color pattern template multiple times.
     * Check {@link #generateCompleteColorPatternTemplate()} method for more info.
     */
    private static Map<Integer, List<Integer>> colorTemplate;

    /**
     * Creates a Map with the following color patterns template:
     * 0 - 1..9, 1 - 10..19, 2 - 20..29, etc.
     *
     * @return
     */
    public static Map<Integer, List<Integer>> generateCompleteColorPatternTemplate()
    {
        if (colorTemplate != null) return colorTemplate;
        colorTemplate = new HashMap<>();
        for (int i = 0; i <= 4; i++)
        {
            List<Integer> list = new ArrayList<>();
            switch (i)
            {
                case 0:
                    for (int num = 1; num <= 9; num++) list.add(num);
                    break;
                case 1:
                    for (int num = 10; num <= 19; num++) list.add(num);
                    break;
                case 2:
                    for (int num = 20; num <= 29; num++) list.add(num);
                    break;
                case 3:
                    for (int num = 30; num <= 39; num++) list.add(num);
                    break;
                case 4:
                    for (int num = 40; num <= 49; num++) list.add(num);
                    break;
            }
            colorTemplate.put(i, list);
        }
        return colorTemplate;
    }

    /**
     * Creates a Map with the following color patterns template:
     * 0 - 1..9, 1 - 10..19, 2 - 20..29, etc., for a list of numbers.
     *
     * @param numList
     * @return
     */
    public static Map<Integer, List<Integer>> generateColorPatternTemplateForNumberList(List<Integer> numList)
    {
        colorTemplate = new HashMap<>();
        int patternIndex;
        for (Integer num : numList)
        {
            patternIndex = num / 10;
            if (colorTemplate.containsKey(patternIndex))
            {
                colorTemplate.get(patternIndex).add(num);
            } else
            {
                colorTemplate.put(patternIndex, new ArrayList<>());
                colorTemplate.get(patternIndex).add(num);
            }
        }
        return colorTemplate;
    }

    /**
     * Returns a string color pattern representation of an int array drawing.
     *
     * @param drawing
     * @return
     */
    public static String getColorPatternAsString(int[] drawing)
    {
        return convertIntegerArrayToString(getColorPatternAsIntArray(drawing));
    }

    /**
     * Returns a string color pattern representation of string drawing.
     *
     * @param drawing
     * @return
     */
    public static String getColorPatternAsString(String drawing)
    {
        String[] drawingArr = drawing.split(",");
        for (int i = 0; i < drawingArr.length; i++)
            drawingArr[i] = (Integer.parseInt(drawingArr[i]) / 10) + "";
        return String.join(",", drawingArr);
    }

    /**
     * Normalizes a int array lotto drawing by dividing each number by 10 in order to get its color pattern id.
     * 0 (1-9), 1 (10-19), 2 (20-29), 3 (30-39), 4 (40-49)
     *
     * @param drawing
     * @return
     */
    public static int[] getColorPatternAsIntArray(int[] drawing)
    {
        int[] clone = drawing.clone();
        for (int i = 0; i < clone.length; i++) clone[i] /= 10;
        Arrays.sort(clone);
        return clone;
    }

    /**
     * Checks if an int array number sequence satisfies a certain color pattern, eg. (0,1,1,2,3,4).
     *
     * @param sequence     The array to be checked
     * @param colorPattern The color pattern
     * @return True - when the int array sequence is equal to the color pattern, False - otherwise
     */
    public static boolean doesNumberSequenceSatisfyColorPattern(int[] sequence, int[] colorPattern)
    {
        return Arrays.equals(getColorPatternAsIntArray(sequence), colorPattern);
    }

    /**
     * Calculates the total number of possible number combinations for a chosen color pattern.
     * See {@link #generateCompleteColorPatternTemplate()} for more info on the color pattern template.
     * Total combinations calculation example for 0,1,1,2,3,4: 9 * 10 * 9 * 10 * 10 * 10
     *
     * @param pattern The color pattern
     * @return The calculated total number of combinations for that color pattern
     */
    public static int calculateTotalNumberCombinationsForColorPattern(int[] pattern)
    {
        int totalComb = 1;
        int[] patternIDs = new int[5]; // 0 - 4
        for (int num : pattern) // color pattern example: 0,1,1,2,3,4
        {
            patternIDs[num]++;
            if (num == 0) totalComb *= patternIDs[num] > 1 ? 9 - (patternIDs[num] - 1) : 9;
            else totalComb *= patternIDs[num] > 1 ? 10 - (patternIDs[num] - 1) : 10;
        }
        return totalComb;
    }

    /**
     * Loops through the loaded lotto ajava.data and returns a list of int arrays with drawings for all years since the provided year filter.
     *
     * @param yearFilter
     * @return The list with the color patterns
     */
    public static List<int[]> getColorPatternsSinceYear(String yearFilter)
    {
        List<int[]> list = new ArrayList<>();
        // NOTE: the map is sorted in ascending order (TreeMap)
        for (Map.Entry<String, List<int[]>> entry : TxtFileManager.getInstance().getMappedData().entrySet())
        {
            if (entry.getKey().compareTo(yearFilter) < 0) continue; // skip entry if it doesn't satisfy the year filter
            list.addAll(entry.getValue());
        }
        return list;
    }

    /**
     * Checks if a set of numbers will satisfy the provided color pattern when used for generating number sequences.
     *
     * @param numSet
     * @param pattern
     * @return True - when it does satisfy it, False - otherwise
     */
    public static boolean doesNumberSetSatisfyColorPattern(List<Integer> numSet, String pattern)
    {
        int[] patternTemp = new int[5];
        int[] zeroArr = patternTemp.clone();
        String[] patternArr = pattern.split(",");
        for (String str : patternArr)
            patternTemp[Integer.parseInt(str)]++;
        int patternIndex;
        for (Integer num : numSet)
        {
            patternIndex = num / 10;
            if (patternTemp[patternIndex] - 1 < 0) continue;
            else patternTemp[patternIndex]--;
        }
        return Arrays.equals(patternTemp, zeroArr);
    }

    // ==========================================================================================
    // High/Low Pattern Methods
    // ==========================================================================================

    /**
     * Represents the middle border indicating lower and higher numbers, used to determine if a lotto number should be classified as a low or high.
     * Can also be 24, but with 25 we achieve the correct probability % from the LottoMetrix website.
     */
    public static final int HIGH_LOW_MIDPOINT = 25; // 24

    /**
     * Returns a string high/low number pattern representation of an int arr drawing.
     *
     * @param drawing
     * @return eg. "hhhlll"
     */
    public static String getHighLowPattern(int[] drawing)
    {
        StringBuilder pattern = new StringBuilder();
        for (int number : drawing) pattern.append(number <= HIGH_LOW_MIDPOINT ? "l" : "h");
        return MySort.sortString(pattern.toString());
    }

    /**
     * Returns a string high/low number pattern representation of string drawing.
     *
     * @param drawing
     * @return eg. "hhhlll"
     */
    public static String getHighLowPattern(String drawing)
    {
        StringBuilder pattern = new StringBuilder();
        String[] drawingArr = drawing.split(",");
        for (int i = 0; i < drawingArr.length; i++)
            pattern.append(Integer.parseInt(drawingArr[i]) <= HIGH_LOW_MIDPOINT ? "l" : "h");
        return MySort.sortString(pattern.toString());
    }

    /**
     * Checks if an int array's number sequence has the same high/low pattern as the provided pattern.
     *
     * @param sequence
     * @return True - if the array has the same pattern, False - otherwise
     */
    public static boolean doesNumberSequenceSatisfyHighLowPattern(int[] sequence, String highLowPattern)
    {
        return getHighLowPattern(sequence).equals(highLowPattern);
    }

    /**
     * Checks if a set of numbers will satisfy the provided high/low pattern when used for generating number sequences.
     *
     * @param numSet
     * @param pattern
     * @return True - when it does satisfy it, False - otherwise
     */
    public static boolean doesNumberSetSatisfyHighLowPattern(List<Integer> numSet, String pattern)
    {
        int hCnt = pattern.length() - pattern.replace("h", "").length();
        int lCnt = pattern.length() - pattern.replace("l", "").length();
        for (Integer num : numSet)
        {
            if (num <= HIGH_LOW_MIDPOINT) lCnt--;
            else hCnt--;
            if (lCnt <= 0 && hCnt <= 0) return true;
        }
        return false;
    }

    // ==========================================================================================
    // Odd/Even Pattern Methods
    // ==========================================================================================

    /**
     * Returns a string odd/even number pattern representation of an in array drawing.
     *
     * @param drawing
     * @return eg. "eeeooo"
     */
    public static String getOddEvenPattern(int[] drawing)
    {
        StringBuilder pattern = new StringBuilder();
        for (int number : drawing) pattern.append(((number & 1) == 0) ? "e" : "o");
        return MySort.sortString(pattern.toString());
    }

    /**
     * Returns a string odd/even number pattern representation of string drawing.
     *
     * @param drawing
     * @return eg. "eeeooo"
     */
    public static String getOddEvenPattern(String drawing)
    {
        StringBuilder pattern = new StringBuilder();
        String[] drawingArr = drawing.split(",");
        for (int i = 0; i < drawingArr.length; i++)
            pattern.append(((Integer.parseInt(drawingArr[i]) & 1) == 0) ? "e" : "o");
        return MySort.sortString(pattern.toString());
    }

    /**
     * Checks if a array satisfies a provided odd even rule - a combination of odd and even numbers.
     *
     * @param sequence
     * @return
     */
    public static boolean doesNumberSequenceSatisfyOddEvenPattern(int[] sequence, String oddEvenPattern)
    {
        return getOddEvenPattern(sequence).equals(oddEvenPattern);
    }

    /**
     * Checks if a set of numbers will satisfy the provided odd/even pattern when used for generating number sequences.
     *
     * @param numSet
     * @param pattern
     * @return True - when it does satisfy it, False - otherwise
     */
    public static boolean doesNumberSetSatisfyOddEvenPattern(List<Integer> numSet, String pattern)
    {
        int eCnt = pattern.length() - pattern.replace("e", "").length();
        int oCnt = pattern.length() - pattern.replace("o", "").length();
        for (Integer num : numSet)
        {
            if ((num & 1) == 0) eCnt--;
            else oCnt--;
            if (oCnt <= 0 && eCnt <= 0) return true;
        }
        return false;
    }

    // ==========================================================================================
    // Convert Methods
    // ==========================================================================================

    /**
     * Converts a string array to string sequence of numbers using comma as a separator.
     * ["1","2","3","4","5","6"] >> "1,2,3,4,5,6"
     *
     * @param array The string array to be converted
     * @return The converted array as string
     */
    public static String convertStringArrayToString(String[] array)
    {
        return String.join(",", array);
    }

    /**
     * Converts a string sequence of numbers separated by comma to an int array.
     *
     * @param str
     * @return
     */
    public static int[] convertStringToIntegerArray(String str)
    {
        return convertStringArrayToIntegerArray(str.split(","));
    }

    /**
     * Converts a string array to an int array.
     * ["1","2","3","4","5","6"] >> [1,2,3,4,5,6]
     *
     * @param array The string array to be converted
     * @return The converted string array as int array
     */
    public static int[] convertStringArrayToIntegerArray(String[] array)
    {
        //return Arrays.asList(array).stream().mapToInt(Integer::parseInt).toArray();
        return Arrays.stream(array).mapToInt(Integer::parseInt).toArray();
    }

    /**
     * Converts an int array to a string sequence of numbers separated by comma.
     *
     * @param array The int array to be converted to string
     * @return The converted int array as string
     */
    public static String convertIntegerArrayToString(int[] array)
    {
        StringBuilder strArr = new StringBuilder();
        for (int i = 0; i < array.length; i++)
            strArr.append(i < array.length - 1 ? array[i] + "," : array[i]);
        return strArr.toString();
    }

    // ==========================================================================================
    // Contains Methods
    // ==========================================================================================

    /**
     * Returns true if the provided array contains the provided number.
     *
     * @param arr    The array to be checked
     * @param number The number to be checked
     * @return True - when the array contains the number, False - when the array doesn't contain the number
     */
    public static boolean doesIntegerArrayContains(int[] arr, int number)
    {
        return Arrays.stream(arr).anyMatch(i -> i == number);
    }

    // ==========================================================================================
    // Other Methods
    // ==========================================================================================

    /**
     * Compares two int arrays to find out how many of the numbers of the one array are contained in the second array.
     * Returns 0 the array have different lengths or there are no similar numbers in both of them.
     * Can also return 1-6 depending on how many of the numbers are the same.
     *
     * @param arr1
     * @param arr2
     * @return 0 - 6
     */
    public static int getSimilarityIndexForIntegerArrays(int[] arr1, int[] arr2)
    {
        if (arr1.length != arr2.length) return 0;
        if (Arrays.equals(arr1, arr2)) return 6;
        int cnt = 0;
        for (int num1 : arr1)
            for (int num2 : arr2)
                if (num1 == num2)
                {
                    cnt++;
                    break;
                }
        return cnt;
    }

}
