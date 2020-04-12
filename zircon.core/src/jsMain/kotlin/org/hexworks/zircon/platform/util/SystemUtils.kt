package org.hexworks.zircon.platform.util

import com.soywiz.klock.DateTime

actual object SystemUtils {
    actual fun getCurrentTimeMs() = DateTime.nowUnixLong()

    actual fun getLineSeparator() = "\r\n"
}