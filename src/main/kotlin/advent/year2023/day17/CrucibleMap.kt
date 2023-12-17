package advent.year2023.day17

import advent.utils.*
import java.io.File

/**
 * --- Day 17: Clumsy Crucible ---
 * The lava starts flowing rapidly once the Lava Production Facility is operational. As you leave, the reindeer offers
 * you a parachute, allowing you to quickly reach Gear Island.
 * As you descend, your bird's-eye view of Gear Island reveals why you had trouble finding anyone on your way up: half
 * of Gear Island is empty, but the half below you is a giant factory city!
 * You land near the gradually-filling pool of lava at the base of your new lavafall. Lavaducts will eventually carry
 * the lava throughout the city, but to make use of it immediately, Elves are loading it into large crucibles on wheels.
 * The crucibles are top-heavy and pushed by hand. Unfortunately, the crucibles become very difficult to steer at high
 * speeds, and so it can be hard to go in a straight line for very long.
 * To get Desert Island the machine parts it needs as soon as possible, you'll need to find the best way to get the
 * crucible from the lava pool to the machine parts factory. To do this, you need to minimize heat loss while choosing
 * a route that doesn't require the crucible to go in a straight line for too long.
 * Fortunately, the Elves here have a map (your puzzle input) that uses traffic patterns, ambient temperature, and
 * hundreds of other parameters to calculate exactly how much heat loss can be expected for a crucible entering any
 * particular city block.
 * For example:
 * 2413432311323
 * 3215453535623
 * 3255245654254
 * 3446585845452
 * 4546657867536
 * 1438598798454
 * 4457876987766
 * 3637877979653
 * 4654967986887
 * 4564679986453
 * 1224686865563
 * 2546548887735
 * 4322674655533
 *
 * Each city block is marked by a single digit that represents the amount of heat loss if the crucible enters that
 * block. The starting point, the lava pool, is the top-left city block; the destination, the machine parts factory, is
 * the bottom-right city block. (Because you already start in the top-left block, you don't incur that block's heat
 * loss unless you leave that block and then return to it.)
 * Because it is difficult to keep the top-heavy crucible going in a straight line for very long, it can move at most
 * three blocks in a single direction before it must turn 90 degrees left or right. The crucible also can't reverse
 * direction; after entering each city block, it may only turn left, continue straight, or turn right.
 * One way to minimize heat loss is this path:
 * 2>>34^>>>1323
 * 32v>>>35v5623
 * 32552456v>>54
 * 3446585845v52
 * 4546657867v>6
 * 14385987984v4
 * 44578769877v6
 * 36378779796v>
 * 465496798688v
 * 456467998645v
 * 12246868655<v
 * 25465488877v5
 * 43226746555v>
 *
 * This path never moves more than three consecutive blocks in the same direction and incurs a heat loss of only 102.
 * Directing the crucible from the lava pool to the machine parts factory, but not moving more than three consecutive
 * blocks in the same direction, what is the least heat loss it can incur?
 *
 * --- Part Two ---
 * The crucibles of lava simply aren't large enough to provide an adequate supply of lava to the machine parts factory.
 * Instead, the Elves are going to upgrade to ultra crucibles.
 * Ultra crucibles are even more difficult to steer than normal crucibles. Not only do they have trouble going in a
 * straight line, but they also have trouble turning!
 * Once an ultra crucible starts moving in a direction, it needs to move a minimum of four blocks in that direction
 * before it can turn (or even before it can stop at the end). However, it will eventually start to get wobbly: an
 * ultra crucible can move a maximum of ten consecutive blocks without turning.
 * In the above example, an ultra crucible could follow this path to minimize heat loss:
 * 2>>>>>>>>1323
 * 32154535v5623
 * 32552456v4254
 * 34465858v5452
 * 45466578v>>>>
 * 143859879845v
 * 445787698776v
 * 363787797965v
 * 465496798688v
 * 456467998645v
 * 122468686556v
 * 254654888773v
 * 432267465553v
 *
 * In the above example, an ultra crucible would incur the minimum possible heat loss of 94.
 * Here's another example:
 * 111111111111
 * 999999999991
 * 999999999991
 * 999999999991
 * 999999999991
 *
 * Sadly, an ultra crucible would need to take an unfortunate path like this one:
 * 1>>>>>>>1111
 * 9999999v9991
 * 9999999v9991
 * 9999999v9991
 * 9999999v>>>>
 *
 * This route causes the ultra crucible to incur the minimum possible heat loss of 71.
 * Directing the ultra crucible from the lava pool to the machine parts factory, what is the least heat loss it can
 * incur?
 *
 */
class CrucibleMap(val rows: List<List<Int>>) {
  // We reverse the lines so that positive Y is towards the top.
  constructor(input: String) : this(input.trim().lines().map { line -> line.map { it.toString().toInt() } }.reversed())

  val height = rows.size
  val width = rows.maxOf { it.size }

  operator fun contains(point: Point) = (point.y in 0 until height) && (point.x in 0 until width)
  operator fun get(point: Point): Int? = if (this.contains(point)) rows[point.y][point.x] else null

  fun leastHeatLoss(crucibleType: CrucibleType = CrucibleType.normal): Int {
    val startPoint = Point(0, height - 1)
    val endPoint = Point(width - 1, 0)
    val start = CrucibleState(startPoint, Direction.E, 0, crucibleType)

    val path = ShortestPathFinder().find(
            start = start,
            end = EndCondition { it.position == endPoint },
            nextSteps = StepsWithCost(::nextSteps),
            collapse = CollapseOnCurrentState()
    ).first()

    return path.totalCost
  }

  private fun nextSteps(state: CrucibleState): Set<Step<CrucibleState>> {
    val sameDirectionStep: Step<CrucibleState>? = if (state.stepsInSameDirection < state.type.maximumStepsInSameDirection) {
      val newPosition = state.position + state.direction.toPoint()
      val cost = this[newPosition]
      cost?.let { Step(CrucibleState(state.position + state.direction.toPoint(), state.direction, state.stepsInSameDirection + 1, state.type), cost) }
    } else null

    val turns = listOf(state.direction.left(), state.direction.right())
    val turnSteps: List<Step<CrucibleState>> = turns.mapNotNull { newDirection ->
      val newPositions = (1..state.type.minimumStepsInSameDirection).map { distance -> state.position + distance * newDirection.toPoint() }
      val newCosts = newPositions.map(this::get)
      if (newCosts.any { it == null }) {
        null
      } else {
        Step(CrucibleState(newPositions.last(), newDirection, state.type.minimumStepsInSameDirection, state.type), newCosts.filterNotNull().sum())
      }
    }

    return (listOfNotNull(sameDirectionStep) + turnSteps).toSet()
  }
}

data class CrucibleState(val position: Point,
                         val direction: Direction,
                         val stepsInSameDirection: Int,
                         val type: CrucibleType)

class CrucibleType(val minimumStepsInSameDirection: Int, val maximumStepsInSameDirection: Int) {
  companion object {
    val normal = CrucibleType(1, 3)
    val ultra = CrucibleType(4, 10)
  }
}

fun main() {
  val input = File("src/main/kotlin/advent/year2023/day17/input.txt").readText().trim()
  val map = CrucibleMap(input)

  // This is pretty slow! (A couple minutes.) But it works.
  println(map.leastHeatLoss())
  println(map.leastHeatLoss(CrucibleType.ultra))
}