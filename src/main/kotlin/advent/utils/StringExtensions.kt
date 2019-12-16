package advent.utils

fun String.digits() = this.split("").filter { it.isNotEmpty() }.map { it.toInt() }