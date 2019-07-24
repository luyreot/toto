package utils;

import java.util.*;

/**
 * A final class that includes static method for sorting various collections.
 */
public final class MySort
{
    /**
     * Return a sorted Map by value, in descending order.
     *
     * @param unsortedMap
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V extends Comparable<V>> Map<K, V> sortMapByValue(Map<K, V> unsortedMap)
    {
        List<Map.Entry<K, V>> list = new LinkedList<>(unsortedMap.entrySet());
        list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        Map<K, V> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) sortedMap.put(entry.getKey(), entry.getValue());
        return sortedMap;
    }

    /**
     * Returns a sorted String variable in ascending order.
     *
     * @param unsortedString
     * @return
     */
    public static String sortString(String unsortedString)
    {
        char[] charArr = unsortedString.toCharArray();
        Arrays.sort(charArr);
        return new String(charArr);
    }

}
