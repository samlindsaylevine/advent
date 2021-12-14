package advent.utils

fun <T : Comparable<T>> Iterable<T>.maxOrThrow(): T = this.maxOrNull()
  ?: throw IllegalStateException("Max of an empty Iterable is undefined")

fun <T : Comparable<T>> Iterable<T>.minOrThrow(): T = this.minOrNull()
  ?: throw IllegalStateException("Max of an empty Iterable is undefined")

fun <T, R : Comparable<R>> Iterable<T>.minByOrThrow(selector: (T) -> R): T = this.minByOrNull(selector)
  ?: throw IllegalStateException("Min of an empty Iterable is undefined")

fun <T> Iterable<T>.permutations() = this.toList().permutations()

fun <T> List<T>.permutations(): Sequence<List<T>> = when {
  this.isEmpty() -> sequenceOf(emptyList())
  else -> this.asSequence().flatMap { first ->
    (this - first).permutations().map { rest: List<T> -> listOf(first) + rest }
  }
}

/**
 * Find the "median" of a sortable collection of any type. Since we don't guarantee the ability to do arithmetic, only
 * to sort, this only works with an odd number of elements.
 */
fun <T : Comparable<T>> Collection<T>.median() = when {
  this.size % 2 == 0 -> throw IllegalArgumentException("Median of arbitrary type only works for odd number of elements")
  else -> this.sorted()[(this.size - 1) / 2]
}