package advent.year2023.day23

import advent.utils.Point
import java.io.File

class HikingTrail(val points: Map<Point, Char>) {
  constructor(input: String) : this(input.trim()
          .lines()
          .flatMapIndexed { y, line ->
            line.mapIndexed { x, c -> Point(x, y) to c }
          }
          .toMap()
  )

  fun toGraph(): HikingGraph {
    val edges = points.entries.flatMap { (point, char) ->
      val neighbors: Set<Point> = when (char) {
        '#' -> emptySet()
        '.' -> point.adjacentNeighbors
        '>' -> setOf(point + Point(1, 0))
        '<' -> setOf(point + Point(-1, 0))
        // Up is negative Y.
        '^' -> setOf(point + Point(0, -1))
        'v' -> setOf(point + Point(0, 1))
        else -> throw IllegalStateException("Unrecognized character $char at $point")
      }
      val openNeighbors = neighbors.filter {
        val neighbor = points[it]
        neighbor != null && neighbor != '#'
      }
      openNeighbors.map { to -> HikingEdge(from = point, to = to, distance = 1) }
    }.toSet()
    val openPoints = points.entries
            .filter { it.value == '.' }
            .map { it.key }
    val start = openPoints.minBy { it.y }
    val end = openPoints.maxBy { it.y }
    return HikingGraph(start, end, edges)
  }
}

data class HikingGraph(
        val start: Point,
        val end: Point,
        val edges: Set<HikingEdge>) {

  private val edgesByTo = edges.groupBy { it.to }
  private val edgesByFrom = edges.groupBy { it.from }
  private val points = edgesByTo.keys + edgesByFrom.keys

  // My experience suggests to me that it will be a good idea, when traversing the graph,
  // to compact the edges - i.e., if we have a bunch of nodes in a row that only link to each
  // other, turn them into a single edge with no nodes.
  fun compacted(): HikingGraph {
    val newEdges = edges.toMutableSet()
    points.forEach { point ->
      val incomingEdges = newEdges.filter { it.to == point }.toSet()
      val outgoingEdges = newEdges.filter { it.from == point }.toSet()
      // We can eliminate a point if it is not the start or end, and has at most two neighbors (of any type).
      val toNeighbors = outgoingEdges.map { it.to }.toSet()
      val fromNeighbors = incomingEdges.map { it.from }.toSet()
      val eliminate = (point != start && point != end && (toNeighbors + fromNeighbors).size <= 2)
      if (eliminate) {
        // Eliminate all of the previous both kinds of edges.
        newEdges.removeAll(incomingEdges)
        newEdges.removeAll(outgoingEdges)
        // Replace each previous incoming edge with a new edge corresponding to each of the previous outgoing ones.
        val replacementEdges = incomingEdges.flatMap { incomingEdge ->
          outgoingEdges
                  .filter { it.to != incomingEdge.from }
                  .map { outgoingEdge ->
                    HikingEdge(
                            from = incomingEdge.from,
                            to = outgoingEdge.to,
                            distance = incomingEdge.distance + outgoingEdge.distance)
                  }
        }
        newEdges.addAll(replacementEdges)
      }
    }

    return HikingGraph(start, end, newEdges)
  }

  fun longestHike(): Hike {
    val fromStart = edgesByFrom[start] ?: emptySet()
    if (fromStart.size != 1) throw IllegalStateException("There should be one path from the start")
    return longestRestOfHike(Hike(listOf(fromStart.first()))) ?: throw IllegalStateException("No path to exit!")
  }

  // Returns the longest hike that reaches the exit from the given start.
  private fun longestRestOfHike(soFar: Hike): Hike? {
    val current = soFar.currentPosition
    if (current == end) return soFar
    val nextEdges = edgesByFrom[current] ?: emptySet()
    val notYetVisited = nextEdges.filter { it.to !in soFar }
    return notYetVisited.map { soFar + it }
            .mapNotNull { longestRestOfHike(it) }
            .maxByOrNull { it.distance }
  }
}

// A directed edge.
data class HikingEdge(val from: Point, val to: Point, val distance: Int)

data class Hike(val steps: List<HikingEdge>) {
  val visited by lazy { steps.map { it.to }.toSet() }
  val distance by lazy { steps.sumOf { it.distance } }
  val points by lazy { steps.map { it.to } }
  val currentPosition by lazy { points.last() }
  operator fun plus(edge: HikingEdge) = Hike(steps + edge)
  operator fun contains(point: Point) = point in points
}

fun main() {
  val input = File("src/main/kotlin/advent/year2023/day23/input.txt").readText().trim()
  val trail = HikingTrail(input)
  val graph = trail.toGraph().compacted()

  println(graph.longestHike().distance)
}