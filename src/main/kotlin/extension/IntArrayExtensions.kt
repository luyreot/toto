package extension

fun IntArray.clear() {
    for (i in indices) {
        set(i, 0)
    }
}

fun IntArray.clearAfter(index: Int) {
    for (i in indices) {
        if (i < index) continue
        set(i, 0)
    }
}