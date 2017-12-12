package advent.year2017.day12

import java.io.File

class PipeGraph(lines: Sequence<String>) {

    private val nodesByName: Map<Int, Node> = lines
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .map { Node.fromText(it) }
            .map { it.name to it }
            .toMap()

    constructor(text: String) : this(text
            .split("\n")
            .asSequence())

    /**
     * Given a node name, returns the names of all nodes in that node's connected subgraph, i.e.,
     * all nodes reachable from the provided node.
     */
    fun connectedSubgraph(name: Int): Set<Int> {
        val node = nodesByName[name] ?: return emptySet()

        return findSubgraph(node.edges, setOf(name))
    }

    /**
     * Returns all connected subgraphs.
     */
    fun connectedSubgraphs(): Set<Set<Int>> {
        return nodesByName.values
                .mapTo(mutableSetOf()) { connectedSubgraph(it.name) }
    }

    private tailrec fun findSubgraph(nextEdges: Set<Int>, visitedNodes: Set<Int>): Set<Int> {
        if (nextEdges.isEmpty()) {
            return visitedNodes
        }

        val nextNodeName = nextEdges.first()
        val nextNode = nodesByName[nextNodeName] ?: throw IllegalStateException("Missing node $nextNodeName")



        return findSubgraph(nextEdges - nextNodeName + (nextNode.edges - visitedNodes),
                visitedNodes + nextNodeName)
    }

    private class Node(val name: Int, val edges: Set<Int>) {

        companion object {
            val REGEX = "(\\d+) <-> (.*)".toRegex()

            fun fromText(text: String): Node {
                val match = REGEX.matchEntire(text) ?: throw IllegalArgumentException("Unrecognized node format $text")

                return Node(match.groupValues[1].toInt(),
                        match.groupValues[2].trim().split(", ").map { it.toInt() }.toSet())
            }
        }
    }
}

fun main(args: Array<String>) {
    val graph = File("src/main/kotlin/advent/year2017/day12/input.txt")
            .useLines { PipeGraph(it) }

    println(graph.connectedSubgraph(0).size)
    println(graph.connectedSubgraphs().size)
}