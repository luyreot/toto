package logicNew.extensions

fun <K, V : Comparable<V>> MutableMap<K, V>.sortByValue() {
    val sortedMap = toList().sortedBy { (_, value) -> value }.toMap()
    clear()
    putAll(sortedMap)
}

fun <K, V : Comparable<V>> MutableMap<K, V>.sortByValueDescending() {
    val sortedMap = toList().sortedByDescending { (_, value) -> value }.toMap()
    clear()
    putAll(sortedMap)
}