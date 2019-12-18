package advent.year2019.day18

import advent.utils.*
import java.io.File

class KeyedVault(val walls: Set<Point>,
                 val doors: Map<Point, Key>,
                 val keys: Map<Point, Key>,
                 val start: Point) {
    companion object {
        fun parse(input: String): KeyedVault {
            val walls = mutableSetOf<Point>()
            val doors = mutableMapOf<Point, Key>()
            val keys = mutableMapOf<Point, Key>()
            var start: Point? = null

            input.trim().lines().forEachIndexed { y, line ->
                line.forEachIndexed { x, char ->
                    val here = Point(x, y)
                    when {
                        char == '#' -> walls.add(here)
                        char == '.' -> Unit
                        char == '@' -> start = here
                        char.isLowerCase() -> keys[here] = char
                        char.isUpperCase() -> doors[here] = char.toLowerCase()
                    }
                }
            }

            return KeyedVault(walls,
                    doors,
                    keys,
                    start ?: throw IllegalArgumentException("No starting point in \n$input"))
        }
    }

    fun shortestPathLength(): Int {
        val finder = ShortestPathFinder()

        val paths = finder.find(start = VaultExplorationState(start, emptySet()),
                end = EndCondition { it.keysOwned.containsAll(keys.values) },
                nextSteps = Steps(::nextOptions),
                // Since we only care about the length of the shortest path, we can collapse any paths that end up
                // at the same state, regardless of how they got there.
                collapse = Collapse { steps: List<VaultExplorationState> -> steps.last() },
                verbose = true)

        return paths.first().steps.size
    }

    private fun nextOptions(current: VaultExplorationState): Set<VaultExplorationState> =
            current.position.adjacentNeighbors
                    .filter { current.canWalk(it) }
                    .map {
                        val keyAtPoint = keys[it]
                        if (keyAtPoint == null) {
                            VaultExplorationState(it, current.keysOwned)
                        } else {
                            VaultExplorationState(it, current.keysOwned + keyAtPoint)
                        }
                    }
                    .toSet()

    private fun VaultExplorationState.canWalk(target: Point) = !walls.contains(target) &&
            (!doors.containsKey(target) || this.keysOwned.contains(doors[target]))
}

private data class VaultExplorationState(val position: Point,
                                         val keysOwned: Set<Key>)

private typealias Key = Char

fun main() {
    val input = File("src/main/kotlin/advent/year2019/day18/input.txt")
            .readText()

    val vault = KeyedVault.parse(input)

    println(vault.shortestPathLength())
}