package ajava.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * A final singleton class for random checks over the collected ajava.data. Eg. if a certain combination/drawing exists, etc.
 */
public final class RandomChecks
{
    private static final RandomChecks INSTANCE = new RandomChecks();

    public static RandomChecks getInstance()
    {
        return INSTANCE;
    }

    /**
     * Checks and prints to the console if a certain number combination/drawings exists withing collected ajava.data.
     * Must provide a year filter for which years the check should be done.
     *
     * @param arr
     * @param yearFilter
     */
    public boolean doesDrawingExist(int[] arr, String yearFilter)
    {
        if (arr.length != 6)
        {
            System.out.println("Cannot check this drawing, length is not proper!");
            return false;
        }

        System.out.println("Checking if this drawing exists: " + Arrays.toString(arr));
        int totalCnt = 0;
        for (Map.Entry<String, List<int[]>> entry : TxtFileManager.getInstance().getMappedData().entrySet())
        {
            if (!yearFilter.equals("all") && entry.getKey().compareTo(yearFilter) < 0) continue;
            int yearCnt = 0;
            for (int[] drawing : entry.getValue())
                if (Arrays.equals(drawing, arr))
                    yearCnt++;
            if (yearCnt != 0)
            {
                System.out.println("Year " + entry.getKey() + " has that drawing " + yearCnt + " times.");
                totalCnt += yearCnt;
            }
        }
        return totalCnt > 0;
    }

    /**
     * Prints out how many drawings are there since a provided year as a parameter
     *
     * @param year The year as String
     * @return The total number of drawings since the provided year
     */
    public int countDrawingsForYears(String year)
    {
        System.out.print("Printing how many drawings exists since " + year + ": ");
        int cnt = 0;
        for (Map.Entry<String, List<int[]>> entry : TxtFileManager.getInstance().getMappedData().entrySet())
        {
            if (!year.equals("all") && entry.getKey().compareTo(year) < 0) continue;

            cnt += entry.getValue().size();
        }
        System.out.println(cnt);
        return cnt;
    }

}
