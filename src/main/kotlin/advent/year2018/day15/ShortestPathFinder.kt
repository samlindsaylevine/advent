package advent.year2018.day15

class ShortestPathFinder {

    /**
     * Finds all the shortest paths from a starting point to an ending point.
     *
     * We assume that the paths are stateless except for what is captured in the "point" type - i.e., there is no reason
     * to ever revisit a point since a shorter path would then exist.
     *
     * We do a breadth-first search and eliminate any path that returns to a point that we have already visited (since
     * it cannot then be one of the shortest paths).
     *
     * @param start The starting point of the path.
     * @param end The target ending point.
     * @param nextSteps Given a point, what are the next points that can be taken in a path?
     * @param collapseKey A speed up optimization choice: we can optionally collapse some paths and only
     * consider the first one, if our problem statement allows it.
     *
     * @return All the shortest paths (i.e., of equal length) from start to end, or empty set if there are none.
     */
    fun <T, K> find(start: T,
                    end: T,
                    nextSteps: (T) -> Set<T>,
                    collapseKey: (List<T>) -> K): Set<Path<T>> {
        return find(end,
                setOf(PathInProgress(emptyList(), start)),
                setOf(start),
                nextSteps,
                collapseKey)
    }

    /**
     * Find with no collapsing of paths.
     */
    fun <T> find(start: T,
                 end: T,
                 nextSteps: (T) -> Set<T>) =
            find(start, end, nextSteps, { it })

    private tailrec fun <T, K> find(end: T,
                                    currentPaths: Set<PathInProgress<T>>,
                                    visited: Set<T>,
                                    nextSteps: (T) -> Set<T>,
                                    collapseKey: (List<T>) -> K): Set<Path<T>> {
        val successfulPaths = currentPaths.filter { it.steps.isNotEmpty() && it.steps.last() == end }

        return when {
            successfulPaths.isNotEmpty() -> successfulPaths.map { Path(it.steps) }.toSet()
            currentPaths.isEmpty() -> emptySet()
            else -> {
                val nextPaths = currentPaths.flatMap { it.next(nextSteps, visited) }
                        .groupBy { collapseKey(it.steps) }
                        .values
                        .map { it.first() }
                        .toSet()
                val nextVisited = visited + nextPaths.map { it.currentPoint }

                find(end, nextPaths, nextVisited, nextSteps, collapseKey)
            }
        }
    }
}


/**
 * @param steps The steps taken in the path. The starting point will not be contained. The final element will be the
 * ending point.
 */
data class Path<T>(val steps: List<T>)

private data class PathInProgress<T>(val steps: List<T>,
                                     val currentPoint: T) {
    fun next(nextSteps: (T) -> Set<T>,
             visited: Set<T>): Set<PathInProgress<T>> = nextSteps(currentPoint)
            .asSequence()
            .filter { !visited.contains(it) }
            .map { this.withStep(it) }
            .toSet()

    private fun withStep(nextPoint: T) = PathInProgress(this.steps + nextPoint, nextPoint)
}