package advent.year2017.day12

/**
 * @param T The type of names of nodes in the graph.
 */
open class Graph<T>(nodes: Sequence<Node<T>>) {

    private val nodesByName = nodes.map { it.name to it }.toMap()

    data class Node<T>(val name: T, val edges: Set<T>)

    /**
     * Given a node name, returns the names of all nodes in that node's connected subgraph, i.e.,
     * all nodes reachable from the provided node.
     */
    fun connectedSubgraph(name: T): Set<T> {
        val node = nodesByName[name] ?: return emptySet()

        return findSubgraph(node.edges, setOf(name))
    }

    private tailrec fun findSubgraph(nextEdges: Set<T>, visitedNodes: Set<T>): Set<T> {
        if (nextEdges.isEmpty()) {
            return visitedNodes
        }

        val nextNodeName = nextEdges.first()
        val nextNode = nodesByName[nextNodeName] ?: throw IllegalStateException("Missing node $nextNodeName")

        return findSubgraph(nextEdges - nextNodeName + (nextNode.edges - visitedNodes),
                visitedNodes + nextNodeName)
    }

    /**
     * Returns all connected subgraphs.
     */
    fun connectedSubgraphs(): Set<Set<T>> {
        return findSubgraphs(nodesByName.keys, setOf())
    }

    private tailrec fun findSubgraphs(uncheckedNodes: Set<T>, foundSubgraphs: Set<Set<T>>): Set<Set<T>> {
        val firstNode = uncheckedNodes.firstOrNull() ?: return foundSubgraphs
        val subgraph = connectedSubgraph(firstNode)

        return findSubgraphs(uncheckedNodes - subgraph, foundSubgraphs.plusElement(subgraph))
    }

}