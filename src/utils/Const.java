package utils;

public final class Const
{
    public static final String CURRENT_YEAR = "2019";
    public static final String PATH_TXT = "_txt/";
    public static final String PATH_JSON = "_json/";
    private static final String PAGE_URL = "http://www.toto.bg/results/6x49/";

    public static String getCurrentYearTxtFilePath()
    {
        return PATH_TXT.concat(CURRENT_YEAR);
    }

    public static String getCurrentYearPageUrl()
    {
        return PAGE_URL.concat(CURRENT_YEAR).concat("-");
    }
}
