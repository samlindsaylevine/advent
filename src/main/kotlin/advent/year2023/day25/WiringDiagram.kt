package advent.year2023.day25

import advent.utils.UnorderedPair
import java.io.File

/**
 * --- Day 25: Snowverload ---
 * Still somehow without snow, you go to the last place you haven't checked: the center of Snow Island, directly below
 * the waterfall.
 * Here, someone has clearly been trying to fix the problem. Scattered everywhere are hundreds of weather machines,
 * almanacs, communication modules, hoof prints, machine parts, mirrors, lenses, and so on.
 * Somehow, everything has been wired together into a massive snow-producing apparatus, but nothing seems to be
 * running. You check a tiny screen on one of the communication modules: Error 2023. It doesn't say what Error 2023
 * means, but it does have the phone number for a support line printed on it.
 * "Hi, you've reached Weather Machines And So On, Inc. How can I help you?" You explain the situation.
 * "Error 2023, you say? Why, that's a power overload error, of course! It means you have too many components plugged
 * in. Try unplugging some components and--" You explain that there are hundreds of components here and you're in a bit
 * of a hurry.
 * "Well, let's see how bad it is; do you see a big red reset button somewhere? It should be on its own module. If you
 * push it, it probably won't fix anything, but it'll report how overloaded things are." After a minute or two, you
 * find the reset button; it's so big that it takes two hands just to get enough leverage to push it. Its screen then
 * displays:
 * SYSTEM OVERLOAD!
 *
 * Connected components would require
 * power equal to at least 100 stars!
 *
 * "Wait, how many components did you say are plugged in? With that much equipment, you could produce snow for an
 * entire--" You disconnect the call.
 * You have nowhere near that many stars - you need to find a way to disconnect at least half of the equipment here,
 * but it's already Christmas! You only have time to disconnect three wires.
 * Fortunately, someone left a wiring diagram (your puzzle input) that shows how the components are connected. For
 * example:
 * jqt: rhn xhk nvd
 * rsh: frs pzl lsr
 * xhk: hfx
 * cmg: qnr nvd lhk bvb
 * rhn: xhk bvb hfx
 * bvb: xhk hfx
 * pzl: lsr hfx nvd
 * qnr: nvd
 * ntq: jqt hfx bvb xhk
 * nvd: lhk
 * lsr: lhk
 * rzs: qnr cmg lsr rsh
 * frs: qnr lhk lsr
 *
 * Each line shows the name of a component, a colon, and then a list of other components to which that component is
 * connected. Connections aren't directional; abc: xyz and xyz: abc both represent the same configuration. Each
 * connection between two components is represented only once, so some components might only ever appear on the left or
 * right side of a colon.
 * In this example, if you disconnect the wire between hfx/pzl, the wire between bvb/cmg, and the wire between nvd/jqt,
 * you will divide the components into two separate, disconnected groups:
 *
 * 9 components: cmg, frs, lhk, lsr, nvd, pzl, qnr, rsh, and rzs.
 * 6 components: bvb, hfx, jqt, ntq, rhn, and xhk.
 *
 * Multiplying the sizes of these groups together produces 54.
 * Find the three wires you need to disconnect in order to divide the components into two separate groups. What do you
 * get if you multiply the sizes of these two groups together?
 *
 * --- Part Two ---
 * You climb over weather machines, under giant springs, and narrowly avoid a pile of pipes as you find and disconnect
 * the three wires.
 * A moment after you disconnect the last wire, the big red reset button module makes a small ding noise:
 * System overload resolved!
 * Power required is now 50 stars.
 *
 * Out of the corner of your eye, you notice goggles and a loose-fitting hard hat peeking at you from behind an ultra
 * crucible. You think you see a faint glow, but before you can investigate, you hear another small ding:
 * Power required is now 49 stars.
 *
 * Please supply the necessary stars and
 * push the button to restart the system.
 *
 *
 */
class WiringDiagram(val edges: List<UnorderedPair<String>>) {
  constructor(input: String) : this(input.trim().lines()
          .flatMap { line ->
            val first = line.substringBefore(": ")
            val rest = line.substringAfter(": ")
            rest.split(" ").map { UnorderedPair(first, it) }
          }
  )

  val nodes = edges.flatMap { (first, second) -> listOf(first, second) }.toSet()
  private val edgesByFrom = edges.flatMap { (first, second) -> listOf(first to second, second to first) }
          .groupBy(Pair<String, String>::first, Pair<String, String>::second)

  // Returns the product of the sizes of the two disconnected groups upon removing the correct 3 wires.
  fun disconnectedGroupProduct(): Int {
    // One of the things I remember from graph theory is the good ol' "min cut max flow theorem", so this problem
    // description very quickly looks like a min cut to me. However, this is on an undirected graph, not a flow.
    // I still did know enough to be able to search for "undirected graph min cut". That turned up Karger's algorithm
    // for finding a global minimum cut in an undirected graph. It is a probabilistic algorithm for finding a min cut,
    // by repeatedly contracting a randomly selected edge until the resulting graph has only two vertices.
    // Usually you run it a fixed number of times and have the result be probabilistically correct, but our problem
    // statement gives us the advantage that we know the min cut should have 3 edges. So, we can immediately accept
    // any result that contracts to 3 edges, and always rerun any result that does not.
    while (true) {
      val graph = this.toKargerGraph()
      graph.contractCompletely()
      if (graph.numEdges() == 3) {
        val (first, second) = graph.groupSizes()
        return first * second
      }
    }
  }

  private fun toKargerGraph(): KargerGraph {
    val edges = mutableMapOf<KargerNode, MutableList<KargerNode>>()
    val kargerNodes = nodes.map { KargerNode(listOf(it)) }
    val kargerNodesByName = kargerNodes.associateBy { it.originalNames.first() }
    edgesByFrom.forEach { (nodeName, adjacencies) ->
      val list = mutableListOf<KargerNode>()
      adjacencies.forEach { list.add(kargerNodesByName[it]!!) }
      edges[kargerNodesByName[nodeName]!!] = list
    }
    return KargerGraph(edges)
  }
}

/**
 * A few things are different about this than our original graph: we make it all mutable, because we will be editing
 * it heavily; and, more importantly, we allow multiple edges between a pair of nodes. We still represent the edges
 * in the adjacency form by a from-keyed map. A single edge will appear twice in the map -both a-to-b and b-to-a.
 */
private class KargerGraph(val edges: MutableMap<KargerNode, MutableList<KargerNode>>) {
  fun contractCompletely() {
    while (edges.keys.size > 2) contract()
  }

  // Remember that the edges are double-counted.
  fun numEdges() = edges.values.sumOf { it.size } / 2
  fun groupSizes() = edges.keys.map { it.originalNames.size }

  private fun contract() {
    val (from, to) = randomEdge()
    contractEdge(from, to)
  }

  private fun contractEdge(u: KargerNode, v: KargerNode) {
    val newNode = KargerNode(u.originalNames + v.originalNames)

    val previousAdjacencies = ((edges[u] ?: emptyList()) + (edges[v] ?: emptyList()))
            .filter { it != u && it != v }

    previousAdjacencies.forEach { adjacency ->
      edges[adjacency]?.removeIf { it == u || it == v }
      edges[adjacency]?.add(newNode)
    }

    edges.remove(u)
    edges.remove(v)
    edges[newNode] = previousAdjacencies.toMutableList()
  }

  private fun randomEdge(): Pair<KargerNode, KargerNode> {
    val allEdges: List<Pair<KargerNode, KargerNode>> = edges.flatMap { (key, adjacencies) -> adjacencies.map { key to it } }
    return allEdges.random()
  }
}

private data class KargerNode(val originalNames: List<String>)

fun main() {
  val input = File("src/main/kotlin/advent/year2023/day25/input.txt").readText().trim()
  val diagram = WiringDiagram(input)

  println(diagram.disconnectedGroupProduct())
}