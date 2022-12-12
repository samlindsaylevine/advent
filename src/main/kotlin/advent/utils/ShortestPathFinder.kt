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
   * @param end The target [EndOptions].
   * @param nextSteps Given a point, what are the next [StepOptions] that can be taken in a path?
   * @param filter A speed up optimization: [FilterOptions] can control the discarding of paths that we know are
   *               irrelevant.
   * @param collapse A speed up optimization choice: [CollapseOptions] can optionally collapse some paths and only
   * consider the first one, if our problem statement allows it. For example, if we only care about finding *one* path,
   * we will often want to collapse based on the last() element of the path.
   * @param reportEvery If non-null, prints a debug statement every this number of steps.
   *
   * @return All the shortest paths (i.e., of equal length) from start to end, or empty set if there are none.
   */
  fun <T> find(
    start: T,
    end: EndOptions<T>,
    nextSteps: StepOptions<T>,
    filter: FilterOptions<T> = NoFilter(),
    collapse: CollapseOptions<T, *> = NoCollapse(),
    reportEvery: Int? = null
  ): Set<Path<T>> =
    find(
      end::matches,
      PathsInProgress(start),
      setOf(start),
      nextSteps::next,
      filter::discard,
      collapse::collapseKey,
      reportEvery,
      0
    )

  private tailrec fun <T, K> find(
    endCondition: (T) -> Boolean,
    inProgress: PathsInProgress<T>,
    visited: Set<T>,
    nextSteps: (T) -> Set<Step<T>>,
    filterOut: (List<T>) -> Boolean,
    collapseKey: (List<T>) -> K,
    reportEvery: Int?,
    costIncurred: Int
  ): Set<Path<T>> {

    val currentPaths = inProgress.current()
    val successfulPaths = currentPaths.filter { it.steps.isNotEmpty() && endCondition(it.steps.last()) }

    return when {
      successfulPaths.isNotEmpty() -> successfulPaths.map { Path(it.steps, costIncurred) }.toSet()
      inProgress.isEmpty() -> emptySet()
      else -> {
        val nextPaths: Set<PathAndCost<T>> = currentPaths.flatMap { it.next(nextSteps, visited) }
          .filter { !filterOut(it.path.steps) }
          .toSet()

        if (reportEvery != null && (costIncurred % reportEvery == 0)) {
          println("At step $costIncurred, considering ${nextPaths.size} options")
        }

        val nextInProgress: PathsInProgress<T> = (inProgress.plus(nextPaths, collapseKey)).advanced()

        val nextVisited: Set<T> = visited + nextInProgress.current().map { it.currentPoint }

        find(
          endCondition,
          nextInProgress,
          nextVisited,
          nextSteps, filterOut,
          collapseKey,
          reportEvery,
          costIncurred + 1
        )
      }
    }
  }
}

/**
 * A definition of when the search is completed successfully.
 */
interface EndOptions<T> {
  fun matches(state: T): Boolean
}

/**
 * The search is completed successfully when it reaches this exact state.
 */
data class EndState<T>(val endState: T) : EndOptions<T> {
  override fun matches(state: T) = (state == endState)
}

/**
 * The search is completed successfully when it reaches any state that passes this test.
 */
data class EndCondition<T>(val test: (T) -> Boolean) : EndOptions<T> {
  override fun matches(state: T) = test(state)
}

/**
 * A definition of what steps are available from a state.
 */
interface StepOptions<T> {
  fun next(state: T): Set<Step<T>>
}

/**
 * A problem definition where all steps have the same cost (1).
 */
data class Steps<T>(val nextSteps: (T) -> Set<T>) : StepOptions<T> {
  override fun next(state: T) = nextSteps(state).map { Step(it, 1) }.toSet()
}

/**
 * A problem definition where steps can have different costs and we want to find the *cheapest* path.
 */
data class StepsWithCost<T>(val nextSteps: (T) -> Set<Step<T>>) : StepOptions<T> {
  override fun next(state: T) = nextSteps(state)
}

/**
 * A definition of which paths to "collapse" and keep only one, as a speed up optimization.
 */
interface CollapseOptions<T, K> {
  fun collapseKey(states: List<T>): K
}

/**
 * Do not collapse any paths.
 */
class NoCollapse<T> : CollapseOptions<T, List<T>> {
  override fun collapseKey(states: List<T>) = states
}

/**
 * If multiple paths in progress have the same output from the "collapse" function, keep only one of those, and the one
 * that has the smallest total cost.
 */
data class Collapse<T, K>(val collapse: (List<T>) -> K) : CollapseOptions<T, K> {
  override fun collapseKey(states: List<T>) = collapse(states)
}

class CollapseOnCurrentState<T> : CollapseOptions<T, T> {
  override fun collapseKey(states: List<T>) = states.last()
}

/**
 * A definition of what paths in progress should be filtered out and discarded, as a speed-up optimization.
 */
interface FilterOptions<T> {
  fun discard(path: List<T>): Boolean
}

/**
 * Do not filter out any paths.
 */
class NoFilter<T> : FilterOptions<T> {
  override fun discard(path: List<T>) = false
}

/**
 * Filter out any paths that pass the "discardIf" test.
 */
class Filter<T>(val discardIf: (List<T>) -> Boolean) : FilterOptions<T> {
  override fun discard(path: List<T>) = discardIf(path)
}

data class Step<T>(val next: T, val cost: Int) {
  init {
    require(cost > 0)
  }
}

/**
 * @param steps The steps taken in the path. The starting point will not be contained. The final element will be the
 * ending point.
 */
data class Path<T>(val steps: List<T>, val totalCost: Int)

private data class PathInProgress<T>(
  val steps: List<T>,
  val currentPoint: T
) {
  fun next(
    nextSteps: (T) -> Set<Step<T>>,
    visited: Set<T>
  ): Set<PathAndCost<T>> = nextSteps(currentPoint)
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

  fun <K> plus(
    newPaths: Set<PathAndCost<T>>,
    collapseKey: (List<T>) -> K
  ): PathsInProgress<T> {

    val allNext = nextPathsByCost + newPaths

    val collapsed = allNext.groupBy { if (it.path.steps.isEmpty()) null else collapseKey(it.path.steps) }
      .values
      .mapNotNull { paths -> paths.minByOrNull { it.cost } }

    return PathsInProgress(collapsed.toSet())
  }
}