package advent.year2018.day18

import java.io.File

class LumberArea private constructor(private val state: Map<Point, Acre>) {

    companion object {
        fun parse(input: String) = LumberArea(
                input.lines().mapIndexed { y, line -> Pair(y, line) }
                        .flatMap { pair -> parseLine(pair.second, pair.first) }
                        .toMap()
        )

        private fun parseLine(line: String, y: Int) = line.split("").filter { it.isNotEmpty() }
                .mapIndexed { x, char -> Point(x, y) to acre(char) }

        private fun acre(input: String) = Acre.values().first { it.display == input }
    }

    private enum class Acre(val display: String) {
        OPEN_GROUND("."),
        TREES("|"),
        LUMBERYARD("#")
    }

    private operator fun get(point: Point) = state[point] ?: Acre.OPEN_GROUND

    fun resourceValue() = state.count { it.value == Acre.TREES } * state.count { it.value == Acre.LUMBERYARD }

    private fun advanced() = LumberArea(state.mapValues { (point, _) -> nextState(point) })

    private fun nextState(point: Point): Acre {
        val current = this[point]
        val neighborStates = point.neighbors().map { this[it] }
        return when (current) {
            Acre.OPEN_GROUND -> if (neighborStates.count { it == Acre.TREES } >= 3) Acre.TREES else Acre.OPEN_GROUND
            Acre.TREES -> if (neighborStates.count { it == Acre.LUMBERYARD } >= 3) Acre.LUMBERYARD else Acre.TREES
            Acre.LUMBERYARD -> if (neighborStates.count { it == Acre.LUMBERYARD } >= 1 &&
                    neighborStates.count { it == Acre.TREES } >= 1) Acre.LUMBERYARD else Acre.OPEN_GROUND
        }
    }

    fun advanced(times: Int) = advance(times = times, start = this) { it.advanced() }

    override fun toString(): String {
        val minX = state.keys.map { it.x }.minOrNull() ?: 0
        val maxX = state.keys.map { it.x }.maxOrNull() ?: 0
        val minY = state.keys.map { it.y }.minOrNull() ?: 0
        val maxY = state.keys.map { it.y }.maxOrNull() ?: 0

        return (minY..maxY).joinToString("\n") { y ->
            (minX..maxX).joinToString("") { x -> this[Point(x, y)].display }
        }
    }

    // IntelliJ generated.
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LumberArea

        if (state != other.state) return false

        return true
    }

    // IntelliJ generated.
    override fun hashCode(): Int {
        return state.hashCode()
    }
}

private data class Point(val x: Int, val y: Int) {
    fun neighbors() = setOf(Point(x - 1, y - 1),
            Point(x, y - 1),
            Point(x + 1, y - 1),
            Point(x + 1, y),
            Point(x + 1, y + 1),
            Point(x, y + 1),
            Point(x - 1, y + 1),
            Point(x - 1, y))
}

fun main() {
    val input = File("src/main/kotlin/advent/year2018/day18/input.txt")
            .readText()

    val area = LumberArea.parse(input)

    println(area.advanced(10).resourceValue())
    println(area.advanced(1_000_000_000).resourceValue())
}