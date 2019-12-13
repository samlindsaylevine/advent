package advent.year2019.day6

import advent.year2018.day15.ShortestPathFinder
import java.io.File

/**
 * @param orbits Map from objects to everything that is orbiting them.
 */
class OrbitMap(private val orbits: Map<String, Set<String>>) {

    companion object {
        private val REGEX = "^(.*)\\)(.*)$".toRegex()

        fun parse(input: String) = OrbitMap(input.lines()
                .map { parseLine(it) }
                .groupBy(keySelector = { it.first }, valueTransform = { it.second })
                .mapValues { it.value.toSet() }
        )

        private fun parseLine(line: String): Pair<String, String> {
            val match = REGEX.matchEntire(line) ?: throw IllegalArgumentException("Unparseable line $line")

            return match.groupValues[1] to match.groupValues[2]
        }
    }

    // Map from objects to what that object is orbiting.
    private val reverseOrbits = orbits.flatMap { entry -> entry.value.map { it to entry.key } }
            .toMap()

    fun totalDirectAndIndirect() = orbits.keys.sumBy { directAndIndiretFrom(it) }

    private fun directAndIndiretFrom(objectName: String): Int {
        val children = orbits[objectName] ?: emptyList<String>()

        return children.size + children.sumBy { directAndIndiretFrom(it) }
    }

    fun transfers(start: String, target: String): Int? {
        val startChild = reverseOrbits[start] ?: throw IllegalStateException("Start is not orbiting anything")
        val targetChild = reverseOrbits[target] ?: throw IllegalStateException("Target is not orbiting anything")

        val finder = ShortestPathFinder()

        val shortestPaths = finder.find(startChild, targetChild, ::adjacent)
        return shortestPaths.map { it.steps.size }.min()
    }

    private fun adjacent(obj: String): Set<String> {
        val upstream = orbits[obj] ?: emptySet()
        val downstream = listOfNotNull(reverseOrbits[obj])

        return upstream + downstream
    }
}

fun main() {
    val input = File("src/main/kotlin/advent/year2019/day6/input.txt")
            .readText()
            .trim()

    val map = OrbitMap.parse(input)

    println(map.totalDirectAndIndirect())
    println(map.transfers("YOU", "SAN"))
}