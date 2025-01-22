package deeplearning.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Util {

    fun getCurrentTime(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val current = LocalDateTime.now().format(formatter)
        return current
    }
}