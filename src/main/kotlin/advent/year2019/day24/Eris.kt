package advent.year2019.day24

import advent.utils.Point
import kotlin.math.pow

class SimpleEris(val initialState: SimpleErisState) {
  fun firstToAppearTwice(): SimpleErisState {
    val observed = mutableSetOf<SimpleErisState>()
    var current = initialState
    while (!observed.contains(current)) {
      observed.add(current)
      current = current.next()
    }
    return current
  }
}

data class SimpleErisState(val bugs: Set<Point>) {
  companion object {
    fun parsePoints(input: String): Set<Point> {
      val lines = input.lines()

      val bugs = allPoints.filter { lines[it.y][it.x] == '#' }

      return bugs.toSet()
    }

    fun parse(input: String) = SimpleErisState(parsePoints(input))

    val allPoints = (0..4).flatMap { y ->
      (0..4).map { x -> Point(x, y) }
    }

    val allPointsExceptMiddle = allPoints.filter { !(it.x == 2 && it.y == 2) }.toSet()
  }

  fun next(): SimpleErisState {
    val newBugs = allPoints.filter {
      (bugs.contains(it) && it.neighborBugCount() == 1) ||
              (!bugs.contains(it) && (it.neighborBugCount() == 1 || it.neighborBugCount() == 2))
    }
    return SimpleErisState(newBugs.toSet())
  }

  private fun Point.neighborBugCount() = this.adjacentNeighbors.count { bugs.contains(it) }

  fun biodiversity() = allPoints.mapIndexed { i, point -> if (bugs.contains(point)) 2.0.pow(i).toLong() else 0 }.sum()
}

data class RecursiveErisState(val floors: Map<Int, SimpleErisState>) {

  constructor(floor: SimpleErisState) : this(mapOf(0 to floor))

  fun next(): RecursiveErisState {
    val minFloor = floors.keys.minOrNull() ?: 0
    val maxFloor = floors.keys.maxOrNull() ?: 0

    val newFloors = mutableMapOf<Int, SimpleErisState>()
    floors.forEach { (num, _) -> newFloors[num] = nextFloor(num) }
    nextFloor(minFloor - 1).takeIf { it.bugs.isNotEmpty() }?.let { newFloors[minFloor - 1] = it }
    nextFloor(maxFloor + 1).takeIf { it.bugs.isNotEmpty() }?.let { newFloors[maxFloor + 1] = it }

    return RecursiveErisState(newFloors)
  }

  fun bugCount() = floors.values.sumBy { it.bugs.count() }

  fun advanced(steps: Int) = (1..steps).fold(this) { state, _ -> state.next() }

  private fun nextFloor(floorNum: Int): SimpleErisState {
    val newBugs = SimpleErisState.allPointsExceptMiddle.filter {
      val neighborCount = neighborBugCount(floorNum, it)

      (hasBug(floorNum, it) && neighborCount == 1) ||
              (!hasBug(floorNum, it) && (neighborCount == 1 || neighborCount == 2))
    }

    return SimpleErisState(newBugs.toSet())
  }

  private fun neighbors(floorNum: Int, point: Point): Set<Pair<Int, Point>> {
    val output = mutableSetOf<Pair<Int, Point>>()

    val validNeighbors = point.adjacentNeighbors.intersect(SimpleErisState.allPointsExceptMiddle)
    output.addAll(validNeighbors.map { floorNum to it })

    // Connections upwards.
    if (point.x == 0) {
      output.add(floorNum - 1 to Point(1, 2))
    }
    if (point.y == 0) {
      output.add(floorNum - 1 to Point(2, 1))
    }
    if (point.x == 4) {
      output.add(floorNum - 1 to Point(3, 2))
    }
    if (point.y == 4) {
      output.add(floorNum - 1 to Point(2, 3))
    }

    // Connections downwards.
    if (point == Point(2, 1)) {
      output.add(floorNum + 1 to Point(0, 0))
      output.add(floorNum + 1 to Point(1, 0))
      output.add(floorNum + 1 to Point(2, 0))
      output.add(floorNum + 1 to Point(3, 0))
      output.add(floorNum + 1 to Point(4, 0))
    }
    if (point == Point(1, 2)) {
      output.add(floorNum + 1 to Point(0, 0))
      output.add(floorNum + 1 to Point(0, 1))
      output.add(floorNum + 1 to Point(0, 2))
      output.add(floorNum + 1 to Point(0, 3))
      output.add(floorNum + 1 to Point(0, 4))
    }
    if (point == Point(2, 3)) {
      output.add(floorNum + 1 to Point(0, 4))
      output.add(floorNum + 1 to Point(1, 4))
      output.add(floorNum + 1 to Point(2, 4))
      output.add(floorNum + 1 to Point(3, 4))
      output.add(floorNum + 1 to Point(4, 4))
    }
    if (point == Point(3, 2)) {
      output.add(floorNum + 1 to Point(4, 0))
      output.add(floorNum + 1 to Point(4, 1))
      output.add(floorNum + 1 to Point(4, 2))
      output.add(floorNum + 1 to Point(4, 3))
      output.add(floorNum + 1 to Point(4, 4))
    }

    return output
  }

  private fun neighborBugCount(floorNum: Int, point: Point) = neighbors(floorNum, point)
          .count { hasBug(it.first, it.second) }

  private fun hasBug(floorNum: Int, point: Point) = floors[floorNum]?.bugs?.contains(point) ?: false
}

fun main() {
  val input = """
    #####
    .....
    ....#
    #####
    .###.
  """.trimIndent()

  val initialState = SimpleErisState.parse(input)

  println(SimpleEris(initialState).firstToAppearTwice().biodiversity())

  println(RecursiveErisState(initialState).advanced(200).bugCount())
}