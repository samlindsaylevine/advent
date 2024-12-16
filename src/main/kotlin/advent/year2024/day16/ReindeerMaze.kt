package advent.year2024.day16

import advent.meta.readInput
import advent.utils.*
import kotlin.Pair

class ReindeerMaze(val start: Point, val end: Point, val walls: Set<Point>) {
    companion object {
        fun of(input: String): ReindeerMaze {
            val lines = input.lines()
            val charsByPoint: List<Pair<Point, Char>> = lines.flatMapIndexed { y, line ->
                line.mapIndexed { x, c -> Point(x, lines.size - y) to c }
            }
            val start = charsByPoint.first { it.second == 'S' }.first
            val end = charsByPoint.first { it.second == 'E' }.first
            val walls = charsByPoint.filter { it.second == '#' }.map { it.first }
            return ReindeerMaze(start, end, walls.toSet())
        }
    }

    private fun nextSteps(reindeerState: ReindeerState): Set<Step<ReindeerState>> {
        // Naively calculating steps with one step at a time, and turning options available, is too slow for part two
        // where we keep all the paths around. Instead, we will have each step be an optional turn, then advance forward
        // to the next intersection.
        val direction = reindeerState.direction
        val turnsWithCosts = listOf(direction.left() to 1000, direction to 0, direction.right() to 1000)
        return turnsWithCosts.mapNotNull { (newDirection, turnCost) ->
            val nextPossibleTurnPoint = nextTurnPoint(reindeerState.position, newDirection)
            nextPossibleTurnPoint?.let { newPosition ->
                val tilesMoved = newPosition.distanceFrom(reindeerState.position)
                Step(ReindeerState(newPosition, newDirection), turnCost + tilesMoved)
            }
        }.toSet()
    }

    /**
     * Finds the next point to consider when travelling in the same direction from the given point.
     *
     * We will stop and consider a point when either
     * 1) that point's forward space is a wall, so we must turn
     * 2) one of the right or left spaces is not a wall, so we may turn
     *
     * If we start out facing a dead end, there will be no such thing at all and we will return null.
     */
    private fun nextTurnPoint(start: Point, direction: Direction): Point? =
        generateSequence(start) { it + direction }
            .takeWhile { it !in walls }
            .drop(1)
            .firstOrNull { point ->
                point + direction in walls || point + direction.left() !in walls || point + direction.right() !in walls
            }

    fun shortestPaths(): Set<Path<ReindeerState>> {
        val startingState = ReindeerState(start, Direction.E)

        val paths = ShortestPathFinder().find(
            start = startingState,
            end = EndCondition { it.position == end },
            nextSteps = StepsWithCost(this::nextSteps),
            reportEvery = 10_000
        )

        return paths
    }

    fun countUniqueTiles(paths: Set<Path<ReindeerState>>) =
        paths.flatMap { it.uniqueTiles() }.toSet().size

    private fun Path<ReindeerState>.uniqueTiles() =
        this.steps
            .map { it.position }
            // Paths don't contain their starting position.
            .let { listOf(start) + it }
            .zipWithNext()
            .flatMap { (first, second) -> first..second }
            .toSet()

    data class ReindeerState(val position: Point, val direction: Direction)
}

fun Set<Path<ReindeerMaze.ReindeerState>>.lowestScore() = this.first().totalCost

fun main() {
    val maze = ReindeerMaze.of(readInput())

    // This is still pretty slow (like 5 minutes!) Oh well, some day instead of reusing my breadth-first search
    // for these purposes, I will actually use Dijkstra's algorithm with the enhancement that preserves arrows
    // on the graph and then can reconsistute the shortest path itself.
    val paths = maze.shortestPaths()
    println(paths.lowestScore())
    println(maze.countUniqueTiles(paths))
}