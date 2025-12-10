package advent.year2025.day9

import advent.meta.readInput
import advent.utils.Point
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class TheaterTiles(val redTiles: List<Point>) {
  constructor(input: String) : this(
    input.trim()
      .lines()
      .map { line ->
        val (x, y) = line.split(",")
        Point(x.toInt(), y.toInt())
      }
  )

  private fun pairs(): Sequence<Pair<Point, Point>> = redTiles.indices.asSequence().flatMap { i ->
    (0 until i).asSequence().map { j -> redTiles[i] to redTiles[j] }
  }

  private fun Pair<Point, Point>.rectangleArea(): Long = (abs(first.x - second.x) + 1).toLong() *
          (abs(first.y - second.y) + 1).toLong()

  private val edges: List<Pair<Point, Point>> = (redTiles.zipWithNext() + (redTiles.last() to redTiles.first()))

  fun isOnEdge(point: Point): Boolean = edges.any { this.isOnEdge(point, it) }
  private fun isOnEdge(point: Point, edge: Pair<Point, Point>): Boolean {
    val (minX, maxX, minY, maxY) = BoundingBox(edge)
    return (point.x == minX && point.x == maxX && point.y in minY..maxY) ||
            (point.y == minY && point.y == maxY && point.x in minX..maxX)
  }

  private data class BoundingBox(val minX: Int, val maxX: Int, val minY: Int, val maxY: Int) {
    constructor(edge: Pair<Point, Point>) : this(
      min(edge.first.x, edge.second.x),
      max(edge.first.x, edge.second.x),
      min(edge.first.y, edge.second.y),
      max(edge.first.y, edge.second.y)
    )
  }

  fun rectanglePerimeterIsInPolygon(corners: Pair<Point, Point>): Boolean {
    val (minX, maxX, minY, maxY) = BoundingBox(corners)
    val segments = listOf(
      Point(minX, minY) to Point(maxX, minY),
      Point(minX, maxY) to Point(maxX, maxY),
      Point(minX, minY) to Point(minX, maxY),
      Point(maxX, minY) to Point(maxX, maxY)
    )
    return segments.all { (start, endInclusive) ->
      segmentIsInPolygon(start, endInclusive)
    }
  }

  private fun segmentIsInPolygon(start: Point, endInclusive: Point): Boolean {
    // We'll use a 'ray tracing' technique to see which points are in the polygon: start walking in from "infinity"
    // (i.e., -1 X or Y) and every time we pass through an edge, we will toggle whether we think we are in the polygon.
    // We're going to make a couple simplifying assumptions: 1) that none of the edges overlap (i.e., that the
    // segments between successive red tiles really does form a single polygon
    // 2) none of the edges are immediately adjacent to each other; this would increase our complexity significantly
    // and appears to be true in our input data, as well as the example.
    val farAway: Point = when {
      start.x == endInclusive.x -> Point(start.x, -1)
      start.y == endInclusive.y -> Point(-1, start.y)
      else -> throw IllegalArgumentException("Somehow start $start is not lined up with end $endInclusive!")
    }
    var inPolygon = false
    var previousPointOnEdge = false
    for (point in (farAway..start)) {
      val currentPointOnEdge = isOnEdge(point)
      if (!currentPointOnEdge && previousPointOnEdge) inPolygon = !inPolygon
      previousPointOnEdge = currentPointOnEdge
    }
    for (point in (start..endInclusive).drop(1)) {
      val currentPointOnEdge = isOnEdge(point)
      if (!currentPointOnEdge && previousPointOnEdge) inPolygon = !inPolygon
      if (!currentPointOnEdge && !inPolygon) return false
      previousPointOnEdge = currentPointOnEdge
    }
    return true
  }

  fun largestRectangleArea() = pairs()
    .map { it.rectangleArea() }
    .max()

  fun largestRedAndGreenRectangleArea() = pairs()
    .withIndex()
    // If all the points on the perimeter are in the polygon, so too must all the ones be that are inside the
    // rectangle.
    .onEach { (i, _) -> if (i % 10 == 0) println(i) }
    .filter { (_, pair) -> rectanglePerimeterIsInPolygon(pair) }
    .map { (_, pair) -> pair.rectangleArea() }
    .max()
}

fun main() {
  val tiles = TheaterTiles(readInput())

  println(tiles.largestRectangleArea())
  println(tiles.largestRedAndGreenRectangleArea())
}