package advent.year2017.day12

import java.io.File

class PipeGraph(lines: Sequence<String>) : Graph<Int>(lines.map { parseNode(it) }) {

    companion object {
        val REGEX = "(\\d+) <-> (.*)".toRegex()

        fun parseNode(text: String): Node<Int> {
            val match = REGEX.matchEntire(text) ?: throw IllegalArgumentException("Unrecognized node format $text")

            return Node(match.groupValues[1].toInt(),
                    match.groupValues[2].trim().split(", ").map { it.toInt() }.toSet())
        }
    }

    constructor(text: String) : this(text
            .split("\n")
            .asSequence())
}

fun main() {
    val graph = File("src/main/kotlin/advent/year2017/day12/input.txt")
            .useLines { PipeGraph(it) }

    println(graph.connectedSubgraph(0).size)
    println(graph.connectedSubgraphs().size)
}