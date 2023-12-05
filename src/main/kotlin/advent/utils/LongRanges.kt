package advent.utils

class RangeComparisonResult(leftInput: List<LongRange>,
                            bothInput: LongRange,
                            rightInput: List<LongRange>) {
    val left: List<LongRange> = leftInput.filter { !it.isEmpty() }
    val both: LongRange = bothInput
    val right: List<LongRange> = rightInput.filter { !it.isEmpty() }

    operator fun component1() = left
    operator fun component2() = both
    operator fun component3() = right
}

infix fun LongRange.compare(other: LongRange): RangeComparisonResult = when {
    // No overlap.
    other.last < this.first || other.first > this.last -> RangeComparisonResult(listOf(this), LongRange.EMPTY, listOf(other))
    // Other completely contains this.
    this.first in other && this.last in other -> RangeComparisonResult(emptyList(),
            this,
            listOf(other.first until this.first, this.last + 1..other.last))
    // This completely contains other.
    other.first in this && other.last in this -> RangeComparisonResult(listOf(this.first until other.first, other.last + 1..this.last),
            other,
            emptyList())
    // Overlap; this starts first.
    this.first !in other && this.last in other -> RangeComparisonResult(listOf(this.first until other.first),
            other.first..this.last,
            listOf(this.last + 1..other.last))
    // Overlap, other starts first.
    this.first in other && this.last !in other -> RangeComparisonResult(listOf(other.last + 1..this.last),
            this.first..other.last,
            listOf(other.first until this.first))

    else -> throw IllegalStateException("Unexpected failure comparing $this to $other")
}