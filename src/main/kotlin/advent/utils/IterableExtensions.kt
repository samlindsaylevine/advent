package advent.utils

fun <T : Comparable<T>> Iterable<T>.maxOrThrow(): T = this.maxOrNull()
        ?: throw IllegalStateException("Max of an empty Iterable is undefined")