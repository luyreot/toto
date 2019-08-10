package ajava.utils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * A final singleton class used for loading the lotto ajava.data from txt files.
 */
public final class TxtFileManager
{
    private static final TxtFileManager INSTANCE = new TxtFileManager();

    public static TxtFileManager getInstance()
    {
        return INSTANCE;
    }

    private TxtFileManager()
    {
        this.mappedData = new TreeMap<>();
        this.listerizedData = new ArrayList<>();
    }

    /**
     * TreeMap for storing a list of drawings (int array) for each year. The year is used as key.
     */
    private Map<String, List<int[]>> mappedData;

    public Map<String, List<int[]>> getMappedData()
    {
        return mappedData;
    }

    private List<String> listerizedData;

    public List<String> getListerizedData()
    {
        return listerizedData;
    }

    /**
     * Loads/Reads all txt files and stores the ajava.data in the tree map. Skips any non-file objects, or system files.
     */
    public void loadData()
    {
        File[] fileList = null;
        try
        {
            fileList = new File(Const.PATH_TXT).listFiles();
        } catch (NullPointerException e)
        {
            e.printStackTrace();
        }
        if (fileList == null)
        {
            System.out.println("ERROR! Couldn't get file list at path: ".concat(Const.PATH_TXT));
            return;
        }
        for (File f : fileList)
        {
            if (!f.isFile() || f.getName().equals(".DS_Store")) continue;
            mappedData.put(f.getName(), convertFileContentsToIntegerList(Const.PATH_TXT.concat(f.getName())));
        }
    }

    /**
     * Reads the contents of the file and returns it as a list of integers where each list item represents a single line of numbers.
     *
     * @param pathAndName The filepath
     * @return The file contents as integer list
     */
    private List<int[]> convertFileContentsToIntegerList(String pathAndName)
    {
        List<int[]> list = new ArrayList<>();
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(pathAndName));
            String line;

            while ((line = br.readLine()) != null)
                list.add(Utils.convertStringArrayToIntegerArray(line.split(",")));

            br.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Converts the loaded ajava.data to a list of string, going from the lowest to highest year. Also skips any years that don't satisfy the year filter.
     */
    public void convertMappedDataToList(String yearFilter)
    {
        // NOTE: the map is sorted in ascending order (TreeMap)
        for (Map.Entry<String, List<int[]>> entry : mappedData.entrySet())
        {
            if (entry.getKey().compareTo(yearFilter) < 0) continue; // skip entry if it doesn't satisfy the year filter
            for (int[] drawing : entry.getValue())
                listerizedData.add(Utils.convertIntegerArrayToString(drawing));
        }
    }

    // ==========================================================================================
    // Saving Drawing ajava.data for specific years, or all years
    // ==========================================================================================

    /**
     * Saves the ajava.data for every year in a JSON formatted file.
     */
    public void saveAllDataAsJSON()
    {
        JSONObject jObj = new JSONObject();
        for (Map.Entry<String, List<int[]>> entry : mappedData.entrySet())
            jObj = addYearDrawingsToJSON(jObj, entry.getKey(), entry.getValue());
        IO.saveFile(Const.PATH_JSON + "drawings_all.json", jObj.toString());
    }

    /**
     * Saves the ajava.data for all years past a certain one in a JSON formatted file.
     *
     * @param year The initial year for which the ajava.data should be saved
     */
    public void saveForYearsAsJSON(String year)
    {
        JSONObject jObj = new JSONObject();
        for (Map.Entry<String, List<int[]>> entry : mappedData.entrySet())
        {
            if (entry.getKey().compareTo(year) < 0) continue;
            jObj = addYearDrawingsToJSON(jObj, entry.getKey(), entry.getValue());
        }
        IO.saveFile(Const.PATH_JSON + "drawings_" + year + "+.json", jObj.toString());
    }

    /**
     * Adds a whole year of drawing to a JSON object and returns that object.
     *
     * @param jObj     The JSON object where the ajava.data will be saved.
     * @param year     The year/key
     * @param drawings The list of int arrays representing that year's drawings
     * @return The updated JSON object
     */
    private JSONObject addYearDrawingsToJSON(JSONObject jObj, String year, List<int[]> drawings)
    {
        JSONObject yearDrawings = new JSONObject();
        for (int i = 0; i < drawings.size(); i++)
        {
            JSONArray jArr = new JSONArray();
            for (int j : drawings.get(i))
                jArr.add(j);
            yearDrawings.put(i + 1, jArr);
        }
        jObj.put(year, yearDrawings);
        return jObj;
    }

}
