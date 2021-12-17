package advent.year2021.day17

import advent.utils.Point
import java.io.File
import kotlin.math.absoluteValue

/**
 * --- Day 17: Trick Shot ---
 * You finally decode the Elves' message. HI, the message says. You continue searching for the sleigh keys.
 * Ahead of you is what appears to be a large ocean trench. Could the keys have fallen into it? You'd better send a
 * probe to investigate.
 * The probe launcher on your submarine can fire the probe with any integer velocity in the x (forward) and y (upward,
 * or downward if negative) directions. For example, an initial x,y velocity like 0,10 would fire the probe straight
 * up, while an initial velocity like 10,-1 would fire the probe forward at a slight downward angle.
 * The probe's x,y position starts at 0,0. Then, it will follow some trajectory by moving in steps. On each step, these
 * changes occur in the following order:
 *
 * The probe's x position increases by its x velocity.
 * The probe's y position increases by its y velocity.
 * Due to drag, the probe's x velocity changes by 1 toward the value 0; that is, it decreases by 1 if it is greater
 * than 0, increases by 1 if it is less than 0, or does not change if it is already 0.
 * Due to gravity, the probe's y velocity decreases by 1.
 *
 * For the probe to successfully make it into the trench, the probe must be on some trajectory that causes it to be
 * within a target area after any step. The submarine computer has already calculated this target area (your puzzle
 * input). For example:
 * target area: x=20..30, y=-10..-5
 * This target area means that you need to find initial x,y velocity values such that after any step, the probe's x
 * position is at least 20 and at most 30, and the probe's y position is at least -10 and at most -5.
 * Given this target area, one initial velocity that causes the probe to be within the target area after any step is
 * 7,2:
 * .............#....#............
 * .......#..............#........
 * ...............................
 * S........................#.....
 * ...............................
 * ...............................
 * ...........................#...
 * ...............................
 * ....................TTTTTTTTTTT
 * ....................TTTTTTTTTTT
 * ....................TTTTTTTT#TT
 * ....................TTTTTTTTTTT
 * ....................TTTTTTTTTTT
 * ....................TTTTTTTTTTT
 *
 * In this diagram, S is the probe's initial position, 0,0. The x coordinate increases to the right, and the y
 * coordinate increases upward. In the bottom right, positions that are within the target area are shown as T. After
 * each step (until the target area is reached), the position of the probe is marked with #. (The bottom-right # is
 * both a position the probe reaches and a position in the target area.)
 * Another initial velocity that causes the probe to be within the target area after any step is 6,3:
 * ...............#..#............
 * ...........#........#..........
 * ...............................
 * ......#..............#.........
 * ...............................
 * ...............................
 * S....................#.........
 * ...............................
 * ...............................
 * ...............................
 * .....................#.........
 * ....................TTTTTTTTTTT
 * ....................TTTTTTTTTTT
 * ....................TTTTTTTTTTT
 * ....................TTTTTTTTTTT
 * ....................T#TTTTTTTTT
 * ....................TTTTTTTTTTT
 *
 * Another one is 9,0:
 * S........#.....................
 * .................#.............
 * ...............................
 * ........................#......
 * ...............................
 * ....................TTTTTTTTTTT
 * ....................TTTTTTTTTT#
 * ....................TTTTTTTTTTT
 * ....................TTTTTTTTTTT
 * ....................TTTTTTTTTTT
 * ....................TTTTTTTTTTT
 *
 * One initial velocity that doesn't cause the probe to be within the target area after any step is 17,-4:
 * S..............................................................
 * ...............................................................
 * ...............................................................
 * ...............................................................
 * .................#.............................................
 * ....................TTTTTTTTTTT................................
 * ....................TTTTTTTTTTT................................
 * ....................TTTTTTTTTTT................................
 * ....................TTTTTTTTTTT................................
 * ....................TTTTTTTTTTT..#.............................
 * ....................TTTTTTTTTTT................................
 * ...............................................................
 * ...............................................................
 * ...............................................................
 * ...............................................................
 * ................................................#..............
 * ...............................................................
 * ...............................................................
 * ...............................................................
 * ...............................................................
 * ...............................................................
 * ...............................................................
 * ..............................................................#
 *
 * The probe appears to pass through the target area, but is never within it after any step. Instead, it continues down
 * and to the right - only the first few steps are shown.
 * If you're going to fire a highly scientific probe out of a super cool probe launcher, you might as well do it with
 * style. How high can you make the probe go while still reaching the target area?
 * In the above example, using an initial velocity of 6,9 is the best you can do, causing the probe to reach a maximum
 * y position of 45. (Any higher initial y velocity causes the probe to overshoot the target area entirely.)
 * Find the initial velocity that causes the probe to reach the highest y position and still eventually be within the
 * target area after any step. What is the highest y position it reaches on this trajectory?
 *
 * --- Part Two ---
 * Maybe a fancy trick shot isn't the best idea; after all, you only have one probe, so you had better not miss.
 * To get the best idea of what your options are for launching the probe, you need to find every initial velocity that
 * causes the probe to eventually be within the target area after any step.
 * In the above example, there are 112 different initial velocity values that meet these criteria:
 * 23,-10  25,-9   27,-5   29,-6   22,-6   21,-7   9,0     27,-7   24,-5
 * 25,-7   26,-6   25,-5   6,8     11,-2   20,-5   29,-10  6,3     28,-7
 * 8,0     30,-6   29,-8   20,-10  6,7     6,4     6,1     14,-4   21,-6
 * 26,-10  7,-1    7,7     8,-1    21,-9   6,2     20,-7   30,-10  14,-3
 * 20,-8   13,-2   7,3     28,-8   29,-9   15,-3   22,-5   26,-8   25,-8
 * 25,-6   15,-4   9,-2    15,-2   12,-2   28,-9   12,-3   24,-6   23,-7
 * 25,-10  7,8     11,-3   26,-7   7,1     23,-9   6,0     22,-10  27,-6
 * 8,1     22,-8   13,-4   7,6     28,-6   11,-4   12,-4   26,-9   7,4
 * 24,-10  23,-8   30,-8   7,0     9,-1    10,-1   26,-5   22,-9   6,5
 * 7,5     23,-6   28,-10  10,-2   11,-1   20,-9   14,-2   29,-7   13,-3
 * 23,-5   24,-8   27,-9   30,-7   28,-5   21,-10  7,9     6,6     21,-5
 * 27,-10  7,2     30,-9   21,-8   22,-7   24,-9   20,-6   6,9     29,-5
 * 8,-2    27,-8   30,-5   24,-7
 *
 * How many distinct initial velocity values cause the probe to be within the target area after any step?
 *
 */
data class TrenchProbe(val vx0: Int, val vy0: Int) {

  // Let's put our physics/math knowledge to work and use some smarts here to come up with
  // a closed form formula for x(t) and y(t).
  //
  // y(t) will be simpler: vy(t) is vy0 - t, and y(t+1) = y(t) + vy(t) = y(t) + vy0 - t.
  //
  // We can quickly see that this is a quadratic; if we write
  // y(t) = a*t^2 + b*t + c, we can see
  // y(t+1) = a*(t+1)^2 + b*(t+1) + c = a*t^2 + b*t + c + vy0 - t
  // Then
  // (a*t^2 + a*2t + a) + b*t + b + c = a*t^2 + b*t + c + vy0 - t
  //
  // a*2t + a + b - vy0 + t = 0
  //
  // i.e., grouping by powers of t, a = -1/2; and b = vy0 + 1/2.
  //
  // Since y(0) = 0, c = 0, and thus y(t) = -1/2 * t^2 + (vy0 + 1/2) t.
  //
  // Our expression for x(t) is going to be almost identical. Let's assume that vx(0) is positive
  // (since in our problem input, the target area is at positive x).
  //
  // Then, x(t) will have the exact same behavior as y(t), up until the point at which vx(t) = 0, after which
  // it will forever remain 0, and x(t) will be unchanged. That is to say, for 0 <= t <= vx(0),
  // x(t) = -1/2 * t^2 + (vx(0) + 1/2) t;
  // for t > vx(0),
  // x(t) = -1/2 * (vx(0))^2 + (vx0 + 1/2) * (vx(0)) = 1/2 * vx(0)^2 + 1/2 * vx(0)
  //

  fun x(t: Int) = if (t <= vx0) {
    (-t * t + (2 * vx0 + 1) * t) / 2
  } else {
    (-vx0 * vx0 + (2 * vx0 + 1) * vx0) / 2
  }

  fun y(t: Int) = (-t * t + (2 * vy0 + 1) * t) / 2

  fun hitsTrench(target: TrenchTarget): Boolean = generateSequence(0) { it + 1 }
    .map { Point(x(it), y(it)) }
    .takeWhile { it.y >= target.y.first }
    .any { it.x in target.x && it.y in target.y }
}

class TrenchTarget(val x: IntRange, val y: IntRange) {
  constructor(input: String) : this(
    input.substringAfter("=").substringBefore(",").toIntRange(),
    input.substringAfterLast("=").toIntRange()
  )

  fun maxHeightToHit(): Int {
    // Let's assume that, as in the examples and my problem statement, that the x range is
    // all positive and the y range is all negative.
    //
    // We know for certain that the far side of the parabola will again touch y=0. The highest possible vy(0) that will
    // ever pass through the range is where vy(0) = abs(minY) - 1 - that means it will get just one time step where it
    // touches the bottom of the range.

    val probe = TrenchProbe(vx0 = 0, vy0 = y.first().absoluteValue - 1)

    // The probe reaches the top of its arc after vy(0) time units.
    return probe.y(t = probe.vy0)

    // Notice that we're not actually checking X values here. We're just casually assuming that there is some initial vx
    // that results in the probe ending up in the range as its max X. (That is true, both for the example, and for my
    // problem, but wouldn't necessarily always be true.)
  }

  fun probesThatHit(): List<TrenchProbe> {
    val minVx0 = 1
    val maxVx0 = x.last
    val minVy0 = y.first
    val maxVy0 = y.first.absoluteValue - 1

    return (minVx0..maxVx0).flatMap { x ->
      (minVy0..maxVy0).map { y -> TrenchProbe(x, y) }
    }.filter { it.hitsTrench(this) }
  }
}

private fun String.toIntRange(): IntRange {
  val (start, end) = this.split("..")
  return start.toInt()..end.toInt()
}

fun main() {
  val trench = File("src/main/kotlin/advent/year2021/day17/input.txt")
    .readText()
    .trim()
    .let(::TrenchTarget)

  println(trench.maxHeightToHit())
  println(trench.probesThatHit().size)
}