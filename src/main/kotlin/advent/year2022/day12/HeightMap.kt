package advent.year2022.day12

import advent.utils.*
import java.io.File

/**
 * --- Day 12: Hill Climbing Algorithm ---
 * You try contacting the Elves using your handheld device, but the river you're following must be too low to get a
 * decent signal.
 * You ask the device for a heightmap of the surrounding area (your puzzle input). The heightmap shows the local area
 * from above broken into a grid; the elevation of each square of the grid is given by a single lowercase letter, where
 * a is the lowest elevation, b is the next-lowest, and so on up to the highest elevation, z.
 * Also included on the heightmap are marks for your current position (S) and the location that should get the best
 * signal (E). Your current position (S) has elevation a, and the location that should get the best signal (E) has
 * elevation z.
 * You'd like to reach E, but to save energy, you should do it in as few steps as possible. During each step, you can
 * move exactly one square up, down, left, or right. To avoid needing to get out your climbing gear, the elevation of
 * the destination square can be at most one higher than the elevation of your current square; that is, if your current
 * elevation is m, you could step to elevation n, but not to elevation o. (This also means that the elevation of the
 * destination square can be much lower than the elevation of your current square.)
 * For example:
 * Sabqponm
 * abcryxxl
 * accszExk
 * acctuvwj
 * abdefghi
 *
 * Here, you start in the top-left corner; your goal is near the middle. You could start by moving down or right, but
 * eventually you'll need to head toward the e at the bottom. From there, you can spiral around to the goal:
 * v..v<<<<
 * >v.vv<<^
 * .>vv>E^^
 * ..v>>>^^
 * ..>>>>>^
 *
 * In the above diagram, the symbols indicate whether the path exits each square moving up (^), down (v), left (<), or
 * right (>). The location that should get the best signal is still E, and . marks unvisited squares.
 * This path reaches the goal in 31 steps, the fewest possible.
 * What is the fewest steps required to move from your current position to the location that should get the best signal?
 *
 * --- Part Two ---
 * As you walk up the hill, you suspect that the Elves will want to turn this into a hiking trail. The beginning isn't
 * very scenic, though; perhaps you can find a better starting point.
 * To maximize exercise while hiking, the trail should start as low as possible: elevation a. The goal is still the
 * square marked E. However, the trail should still be direct, taking the fewest steps to reach its goal. So, you'll
 * need to find the shortest path from any square at elevation a to the square marked E.
 * Again consider the example from above:
 * Sabqponm
 * abcryxxl
 * accszExk
 * acctuvwj
 * abdefghi
 *
 * Now, there are six choices for starting position (five marked a, plus the square marked S that counts as being at
 * elevation a). If you start at the bottom-left square, you can reach the goal most quickly:
 * ...v<<<<
 * ...vv<<^
 * ...v>E^^
 * .>v>>>^^
 * >^>>>>>^
 *
 * This path reaches the goal in only 29 steps, the fewest possible.
 * What is the fewest steps required to move starting from any square with elevation a to the location that should get
 * the best signal?
 *
 */
class HeightMap(val chars: Map<Point, Char>) {
  constructor(input: String) : this(input.lines().flatMapIndexed { y, line ->
    line.mapIndexed { x, c -> Point(x, y) to c }
  }.toMap())

  private val start = chars.entries.first { it.value == 'S' }.key
  private val end = chars.entries.first { it.value == 'E' }.key

  private fun height(char: Char): Int = when (char) {
    'S' -> height('a')
    'E' -> height('z')
    else -> char - 'a'
  }

  private fun legalMoves(from: Point): Set<Point> {
    val height = height(chars[from] ?: throw IllegalArgumentException("$from not in map"))

    return from.adjacentNeighbors
      .filter { neighbor ->
        val neighborChar = chars[neighbor]
        neighborChar != null && height(neighborChar) <= height + 1
      }.toSet()
  }

  fun fewestSteps(from: Point = start) = ShortestPathFinder().find(
    start = from,
    end = EndState(end),
    nextSteps = Steps(this::legalMoves),
    collapse = CollapseOnCurrentState()
  ).firstOrNull()?.steps?.size

  // This is inefficient - we're recalculating a lot of paths. A more efficient way would be to do a single path-find
  // starting from "nowhere", with the first legal steps being to any 'a', and then calculating legal steps normally
  // after that. But, this was faster to write and runs quickly enough (a few seconds).
  fun fewestStepsFromAnyA(): Int = this.chars.entries
    .asSequence()
    .filter { it.value == 'a' }
    .map { it.key }
    .mapNotNull { fewestSteps(from = it) }
    .min()
}

fun main() {
  val input = File("src/main/kotlin/advent/year2022/day12/input.txt").readText().trim()

  val map = HeightMap(input)
  println(map.fewestSteps())
  println(map.fewestStepsFromAnyA())
}
