package advent.year2018.day23

import advent.utils.Point3D
import java.io.File
import java.util.*
import java.util.Comparator.comparing

/**
 * --- Day 23: Experimental Emergency Teleportation ---
 * Using your torch to search the darkness of the rocky cavern, you finally locate the man's friend: a small reindeer.
 * You're not sure how it got so far in this cave.  It looks sick - too sick to walk - and too heavy for you to carry
 * all the way back.  Sleighs won't be invented for another 1500 years, of course.
 * The only option is experimental emergency teleportation.
 * You hit the "experimental emergency teleportation" button on the device and push I accept the risk on no fewer than
 * 18 different warning messages. Immediately, the device deploys hundreds of tiny nanobots which fly around the
 * cavern, apparently assembling themselves into a very specific formation. The device lists the X,Y,Z position (pos)
 * for each nanobot as well as its signal radius (r) on its tiny screen (your puzzle input).
 * Each nanobot can transmit signals to any integer coordinate which is a distance away from it less than or equal to
 * its signal radius (as measured by Manhattan distance). Coordinates a distance away of less than or equal to a
 * nanobot's signal radius are said to be in range of that nanobot.
 * Before you start the teleportation process, you should determine which nanobot is the strongest (that is, which has
 * the largest signal radius) and then, for that nanobot, the total number of nanobots that are in range of it,
 * including itself.
 * For example, given the following nanobots:
 * pos=<0,0,0>, r=4
 * pos=<1,0,0>, r=1
 * pos=<4,0,0>, r=3
 * pos=<0,2,0>, r=1
 * pos=<0,5,0>, r=3
 * pos=<0,0,3>, r=1
 * pos=<1,1,1>, r=1
 * pos=<1,1,2>, r=1
 * pos=<1,3,1>, r=1
 * 
 * The strongest nanobot is the first one (position 0,0,0) because its signal radius, 4 is the largest. Using that
 * nanobot's location and signal radius, the following nanobots are in or out of range:
 * 
 * The nanobot at 0,0,0 is distance 0 away, and so it is in range.
 * The nanobot at 1,0,0 is distance 1 away, and so it is in range.
 * The nanobot at 4,0,0 is distance 4 away, and so it is in range.
 * The nanobot at 0,2,0 is distance 2 away, and so it is in range.
 * The nanobot at 0,5,0 is distance 5 away, and so it is not in range.
 * The nanobot at 0,0,3 is distance 3 away, and so it is in range.
 * The nanobot at 1,1,1 is distance 3 away, and so it is in range.
 * The nanobot at 1,1,2 is distance 4 away, and so it is in range.
 * The nanobot at 1,3,1 is distance 5 away, and so it is not in range.
 * 
 * In this example, in total, 7 nanobots are in range of the nanobot with the largest signal radius.
 * Find the nanobot with the largest signal radius.  How many nanobots are in range of its signals?
 * 
 * --- Part Two ---
 * Now, you just need to figure out where to position yourself so that you're actually teleported when the nanobots
 * activate.
 * To increase the probability of success, you need to find the coordinate which puts you in range of the largest
 * number of nanobots. If there are multiple, choose one closest to your position (0,0,0, measured by manhattan
 * distance).
 * For example, given the following nanobot formation:
 * pos=<10,12,12>, r=2
 * pos=<12,14,12>, r=2
 * pos=<16,12,12>, r=4
 * pos=<14,14,14>, r=6
 * pos=<50,50,50>, r=200
 * pos=<10,10,10>, r=5
 * 
 * Many coordinates are in range of some of the nanobots in this formation.  However, only the coordinate 12,12,12 is
 * in range of the most nanobots: it is in range of the first five, but is not in range of the nanobot at 10,10,10.
 * (All other coordinates are in range of fewer than five nanobots.) This coordinate's distance from 0,0,0 is 36.
 * Find the coordinates that are in range of the largest number of nanobots. What is the shortest manhattan distance
 * between any of those points and 0,0,0?
 * 
 */
data class TeleporterNanobot(val location: Point3D, val range: Int) {
  companion object {
    private val REGEX = "pos=<(-?\\d+),(-?\\d+),(-?\\d+)>, r=(\\d+)".toRegex()

    fun parse(input: String): TeleporterNanobot {
      val match = REGEX.matchEntire(input) ?: throw IllegalArgumentException("Unparseable input $input")
      return TeleporterNanobot(Point3D(match.groupValues[1].toInt(),
              match.groupValues[2].toInt(),
              match.groupValues[3].toInt()),
              match.groupValues[4].toInt())
    }
  }

  infix fun isInRangeOf(other: TeleporterNanobot) = (this.location.distanceTo(other.location)) <= other.range
}

class TeleporterNanobots(private val bots: List<TeleporterNanobot>) {
  companion object {
    fun parse(input: String) = TeleporterNanobots(input.lines().map { TeleporterNanobot.parse(it) })
  }

  fun botCountInRangeOfStrongest(): Int {
    val strongest = bots.maxByOrNull { it.range } ?: return 0
    return bots.count { it isInRangeOf strongest }
  }

  /**
   * The second half of this problem is very difficult.
   *
   * The challenge is an implementation that is both performant and correct.
   *
   * Obviously we cannot just check points exhaustively - the possible space is too large.
   *
   * We could attempt to repeatedly split the space and investigate only whichever half overlapped with more bots -
   * but just because a rectilinear volume overlaps with N bots, does not mean that there is an actual point of
   * overlap N within that volume, so we wouldn't be justified in discarding the other half.
   *
   * One option is to make some simplifying, or unproved, assumptions.
   *
   * Based on some experimentation, it seems like if any three bots all pairwise-overlap with each other, then there
   * must be an area of mutual overlap. (This comes from the octohedral shape of the bots' regions.) I can't prove
   * this, but I think if it is true for 3 is must be true for any number, based on some informal inductive
   * reasoning.
   *
   * So, one option would be to find the maximal connected subgraph of bots (where edges in the graph indicate
   * an overlap). That would tell us what bots overlap in the maximal region ... but then could we determine distance?
   *
   * I conjecture that it would be the largest value, among those bots, of its minimum distance from the origin...
   * but I'm not sure I really have a proof for that either.
   *
   * Here's an approach similar to the "repeatedly split" above - split the space, but do not _discard_ either half.
   * Instead, keep both in a "possiblity list". Keep the possibility list sorted by
   * comparing { it.numberOfOverlappingBots }.thenComparing { it.minimumDistanceFromOrigin }.
   * After splitting, examine the next thing at the head of the possibility list. If it is only a single voxel, then
   * it is our result. Otherwise, split it, and add both the resulting possiblities back into the possibility list,
   * and repeat.
   */
  fun originDistanceOfMostOverlap(): Int {
    val possibilities = PriorityQueue<OverlapPossibility>(100, OverlapPossibility.best)

    val initialVolume = RectangularVolume(
            (bots.map { it.location.x }.minOrNull() ?: 0)..(bots.map { it.location.x }.maxOrNull() ?: 0),
            (bots.map { it.location.y }.minOrNull() ?: 0)..(bots.map { it.location.y }.maxOrNull() ?: 0),
            (bots.map { it.location.z }.minOrNull() ?: 0)..(bots.map { it.location.z }.maxOrNull() ?: 0))

    possibilities.add(evaluate(initialVolume))

    return findBest(possibilities)
  }

  private tailrec fun findBest(possiblities: PriorityQueue<OverlapPossibility>): Int {
    val candidate = possiblities.poll()

    return if (candidate.volume.volume() == 1L) {
      candidate.distanceToOrigin
    } else {
      val newVolumes = candidate.volume.split()
      possiblities.add(evaluate(newVolumes.first))
      possiblities.add(evaluate(newVolumes.second))
      findBest(possiblities)
    }
  }

  private fun evaluate(volume: RectangularVolume) = OverlapPossibility(
          bots.count { volume.distanceTo(it.location) <= it.range },
          volume)
}

private data class RectangularVolume(val x: IntRange,
                                     val y: IntRange,
                                     val z: IntRange) {

  private fun IntRange.size() = (this.endInclusive - this.first + 1)
  private fun IntRange.closestValueTo(number: Int) = when {
    number < this.first -> this.first
    number > this.endInclusive -> this.endInclusive
    else -> number
  }

  fun volume(): Long = x.size().toLong() * y.size() * z.size()

  fun closestPointTo(point: Point3D) = Point3D(
          x.closestValueTo(point.x),
          y.closestValueTo(point.y),
          z.closestValueTo(point.z)
  )

  fun distanceTo(point: Point3D) = this.closestPointTo(point).distanceTo(point)

  fun split(): Pair<RectangularVolume, RectangularVolume> = when (listOf(x, y, z).map { it.size() }.maxOrNull()) {
    x.size() -> RectangularVolume(x.split().first, y, z) to RectangularVolume(x.split().second, y, z)
    y.size() -> RectangularVolume(x, y.split().first, z) to RectangularVolume(x, y.split().second, z)
    else -> RectangularVolume(x, y, z.split().first) to RectangularVolume(x, y, z.split().second)
  }
}

fun IntRange.split(): Pair<IntRange, IntRange> {
  val mid = (this.endInclusive + this.first) / 2
  return (this.first..mid) to ((mid + 1)..this.endInclusive)
}

private data class OverlapPossibility(val overlapCount: Int,
                                      val volume: RectangularVolume) {
  val distanceToOrigin = volume.distanceTo(Point3D(0, 0, 0))
  private val size = volume.volume()

  companion object {
    val best: Comparator<OverlapPossibility> = comparing(OverlapPossibility::overlapCount).reversed()
            .thenComparing(OverlapPossibility::distanceToOrigin)
            .thenComparing(OverlapPossibility::size)
  }
}

fun main() {
  val input = File("src/main/kotlin/advent/year2018/day23/input.txt")
          .readText()
          .trim()

  val nanobots = TeleporterNanobots.parse(input)
  println(nanobots.botCountInRangeOfStrongest())
  println(nanobots.originDistanceOfMostOverlap())

}