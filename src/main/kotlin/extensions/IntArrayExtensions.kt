package extensions

fun IntArray.clear() {
    for (i in indices) {
        set(i, 0)
    }
}