package advent.utils

/**
 * Takes items from this sequence until one of them does not match the predicate; then include that failing element in
 * the result.
 */
fun <T> Sequence<T>.takeWhileInclusive(predicate: (T) -> Boolean): Sequence<T> = sequence {
  val iterator = iterator()
  while (iterator.hasNext()) {
    val next = iterator.next()
    yield(next)
    if (!predicate(next)) break
  }
}