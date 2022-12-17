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

/**
 * Attempts to find a "linear recurrence" in the sequence - a repeating set of numbers that are all offset by some delta
 * for each full cycle.
 *
 * This can return false positives; it only looks for two repetitions of the set (i.e., three sets of numbers). If it
 * reaches the end of the sequence, or does not find any repetitions before reaching the maxPeriod, it returns null.
 *
 * A min period can be specified to reduce these false positives.
 */
fun Sequence<Int>.findLinearRecurrence(
  minPeriod: Int = 3,
  maxPeriod: Int = 10_000,
  printEvery: Int? = null
): LinearRecurrence? {
  val encountered = mutableListOf<Int>()

  this.forEachIndexed { index, value ->
    if (printEvery != null && index % printEvery == 0) println("Looking for recurrence at $index")
    if (index / 3 > maxPeriod) return null
    encountered.add(value)
    for (attemptedPeriod in minPeriod..index / 3) {
      val currentWindow = encountered.slice(index - attemptedPeriod + 1..index)
      val previousWindow = encountered.slice(index - 2 * attemptedPeriod + 1..index - attemptedPeriod)
      val twoAgoWindow = encountered.slice(index - 3 * attemptedPeriod + 1..index - 2 * attemptedPeriod)
      val deltas = currentWindow.zip(previousWindow).map { it.first - it.second }
      val previousDeltas = previousWindow.zip(twoAgoWindow).map { it.first - it.second }
      if (deltas.all { it == deltas.first() } && previousDeltas.all { it == deltas.first() }) {
        return LinearRecurrence(index - 3 * attemptedPeriod + 1, attemptedPeriod, deltas.first(), twoAgoWindow)
      }
    }
  }
  return null
}


data class LinearRecurrence(
  val firstIndex: Int,
  val period: Int,
  val deltaPerCycle: Int,
  val individualValues: List<Int>
) {
  /**
   * Extrapolate forward to predict what the value at the given index will be (by counting cycles).
   */
  operator fun get(index: Long): Long = when {
    index < firstIndex -> throw IllegalArgumentException("Can't extrapolate earlier than first repetition")
    else -> {
      val cycles = (index - firstIndex) / period
      individualValues[((index - firstIndex) % period.toLong()).toInt()] + cycles * deltaPerCycle
    }
  }
}