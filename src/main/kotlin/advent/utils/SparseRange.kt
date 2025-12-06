package advent.utils

/**
 * Represents a union of IntRanges, with a way to remove a new IntRange and leave just the remaining values.
 */
data class SparseRange(
  // Guaranteed non-overlapping.
  private val longRanges: List<LongRange>
) {
  constructor(intRange: LongRange) : this(listOf(intRange))

  operator fun minus(range: LongRange): SparseRange = SparseRange(longRanges.flatMap { it.remove(range) })

  fun first() = longRanges.minOf { it.first }
  fun size() = longRanges.sumOf { it.size }

  private val LongRange.size
    get() = this.last - this.first + 1

  private fun LongRange.remove(other: LongRange): List<LongRange> = when {
    this.first in other && this.last in other -> emptyList()
    other.first <= this.first && other.last in this -> listOf(other.last + 1..this.last)
    other.first in this && other.last >= this.last -> listOf(this.first until other.first)
    other.first in this && other.last in this -> listOf(
      this.first until other.first,
      other.last + 1..this.last
    )

    else -> listOf(this)
  }
}