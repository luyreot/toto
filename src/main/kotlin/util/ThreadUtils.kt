package util

import kotlin.concurrent.thread

object ThreadUtils {

    fun launchThread(method: () -> Unit) = thread(true) {
        method.invoke()
    }
}