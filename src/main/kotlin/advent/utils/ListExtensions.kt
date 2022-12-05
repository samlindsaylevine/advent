package advent.utils

fun <T> List<T>.updated(index: Int, newValue: T): List<T> = this.toMutableList().apply { this[index] = newValue }