package org.hexworks.zircon.platform.extension

actual fun <K, V> Map<K, V>.getOrDefault(key: K, defaultValue: V): V = this[key] ?: defaultValue