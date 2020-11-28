package advent.year2019.day24

import advent.utils.Point
import kotlin.math.pow

class Eris(val initialState: ErisState) {
  fun firstToAppearTwice(): ErisState {
    val observed = mutableSetOf<ErisState>()
    var current = initialState
    while (!observed.contains(current)) {
      observed.add(current)
      current = current.next()
    }
    return current
  }
}

data class ErisState(val bugs: Set<Point>) {
  companion object {
    fun parse(input: String): ErisState {
      val lines = input.lines()

      val bugs = allPoints.filter { lines[it.y][it.x] == '#' }

      return ErisState(bugs.toSet())
    }

    private val allPoints = (0..4).flatMap { y ->
      (0..4).map { x -> Point(x, y) }
    }
  }

  fun next(): ErisState {
    val newBugs = allPoints.filter {
      (bugs.contains(it) && it.neighborBugCount() == 1) ||
              (!bugs.contains(it) && (it.neighborBugCount() == 1 || it.neighborBugCount() == 2))
    }
    return ErisState(newBugs.toSet())
  }

  private fun Point.neighborBugCount() = this.adjacentNeighbors.count { bugs.contains(it) }

  fun biodiversity() = allPoints.mapIndexed { i, point -> if (bugs.contains(point)) 2.0.pow(i).toLong() else 0 }.sum()
}

fun main() {
  val input = """
    #####
    .....
    ....#
    #####
    .###.
  """.trimIndent()

  val initialState = ErisState.parse(input)

  println(Eris(initialState).firstToAppearTwice().biodiversity())
}