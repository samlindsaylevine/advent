package advent.year2022.day15

import advent.utils.Point
import java.io.File
import kotlin.math.absoluteValue

/**
 * --- Day 15: Beacon Exclusion Zone ---
 * You feel the ground rumble again as the distress signal leads you to a large network of subterranean tunnels. You
 * don't have time to search them all, but you don't need to: your pack contains a set of deployable sensors that you
 * imagine were originally built to locate lost Elves.
 * The sensors aren't very powerful, but that's okay; your handheld device indicates that you're close enough to the
 * source of the distress signal to use them. You pull the emergency sensor system out of your pack, hit the big button
 * on top, and the sensors zoom off down the tunnels.
 * Once a sensor finds a spot it thinks will give it a good reading, it attaches itself to a hard surface and begins
 * monitoring for the nearest signal source beacon. Sensors and beacons always exist at integer coordinates. Each
 * sensor knows its own position and can determine the position of a beacon precisely; however, sensors can only lock
 * on to the one beacon closest to the sensor as measured by the Manhattan distance. (There is never a tie where two
 * beacons are the same distance to a sensor.)
 * It doesn't take long for the sensors to report back their positions and closest beacons (your puzzle input). For
 * example:
 * Sensor at x=2, y=18: closest beacon is at x=-2, y=15
 * Sensor at x=9, y=16: closest beacon is at x=10, y=16
 * Sensor at x=13, y=2: closest beacon is at x=15, y=3
 * Sensor at x=12, y=14: closest beacon is at x=10, y=16
 * Sensor at x=10, y=20: closest beacon is at x=10, y=16
 * Sensor at x=14, y=17: closest beacon is at x=10, y=16
 * Sensor at x=8, y=7: closest beacon is at x=2, y=10
 * Sensor at x=2, y=0: closest beacon is at x=2, y=10
 * Sensor at x=0, y=11: closest beacon is at x=2, y=10
 * Sensor at x=20, y=14: closest beacon is at x=25, y=17
 * Sensor at x=17, y=20: closest beacon is at x=21, y=22
 * Sensor at x=16, y=7: closest beacon is at x=15, y=3
 * Sensor at x=14, y=3: closest beacon is at x=15, y=3
 * Sensor at x=20, y=1: closest beacon is at x=15, y=3
 *
 * So, consider the sensor at 2,18; the closest beacon to it is at -2,15. For the sensor at 9,16, the closest beacon to
 * it is at 10,16.
 * Drawing sensors as S and beacons as B, the above arrangement of sensors and beacons looks like this:
 *                1    1    2    2
 *      0    5    0    5    0    5
 *  0 ....S.......................
 *  1 ......................S.....
 *  2 ...............S............
 *  3 ................SB..........
 *  4 ............................
 *  5 ............................
 *  6 ............................
 *  7 ..........S.......S.........
 *  8 ............................
 *  9 ............................
 * 10 ....B.......................
 * 11 ..S.........................
 * 12 ............................
 * 13 ............................
 * 14 ..............S.......S.....
 * 15 B...........................
 * 16 ...........SB...............
 * 17 ................S..........B
 * 18 ....S.......................
 * 19 ............................
 * 20 ............S......S........
 * 21 ............................
 * 22 .......................B....
 *
 * This isn't necessarily a comprehensive map of all beacons in the area, though. Because each sensor only identifies
 * its closest beacon, if a sensor detects a beacon, you know there are no other beacons that close or closer to that
 * sensor. There could still be beacons that just happen to not be the closest beacon to any sensor. Consider the
 * sensor at 8,7:
 *                1    1    2    2
 *      0    5    0    5    0    5
 * -2 ..........#.................
 * -1 .........###................
 *  0 ....S...#####...............
 *  1 .......#######........S.....
 *  2 ......#########S............
 *  3 .....###########SB..........
 *  4 ....#############...........
 *  5 ...###############..........
 *  6 ..#################.........
 *  7 .#########S#######S#........
 *  8 ..#################.........
 *  9 ...###############..........
 * 10 ....B############...........
 * 11 ..S..###########............
 * 12 ......#########.............
 * 13 .......#######..............
 * 14 ........#####.S.......S.....
 * 15 B........###................
 * 16 ..........#SB...............
 * 17 ................S..........B
 * 18 ....S.......................
 * 19 ............................
 * 20 ............S......S........
 * 21 ............................
 * 22 .......................B....
 *
 * This sensor's closest beacon is at 2,10, and so you know there are no beacons that close or closer (in any positions
 * marked #).
 * None of the detected beacons seem to be producing the distress signal, so you'll need to work out where the distress
 * beacon is by working out where it isn't. For now, keep things simple by counting the positions where a beacon cannot
 * possibly be along just a single row.
 * So, suppose you have an arrangement of beacons and sensors like in the example above and, just in the row where
 * y=10, you'd like to count the number of positions a beacon cannot possibly exist. The coverage from all sensors near
 * that row looks like this:
 *                  1    1    2    2
 *        0    5    0    5    0    5
 *  9 ...#########################...
 * 10 ..####B######################..
 * 11 .###S#############.###########.
 *
 * In this example, in the row where y=10, there are 26 positions where a beacon cannot be present.
 * Consult the report from the sensors you just deployed. In the row where y=2000000, how many positions cannot contain
 * a beacon?
 *
 * --- Part Two ---
 * Your handheld device indicates that the distress signal is coming from a beacon nearby. The distress beacon is not
 * detected by any sensor, but the distress beacon must have x and y coordinates each no lower than 0 and no larger
 * than 4000000.
 * To isolate the distress beacon's signal, you need to determine its tuning frequency, which can be found by
 * multiplying its x coordinate by 4000000 and then adding its y coordinate.
 * In the example above, the search space is smaller: instead, the x and y coordinates can each be at most 20. With
 * this reduced search area, there is only a single position that could have a beacon: x=14, y=11. The tuning frequency
 * for this distress beacon is 56000011.
 * Find the only possible position for the distress beacon. What is its tuning frequency?
 *
 */
class DeployableSensors(private val sensors: List<DeployableSensor>) {
  constructor(input: String) : this(input.lines().map(DeployableSensor::parse))

  private fun beaconCanExistAt(point: Point) = sensors.all { it.beaconCanExistAt(point) }

  fun cannotContainCount(y: Int): Int {
    val minX = sensors.minOf { it.position.x - it.distance }
    val maxX = sensors.maxOf { it.position.x + it.distance }
    val notPossible = (minX..maxX).count { !beaconCanExistAt(Point(it, y)) }
    // Omit any beacon that actually exists on this row.
    val beaconsOnRow = sensors.map { it.closestBeacon }.filter { it.y == y }.toSet()
    return notPossible - beaconsOnRow.size
  }

  // Just iterating through every point is far too slow. We'll instead iterate through each row and use sparse ranges
  // to see if we can identify a single point in that row as legal.
  fun distressBeacon(max: Int = 4000000) = (0..max).asSequence().map { y ->
    legalDistressXs(y, max) to y
  }
    .first { (xValues, _) -> xValues.size() == 1 }
    .let { (xRange, y) -> Point(xRange.first(), y) }

  private fun legalDistressXs(y: Int, max: Int): SparseRange =
    sensors.fold(SparseRange(0..max)) { range, sensor ->
      range - sensor.eliminatedDistressPositionsOnRow(y)
    }
}

fun Point.tuningFrequency(): Long = this.x.toLong() * 4000000L + this.y.toLong()

/**
 * Represents a union of IntRanges, with a way to remove a new IntRange and leave just the remaining values.
 */
data class SparseRange(
  // Guaranteed non-overlapping.
  private val intRanges: List<IntRange>
) {
  constructor(intRange: IntRange) : this(listOf(intRange))

  operator fun minus(range: IntRange): SparseRange = SparseRange(intRanges.flatMap { it.remove(range) })

  fun first() = intRanges.minOf { it.first }
  fun size() = intRanges.sumOf { it.size }

  private val IntRange.size
    get() = this.last - this.first + 1

  private fun IntRange.remove(other: IntRange): List<IntRange> = when {
    this.first in other && this.last in other -> emptyList()
    other.first <= this.first && other.last in this -> listOf(other.last + 1..this.last)
    other.first in this && other.last >= this.last -> listOf(this.first until other.first)
    other.first in this && other.last in this -> listOf(
      this.first until other.first,
      other.last + 1..this.last
    )
    else -> listOf(this)
  }
}

class DeployableSensor(val position: Point, val closestBeacon: Point) {
  companion object {
    fun parse(input: String): DeployableSensor {
      val regex = "Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)".toRegex()
      val match = regex.matchEntire(input) ?: throw IllegalArgumentException("Unrecognized input $input")
      val (x, y, beaconX, beaconY) = match.destructured
      return DeployableSensor(Point(x.toInt(), y.toInt()), Point(beaconX.toInt(), beaconY.toInt()))
    }
  }

  val distance = position.distanceFrom(closestBeacon)

  fun beaconCanExistAt(point: Point) = position.distanceFrom(point) > distance

  fun eliminatedDistressPositionsOnRow(y: Int): IntRange {
    val delta = distance - (y - position.y).absoluteValue
    return if (delta < 0) IntRange.EMPTY else (position.x - delta)..(position.x + delta)
  }
}

fun main() {
  val input = File("src/main/kotlin/advent/year2022/day15/input.txt").readText().trim()

  val sensors = DeployableSensors(input)
  println(sensors.cannotContainCount(2000000))
  val distress = sensors.distressBeacon()
  println(distress)
  println(distress.tuningFrequency())
}