package advent.year2024.day23

import advent.meta.readInput

/**
 * --- Day 23: LAN Party ---
 * As The Historians wander around a secure area at Easter Bunny HQ, you come across posters for a LAN party scheduled
 * for today! Maybe you can find it; you connect to a nearby datalink port and download a map of the local network
 * (your puzzle input).
 * The network map provides a list of every connection between two computers. For example:
 * kh-tc
 * qp-kh
 * de-cg
 * ka-co
 * yn-aq
 * qp-ub
 * cg-tb
 * vc-aq
 * tb-ka
 * wh-tc
 * yn-cg
 * kh-ub
 * ta-co
 * de-co
 * tc-td
 * tb-wq
 * wh-td
 * ta-ka
 * td-qp
 * aq-cg
 * wq-ub
 * ub-vc
 * de-ta
 * wq-aq
 * wq-vc
 * wh-yn
 * ka-de
 * kh-ta
 * co-tc
 * wh-qp
 * tb-vc
 * td-yn
 *
 * Each line of text in the network map represents a single connection; the line kh-tc represents a connection between
 * the computer named kh and the computer named tc. Connections aren't directional; tc-kh would mean exactly the same
 * thing.
 * LAN parties typically involve multiplayer games, so maybe you can locate it by finding groups of connected
 * computers. Start by looking for sets of three computers where each computer in the set is connected to the other two
 * computers.
 * In this example, there are 12 such sets of three inter-connected computers:
 * aq,cg,yn
 * aq,vc,wq
 * co,de,ka
 * co,de,ta
 * co,ka,ta
 * de,ka,ta
 * kh,qp,ub
 * qp,td,wh
 * tb,vc,wq
 * tc,td,wh
 * td,wh,yn
 * ub,vc,wq
 *
 * If the Chief Historian is here, and he's at the LAN party, it would be best to know that right away. You're pretty
 * sure his computer's name starts with t, so consider only sets of three computers where at least one computer's name
 * starts with t. That narrows the list down to 7 sets of three inter-connected computers:
 * co,de,ta
 * co,ka,ta
 * de,ka,ta
 * qp,td,wh
 * tb,vc,wq
 * tc,td,wh
 * td,wh,yn
 *
 * Find all the sets of three inter-connected computers. How many contain at least one computer with a name that starts
 * with t?
 *
 * --- Part Two ---
 * There are still way too many results to go through them all. You'll have to find the LAN party another way and go
 * there yourself.
 * Since it doesn't seem like any employees are around, you figure they must all be at the LAN party. If that's true,
 * the LAN party will be the largest set of computers that are all connected to each other. That is, for each computer
 * at the LAN party, that computer will have a connection to every other computer at the LAN party.
 * In the above example, the largest set of computers that are all connected to each other is made up of co, de, ka,
 * and ta. Each computer in this set has a connection to every other computer in the set:
 * ka-co
 * ta-co
 * de-co
 * ta-ka
 * de-ta
 * ka-de
 *
 * The LAN party posters say that the password to get into the LAN party is the name of every computer at the LAN
 * party, sorted alphabetically, then joined together with commas. (The people running the LAN party are clearly a
 * bunch of nerds.) In this example, the password would be co,de,ka,ta.
 * What is the password to get into the LAN party?
 *
 */
class NetworkMap(val nodes: Set<String>, val adjacencies: Map<String, Set<String>>) {
    companion object {
        fun of(input: String): NetworkMap {
            val nodes = mutableSetOf<String>()
            val adjacencies = mutableMapOf<String, Set<String>>()

            input.trim().lines().forEach { line ->
                val first = line.substringBefore("-")
                val second = line.substringAfter("-")
                nodes.add(first)
                nodes.add(second)
                adjacencies.merge(first, setOf(second), Set<String>::plus)
                adjacencies.merge(second, setOf(first), Set<String>::plus)
            }

            return NetworkMap(nodes, adjacencies)
        }
    }

    private infix fun String.isAdjacentTo(other: String): Boolean = other in neighbors(this)

    private fun neighbors(node: String) = adjacencies[node] ?: emptySet()

    fun countTTriangles(): Int =
        nodes.trios()
            .count { (first, second, third) ->
                first isAdjacentTo second && second isAdjacentTo third && third isAdjacentTo first &&
                        (first.startsWith("t") || second.startsWith("t") || third.startsWith("t"))
            }

    fun password(): String = largestCompleteSubgraph().toList().sorted().joinToString(",")

    /**
     * I remember enough elementary graph theory to at least know the name of what we are trying to do here! A set of
     * nodes in our graph that are all connected to each other is called a "complete subgraph".
     *
     * Searching for "algorithm largest complete subgraph", I quickly find that this is often called the
     * "clique problem" and there is an algorithm called the "Bron–Kerbosch algorithm" for finding all maximal complete
     * subgraphs, i.e., those subgraphs which are complete and can't add any more nodes while remaining complete.
     *
     * We'll use that Bron-Kerbosch algorith to find the maximal complete subgraphs, then pick the largest one of those
     * to get the largest single complete subgraph.
     */
    private fun largestCompleteSubgraph(): Set<String> = maximalCliques().maxBy { it.size }

    /**
     * This implementation is based on the Wikipedia page on the Bron–Kerbosch algorithm.
     *
     * We are not using the improved version "with pivot", so that could perhaps be an opportunity to run faster.
     * (This runs in a few seconds.)
     */
    private fun maximalCliques(): Set<Set<String>> {
        val output = mutableSetOf<Set<String>>()

        fun bronKerbosch(r: Set<String>, p: Set<String>, x: Set<String>) {
            if (p.isEmpty() && x.isEmpty()) {
                output.add(r)
            }
            var updatedP = p
            var updatedX = x
            p.forEach { v ->
                bronKerbosch(r + v, updatedP intersect neighbors(v), updatedX intersect neighbors(v))
                updatedP = p - v
                updatedX = x + v
            }
        }

        bronKerbosch(r = emptySet(), p = nodes, x = emptySet())

        return output
    }
}

/**
 * Returns all the trios of items in this set, distinct by the items that appear in each trio.
 * (e.g., if "1, 2, 3" is in the output, "2, 1, 3" will not be.)
 */
fun <T> Set<T>.trios(): Sequence<Triple<T, T, T>> {
    val list = this.toList()
    return (0 until list.size - 2).asSequence().flatMap { i ->
        (i + 1 until list.size - 1).asSequence().flatMap { j ->
            (j + 1 until list.size).asSequence().map { k -> Triple(list[i], list[j], list[k]) }
        }
    }
}

fun main() {
    val networkMap = NetworkMap.of(readInput())

    println(networkMap.countTTriangles())
    println(networkMap.password())
}