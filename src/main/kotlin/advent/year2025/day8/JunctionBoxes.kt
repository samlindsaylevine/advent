package advent.year2025.day8

import advent.meta.readInput
import advent.utils.Point3D
import advent.utils.UnorderedPair
import advent.utils.allUnorderedPairs
import advent.year2017.day12.Graph

/**
 * --- Day 8: Playground ---
 * Equipped with a new understanding of teleporter maintenance, you confidently step onto the repaired teleporter pad.
 * You rematerialize on an unfamiliar teleporter pad and find yourself in a vast underground space which contains a
 * giant playground!
 * Across the playground, a group of Elves are working on setting up an ambitious Christmas decoration project. Through
 * careful rigging, they have suspended a large number of small electrical junction boxes.
 * Their plan is to connect the junction boxes with long strings of lights. Most of the junction boxes don't provide
 * electricity; however, when two junction boxes are connected by a string of lights, electricity can pass between
 * those two junction boxes.
 * The Elves are trying to figure out which junction boxes to connect so that electricity can reach every junction box.
 * They even have a list of all of the junction boxes' positions in 3D space (your puzzle input).
 * For example:
 * 162,817,812
 * 57,618,57
 * 906,360,560
 * 592,479,940
 * 352,342,300
 * 466,668,158
 * 542,29,236
 * 431,825,988
 * 739,650,466
 * 52,470,668
 * 216,146,977
 * 819,987,18
 * 117,168,530
 * 805,96,715
 * 346,949,466
 * 970,615,88
 * 941,993,340
 * 862,61,35
 * 984,92,344
 * 425,690,689
 *
 * This list describes the position of 20 junction boxes, one per line. Each position is given as X,Y,Z coordinates.
 * So, the first junction box in the list is at X=162, Y=817, Z=812.
 * To save on string lights, the Elves would like to focus on connecting pairs of junction boxes that are as close
 * together as possible according to straight-line distance. In this example, the two junction boxes which are closest
 * together are 162,817,812 and 425,690,689.
 * By connecting these two junction boxes together, because electricity can flow between them, they become part of the
 * same circuit. After connecting them, there is a single circuit which contains two junction boxes, and the remaining
 * 18 junction boxes remain in their own individual circuits.
 * Now, the two junction boxes which are closest together but aren't already directly connected are 162,817,812 and
 * 431,825,988. After connecting them, since 162,817,812 is already connected to another junction box, there is now a
 * single circuit which contains three junction boxes and an additional 17 circuits which contain one junction box each.
 * The next two junction boxes to connect are 906,360,560 and 805,96,715. After connecting them, there is a circuit
 * containing 3 junction boxes, a circuit containing 2 junction boxes, and 15 circuits which contain one junction box
 * each.
 * The next two junction boxes are 431,825,988 and 425,690,689. Because these two junction boxes were already in the
 * same circuit, nothing happens!
 * This process continues for a while, and the Elves are concerned that they don't have enough extension cables for all
 * these circuits. They would like to know how big the circuits will be.
 * After making the ten shortest connections, there are 11 circuits: one circuit which contains 5 junction boxes, one
 * circuit which contains 4 junction boxes, two circuits which contain 2 junction boxes each, and seven circuits which
 * each contain a single junction box. Multiplying together the sizes of the three largest circuits (5, 4, and one of
 * the circuits of size 2) produces 40.
 * Your list contains many junction boxes; connect together the 1000 pairs of junction boxes which are closest
 * together. Afterward, what do you get if you multiply together the sizes of the three largest circuits?
 *
 * --- Part Two ---
 * The Elves were right; they definitely don't have enough extension cables. You'll need to keep connecting junction
 * boxes together until they're all in one large circuit.
 * Continuing the above example, the first connection which causes all of the junction boxes to form a single circuit
 * is between the junction boxes at 216,146,977 and 117,168,530. The Elves need to know how far those junction boxes
 * are from the wall so they can pick the right extension cable; multiplying the X coordinates of those two junction
 * boxes (216 and 117) produces 25272.
 * Continue connecting the closest unconnected pairs of junction boxes together until they're all in the same circuit.
 * What do you get if you multiply together the X coordinates of the last two junction boxes you need to connect?
 *
 */
class JunctionBoxes(val boxes: List<Point3D>) {
  constructor(input: String) : this(
    input.trim()
      .lines()
      .map { line ->
        val (x, y, z) = line.split(",")
        Point3D(x.toInt(), y.toInt(), z.toInt())
      })

  /**
   * Represents two boxes and the distances between them.
   *
   * We'll just keep the squared distance to avoid having to calculate a square root (both for speed and precision
   * reasons.)
   */
  data class BoxDistance(val boxes: UnorderedPair<Point3D>, val squaredDistance: Long)

  private fun Point3D.squaredDistanceTo(other: Point3D) = (this.x - other.x).toLong() * (this.x - other.x) +
          (this.y - other.y).toLong() * (this.y - other.y) +
          (this.z - other.z).toLong() * (this.z - other.z)

  /**
   * The distances between each pair of boxes, in ascending distance order.
   */
  val distances: List<BoxDistance> by lazy {
    val pairs = boxes.allUnorderedPairs()
    pairs.map { pair -> BoxDistance(pair, (pair.component1().squaredDistanceTo(pair.component2()))) }
      .sortedBy { it.squaredDistance }
  }

  fun graph(numConnections: Int): Graph<Point3D> {
    val pairEdges = distances.take(numConnections).map { it.boxes }
    fun Point3D.neighbors() = pairEdges.filter { it.component1() == this }.map { it.component2() } +
            pairEdges.filter { it.component2() == this }.map { it.component1() }

    val nodes = boxes.map { Graph.Node(it, it.neighbors().toSet()) }
    return Graph(nodes.asSequence())
  }

  fun circuitSizeProduct(numConnections: Int) = graph(numConnections)
    .connectedSubgraphs()
    .map { it.size }
    .sortedDescending()
    .take(3)
    .reduce(Int::times)

  fun lastTwoConnected(): UnorderedPair<Point3D> {
    // This is inefficient but going to try it anyways. Just start making graphs with each number of connections,
    // starting at the number of nodes, and see at which one the first node is in a subgraph with everything else.
    // As it turns out, this takes a few minutes to run, but then succeeds. Ah well, CPU time is cheaper than
    // programmer time!
    val edgeCounts = generateSequence(boxes.size) { it + 1 }
    val minEdgeCount: Int = edgeCounts.first { numConnections ->
      val graph = graph(numConnections)
      val subgraph = graph.connectedSubgraph(boxes.first())
      if (numConnections % 100 == 0) println(numConnections)
      subgraph.size == boxes.size
    }
    return distances[minEdgeCount - 1].boxes
  }

  fun lastConnectedXProduct() = lastTwoConnected().map { it.x.toLong() }.reduce(Long::times)
}

fun main() {
  val boxes = JunctionBoxes(readInput())

  println(boxes.graph(numConnections = 1000).connectedSubgraphs().map { it.size }.sortedDescending())
  println(boxes.circuitSizeProduct(numConnections = 1000))
  println(boxes.lastConnectedXProduct())
}