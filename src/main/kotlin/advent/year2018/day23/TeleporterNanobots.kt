package advent.year2018.day23

import advent.year2015.day24.Ticker
import advent.year2018.day6.allMinBy
import java.io.File
import java.lang.IllegalArgumentException

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


    fun originDistanceRange(): IntRange {
        val distanceToOrigin = this.location.distanceTo(Point3D(0, 0, 0))
        val lower = Math.max(0, distanceToOrigin - range)
        val upper = distanceToOrigin + range
        return lower..upper
    }
}

data class Point3D(val x: Int, val y: Int, val z: Int) {
    fun distanceTo(other: Point3D) = Math.abs(this.x - other.x) +
            Math.abs(this.y - other.y) +
            Math.abs(this.z - other.z)
}

class TeleporterNanobots(val bots: List<TeleporterNanobot>) {
    companion object {
        fun parse(input: String) = TeleporterNanobots(input.lines().map { TeleporterNanobot.parse(it) })
    }

    fun botCountInRangeOfStrongest(): Int {
        val strongest = bots.maxBy { it.range } ?: return 0
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
        TODO()
    }
}

fun main() {
    val input = File("src/main/kotlin/advent/year2018/day23/input.txt")
            .readText()
            .trim()

    val nanobots = TeleporterNanobots.parse(input)
    println(nanobots.botCountInRangeOfStrongest())

}