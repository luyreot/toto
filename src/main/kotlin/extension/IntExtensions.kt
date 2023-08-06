package extension

fun Int.greaterOrEqual(
    limit: Int?,
    defaultValue: Boolean
): Boolean = limit?.let { this >= it } ?: defaultValue