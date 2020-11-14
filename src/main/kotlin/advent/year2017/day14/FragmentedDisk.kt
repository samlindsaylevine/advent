package advent.year2017.day14

import advent.year2017.day10.KnotHash
import advent.year2017.day10.leftPad
import advent.year2017.day12.Graph
import advent.year2017.day12.Graph.Node
import java.math.BigInteger

class FragmentedDisk(val key: String, val width: Int = 128, val height: Int = 128) {

    companion object {
        fun toBinaryString(hex: String) = BigInteger(hex, 16)
                .toString(2)
                .leftPad('0', hex.length * 4)
    }

    /**
     * True is used, false is unused.
     */
    private val rows: List<List<Boolean>> = (0 until height).map { toBinaryString(KnotHash("$key-$it").hex) }
            .map { it.toCharArray().map { it == '1' } }

    private val positions = (0 until height).flatMap { y ->
        (0 until width).map { x -> Position(x, y) }
    }

    private val valuesByPosition: Map<Position, Boolean> = positions.map { it to (rows[it.y])[it.x] }.toMap()

    private val graph: Graph<Position> by lazy {
        Graph(valuesByPosition.filter { it.value }
                .map { Node(it.key, adjacentUsedPositions(it.key)) }.asSequence())
    }

    private fun adjacentUsedPositions(position: Position): Set<Position> {
        return position.neighbors
                .filter { valuesByPosition[it] ?: false }
                .toSet()
    }

    private data class Position(val x: Int, val y: Int) {
        // No diagonals.
        val neighbors by lazy {
            setOf(Position(x - 1, y),
                    Position(x, y + 1),
                    Position(x + 1, y),
                    Position(x, y - 1))
        }
    }

    fun usedSquares(): Int = valuesByPosition.values.count { it }

    fun regions() = graph.connectedSubgraphs().size

}

fun main() {
    val disk = FragmentedDisk(key = "xlqgujun")
    println(disk.usedSquares())
    println(disk.regions())
}