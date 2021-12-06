package advent.utils

/**
 * Merges a map with another. Any entry with a key in one, but not the other, will also be in the resulting output.
 * Any key that is in both maps will also be in the output, and its value will be equal to the [mergeFunction] called
 * upon the original two values.
 */
fun <K, V> Map<K, V>.merge(other: Map<K, V>, mergeFunction: (V, V) -> V): Map<K, V> {
  return (this.asSequence() + other.asSequence())
    .groupingBy { it.key }
    .aggregate { _, acc: V?, elem: Map.Entry<K, V>, _ ->
      if (acc == null) elem.value else mergeFunction(acc, elem.value)
    }
}

fun <K> Map<K, Long>.merge(other: Map<K, Long>) = this.merge(other, Long::plus)