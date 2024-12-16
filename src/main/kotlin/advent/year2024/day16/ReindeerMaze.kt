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
        val output = mutableSetOf<Step<ReindeerState>>()

        val forwardPosition = reindeerState.position + reindeerState.direction.toPoint()
        if (forwardPosition !in walls) output.add(Step(reindeerState.copy(position = forwardPosition), 1))

        // Running the search naively with all the options always available is slow, so we're going to do a little
        // optimization - we will never turn to face a wall, because we know that's not an optimal thing to do.

        val forwardFromLeft = reindeerState.position + reindeerState.direction.left()
        val left = Step(reindeerState.copy(direction = reindeerState.direction.left()), 1000)
        if (forwardFromLeft !in walls) output.add(left)

        val forwardFromRight = reindeerState.position + reindeerState.direction.right()
        val right = Step(reindeerState.copy(direction = reindeerState.direction.right()), 1000)
        if (forwardFromRight !in walls) output.add(right)

        return output
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

    data class ReindeerState(val position: Point, val direction: Direction)
}

fun Set<Path<ReindeerMaze.ReindeerState>>.lowestScore() = this.first().totalCost
fun Set<Path<ReindeerMaze.ReindeerState>>.uniqueTileCount() =
    this.flatMap { it.steps.map { step -> step.position } }.toSet().size

fun main() {
    val maze = ReindeerMaze.of(readInput())

    val paths = maze.shortestPaths()
    println(paths.lowestScore())
    println(paths.uniqueTileCount())
}