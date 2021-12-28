package advent.year2021.day25

import advent.utils.Point
import java.io.File

data class SeaCucumbers(
  val width: Int,
  val height: Int,
  val east: Set<Point>,
  val south: Set<Point>
) {
  constructor(input: String) : this(
    input.lines().first().length,
    input.lines().size,
    input.lines().flatMapIndexed { y, line ->
      line.mapIndexedNotNull { x, c -> if (c == '>') Point(x, y) else null }
    }.toSet(),
    input.lines().flatMapIndexed { y, line ->
      line.mapIndexedNotNull { x, c -> if (c == 'v') Point(x, y) else null }
    }.toSet()
  )

  fun next() = this.eastMove().southMove()

  private val occupied by lazy { east + south }

  private fun Point.moveWrapping(delta: Point): Point {
    val moved = this + delta
    return Point(moved.x % width, moved.y % height)
  }

  private fun Point.moveEast() = this.moveWrapping(Point(1, 0))
  private fun Point.moveSouth() = this.moveWrapping(Point(0, 1))

  private fun eastMove(): SeaCucumbers {
    val moving = east.filter { it.moveEast() !in occupied }
    return SeaCucumbers(this.width, this.height, east - moving + moving.map { it.moveEast() }, south)
  }

  private fun southMove(): SeaCucumbers {
    val moving = south.filter { it.moveSouth() !in occupied }
    return SeaCucumbers(this.width, this.height, east, south - moving + moving.map { it.moveSouth() })
  }

  fun timeToStop() = generateSequence(this, SeaCucumbers::next)
    .mapIndexed { i, seaCucumbers -> i to seaCucumbers }
    .zipWithNext()
    .first { (step1, step2) ->
      val (_, cucumbers1) = step1
      val (_, cucumbers2) = step2
      cucumbers1 == cucumbers2
    }
    .second.first
}

fun main() {
  val cucumbers = File("src/main/kotlin/advent/year2021/day25/input.txt")
    .readText()
    .trim()
    .let(::SeaCucumbers)

  println(cucumbers.timeToStop())
}