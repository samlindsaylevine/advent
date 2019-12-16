package advent.utils

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
                    collapseKey: (List<T>) -> K): Set<Path<T>> =
            findWithCosts(start,
                    end,
                    nextSteps.withCostOne(),
                    collapseKey)

    /**
     * Find with no collapsing of paths.
     */
    fun <T> find(start: T,
                 end: T,
                 nextSteps: (T) -> Set<T>) =
            find(start, end, nextSteps, { it })

    /**
     * Find the least-expensive path in the case where steps have a differing _cost_ and we wish to minimize the
     * total cost, not just the number of steps.
     *
     * The shortest path is actually a special case of this where all the steps have a cost of 1.
     *
     * @param collapseKey As in the shortest path, no-cost case; except that we collapse together any paths with the
     *                    same key where one has a smaller total cost than the other (instead of just collapsing them
     *                    when they have the _same_ cost exactly).
     * @param filterOut If provided, any path that returns true from this check is filtered out and discarded. This is
     *                  another opportunity for providing speed-up optimications.
     */
    fun <T, K> findWithCosts(start: T,
                             end: T,
                             nextSteps: (T) -> Set<Step<T>>,
                             collapseKey: (List<T>) -> K,
                             filterOut: (List<T>) -> Boolean = { false }): Set<Path<T>> {
        return find(end,
                PathsInProgress(start),
                setOf(start),
                nextSteps,
                filterOut,
                0,
                collapseKey)
    }

    /**
     * Find with costs but no collapsing of paths.
     */
    fun <T> findWithCosts(start: T,
                          end: T,
                          nextSteps: (T) -> Set<Step<T>>) =
            findWithCosts(start, end, nextSteps, { it })

    private tailrec fun <T, K> find(end: T,
                                    inProgress: PathsInProgress<T>,
                                    visited: Set<T>,
                                    nextSteps: (T) -> Set<Step<T>>,
                                    filterOut: (List<T>) -> Boolean,
                                    costIncurred: Int,
                                    collapseKey: (List<T>) -> K): Set<Path<T>> {

        val currentPaths = inProgress.current()
        val successfulPaths = currentPaths.filter { it.steps.isNotEmpty() && it.steps.last() == end }

        return when {
            successfulPaths.isNotEmpty() -> successfulPaths.map { Path(it.steps, costIncurred) }.toSet()
            inProgress.isEmpty() -> emptySet()
            else -> {
                val nextPaths: Set<PathAndCost<T>> = currentPaths.flatMap { it.next(nextSteps, visited) }
                        .filter { !filterOut(it.path.steps) }
                        .toSet()

                val nextInProgress: PathsInProgress<T> = (inProgress.plus(nextPaths, collapseKey)).advanced()

                val nextVisited: Set<T> = visited + nextInProgress.current().map { it.currentPoint }

                find(end, nextInProgress, nextVisited, nextSteps, filterOut, costIncurred + 1, collapseKey)
            }
        }
    }
}

data class Step<T>(val next: T, val cost: Int) {
    init {
        require(cost > 0)
    }
}

private fun <T> ((T) -> Set<T>).withCostOne(): (T) -> Set<Step<T>> = { t: T ->
    this(t).map { Step(it, 1) }.toSet()
}

/**
 * @param steps The steps taken in the path. The starting point will not be contained. The final element will be the
 * ending point.
 */
data class Path<T>(val steps: List<T>, val totalCost: Int)

private data class PathInProgress<T>(val steps: List<T>,
                                     val currentPoint: T) {
    fun next(nextSteps: (T) -> Set<Step<T>>,
             visited: Set<T>): Set<PathAndCost<T>> = nextSteps(currentPoint)
            .asSequence()
            .filter { !visited.contains(it.next) }
            .map { PathAndCost(this.withStep(it.next), it.cost) }
            .toSet()

    private fun withStep(nextPoint: T) = PathInProgress(this.steps + nextPoint, nextPoint)
}

private data class PathAndCost<T>(val path: PathInProgress<T>, val cost: Int)

/**
 * The cost for each item in the set is how much additional cost it would be to proceed onto one of those paths from
 * the current state.
 */
private class PathsInProgress<T>(private val nextPathsByCost: Set<PathAndCost<T>>) {
    constructor(start: T) : this(setOf(PathAndCost(PathInProgress(emptyList(), start), 0)))

    fun current() = nextPathsByCost.filter { it.cost == 0 }.map { it.path }

    fun advanced(): PathsInProgress<T> = PathsInProgress(nextPathsByCost.filter { it.cost > 0 }
            .map { PathAndCost(it.path, it.cost - 1) }
            .toSet())

    fun isEmpty() = nextPathsByCost.isEmpty()

    fun size() = nextPathsByCost.size

    fun <K> plus(newPaths: Set<PathAndCost<T>>,
                 collapseKey: (List<T>) -> K): PathsInProgress<T> {

        val allNext = nextPathsByCost + newPaths

        val collapsed = allNext.groupBy { if (it.path.steps.isEmpty()) null else collapseKey(it.path.steps) }
                .values
                .mapNotNull { paths -> paths.minBy { it.cost } }

        return PathsInProgress(collapsed.toSet())
    }
}