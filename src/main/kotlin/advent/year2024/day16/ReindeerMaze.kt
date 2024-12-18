package advent.year2024.day16

import advent.meta.readInput
import advent.utils.*
import kotlin.Pair

/**
 * --- Day 16: Reindeer Maze ---
 * It's time again for the Reindeer Olympics! This year, the big event is the Reindeer Maze, where the Reindeer compete
 * for the lowest score.
 * You and The Historians arrive to search for the Chief right as the event is about to start. It wouldn't hurt to
 * watch a little, right?
 * The Reindeer start on the Start Tile (marked S) facing East and need to reach the End Tile (marked E). They can move
 * forward one tile at a time (increasing their score by 1 point), but never into a wall (#). They can also rotate
 * clockwise or counterclockwise 90 degrees at a time (increasing their score by 1000 points).
 * To figure out the best place to sit, you start by grabbing a map (your puzzle input) from a nearby kiosk. For
 * example:
 * ###############
 * #.......#....E#
 * #.#.###.#.###.#
 * #.....#.#...#.#
 * #.###.#####.#.#
 * #.#.#.......#.#
 * #.#.#####.###.#
 * #...........#.#
 * ###.#.#####.#.#
 * #...#.....#.#.#
 * #.#.#.###.#.#.#
 * #.....#...#.#.#
 * #.###.#.#.#.#.#
 * #S..#.....#...#
 * ###############
 *
 * There are many paths through this maze, but taking any of the best paths would incur a score of only 7036. This can
 * be achieved by taking a total of 36 steps forward and turning 90 degrees a total of 7 times:
 *
 * ###############
 * #.......#....E#
 * #.#.###.#.###^#
 * #.....#.#...#^#
 * #.###.#####.#^#
 * #.#.#.......#^#
 * #.#.#####.###^#
 * #..>>>>>>>>v#^#
 * ###^#.#####v#^#
 * #>>^#.....#v#^#
 * #^#.#.###.#v#^#
 * #^....#...#v#^#
 * #^###.#.#.#v#^#
 * #S..#.....#>>^#
 * ###############
 *
 * Here's a second example:
 * #################
 * #...#...#...#..E#
 * #.#.#.#.#.#.#.#.#
 * #.#.#.#...#...#.#
 * #.#.#.#.###.#.#.#
 * #...#.#.#.....#.#
 * #.#.#.#.#.#####.#
 * #.#...#.#.#.....#
 * #.#.#####.#.###.#
 * #.#.#.......#...#
 * #.#.###.#####.###
 * #.#.#...#.....#.#
 * #.#.#.#####.###.#
 * #.#.#.........#.#
 * #.#.#.#########.#
 * #S#.............#
 * #################
 *
 * In this maze, the best paths cost 11048 points; following one such path would look like this:
 * #################
 * #...#...#...#..E#
 * #.#.#.#.#.#.#.#^#
 * #.#.#.#...#...#^#
 * #.#.#.#.###.#.#^#
 * #>>v#.#.#.....#^#
 * #^#v#.#.#.#####^#
 * #^#v..#.#.#>>>>^#
 * #^#v#####.#^###.#
 * #^#v#..>>>>^#...#
 * #^#v###^#####.###
 * #^#v#>>^#.....#.#
 * #^#v#^#####.###.#
 * #^#v#^........#.#
 * #^#v#^#########.#
 * #S#>>^..........#
 * #################
 *
 * Note that the path shown above includes one 90 degree turn as the very first move, rotating the Reindeer from facing
 * East to facing North.
 * Analyze your map carefully. What is the lowest score a Reindeer could possibly get?
 *
 * --- Part Two ---
 * Now that you know what the best paths look like, you can figure out the best spot to sit.
 * Every non-wall tile (S, ., or E) is equipped with places to sit along the edges of the tile. While determining which
 * of these tiles would be the best spot to sit depends on a whole bunch of factors (how comfortable the seats are, how
 * far away the bathrooms are, whether there's a pillar blocking your view, etc.), the most important factor is whether
 * the tile is on one of the best paths through the maze. If you sit somewhere else, you'd miss all the action!
 * So, you'll need to determine which tiles are part of any best path through the maze, including the S and E tiles.
 * In the first example, there are 45 tiles (marked O) that are part of at least one of the various best paths through
 * the maze:
 * ###############
 * #.......#....O#
 * #.#.###.#.###O#
 * #.....#.#...#O#
 * #.###.#####.#O#
 * #.#.#.......#O#
 * #.#.#####.###O#
 * #..OOOOOOOOO#O#
 * ###O#O#####O#O#
 * #OOO#O....#O#O#
 * #O#O#O###.#O#O#
 * #OOOOO#...#O#O#
 * #O###.#.#.#O#O#
 * #O..#.....#OOO#
 * ###############
 *
 * In the second example, there are 64 tiles that are part of at least one of the best paths:
 * #################
 * #...#...#...#..O#
 * #.#.#.#.#.#.#.#O#
 * #.#.#.#...#...#O#
 * #.#.#.#.###.#.#O#
 * #OOO#.#.#.....#O#
 * #O#O#.#.#.#####O#
 * #O#O..#.#.#OOOOO#
 * #O#O#####.#O###O#
 * #O#O#..OOOOO#OOO#
 * #O#O###O#####O###
 * #O#O#OOO#..OOO#.#
 * #O#O#O#####O###.#
 * #O#O#OOOOOOO..#.#
 * #O#O#O#########.#
 * #O#OOO..........#
 * #################
 *
 * Analyze your map further. How many tiles are part of at least one of the best paths through the maze?
 *
 */
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