package advent.utils

fun <T : Comparable<T>> Iterable<T>.maxOrThrow(): T = this.maxOrNull()
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