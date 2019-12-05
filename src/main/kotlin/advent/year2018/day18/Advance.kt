package advent.year2018.day18

/**
 * Given a starting value, and a definition of how to take a step, advance the specified number of steps and return
 * the result.
 *
 * This is optimized to handle the situation where there is a loop: if we encounter a state that we have already
 * reached once, we know we are in a loop and can short-circuit ahead based on the loop period rather than explicitly
 * recalculate all those individual steps.
 *
 * @param T This type must define a meaningful equals/hashCode in order for the optimization to work.
 */
fun <T> advance(times: Int, start: T, step: (T) -> T): T {
    return advance(times, start, step, mapOf(start to 0))
}

private tailrec fun <T> advance(times: Int,
                                current: T,
                                step: (T) -> T,
                                previouslySeenToIndex: Map<T, Int>): T {
    if (times <= 0) return current

    val next = step(current)
    val nextIndex = previouslySeenToIndex.size
    val previouslySeenIndex = previouslySeenToIndex[next]

    return if (previouslySeenIndex == null) {
        advance(times - 1,
                next,
                step,
                previouslySeenToIndex + (next to nextIndex))
    } else {
        val loopSize = nextIndex - previouslySeenIndex
        val stepsFromPrevious = (times - 1) % loopSize
        previouslySeenToIndex.entries.first { it.value == previouslySeenIndex + stepsFromPrevious }.key
    }

}