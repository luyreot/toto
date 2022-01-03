package logicNew.extensions

fun IntArray.clear() {
    for (i in 0 until size) {
        set(i, 0)
    }
}