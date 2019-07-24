package utils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A IO (read/write) class that gets, lists, reads from and writes to txt and json files.
 */
public final class IO
{
    /**
     * Saves data to a specific path and as a specific format.
     *
     * @param pathAndName The path, filename and extension of the file
     * @param content     The contents of the file
     */
    public static void saveFile(String pathAndName, String content)
    {
        try
        {
            FileWriter file = new FileWriter(pathAndName); // path and name
            file.write(content); // content
            file.flush();
            file.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
