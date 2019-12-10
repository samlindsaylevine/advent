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
                PathsInProgress(start),
                setOf(start),
                nextSteps.withCostOne(),
                0,
                collapseKey)
    }

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
     */
    fun <T> findWithCosts(start: T,
                          end: T,
                          nextSteps: (T) -> Set<Step<T>>): Set<Path<T>> {
        return find(end,
                PathsInProgress(start),
                setOf(start),
                nextSteps,
                0,
                { it })
    }

    private tailrec fun <T, K> find(end: T,
                                    inProgress: PathsInProgress<T>,
                                    visited: Set<T>,
                                    nextSteps: (T) -> Set<Step<T>>,
                                    costIncurred: Int,
                                    collapseKey: (List<T>) -> K): Set<Path<T>> {
        val currentPaths = inProgress.current()
        val successfulPaths = currentPaths.filter { it.steps.isNotEmpty() && it.steps.last() == end }

        return when {
            successfulPaths.isNotEmpty() -> successfulPaths.map { Path(it.steps, costIncurred) }.toSet()
            inProgress.isEmpty() -> emptySet()
            else -> {
                val nextPaths: Set<PathAndCost<T>> = currentPaths.flatMap { it.next(nextSteps, visited) }
                        .groupBy { collapseKey(it.path.steps) }
                        .values
                        .map { it.first() }
                        .toSet()

                val nextInProgress: PathsInProgress<T> = (inProgress + nextPaths).advanced()

                val nextVisited: Set<T> = visited + nextInProgress.current().map { it.currentPoint }

                find(end, nextInProgress, nextVisited, nextSteps, costIncurred + 1, collapseKey)
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
 * The key is how much additional cost it would be to proceed onto one of those paths.
 */
private class PathsInProgress<T>(private val nextPathsByCost: Map<Int, Set<PathInProgress<T>>>) {
    constructor(start: T) : this(mapOf(0 to setOf(PathInProgress(emptyList(), start))))

    fun current() = nextPathsByCost[0] ?: emptySet()

    fun advanced(): PathsInProgress<T> = PathsInProgress(nextPathsByCost.filter { it.key > 0 }
            .mapKeys { it.key - 1 })

    fun isEmpty() = nextPathsByCost.isEmpty()

    operator fun plus(paths: Set<PathAndCost<T>>): PathsInProgress<T> {
        val nextMap = nextPathsByCost.toMutableMap()
        paths.forEach { (path, cost) -> nextMap.merge(cost, setOf(path)) { existing, new -> existing + new } }
        return PathsInProgress(nextMap)
    }
}