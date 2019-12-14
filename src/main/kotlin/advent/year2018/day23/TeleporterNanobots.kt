package advent.year2018.day23

import java.io.File
import java.util.*
import java.util.Comparator.comparing

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

data class Point3D(val x: Int, val y: Int, val z: Int) {
    fun distanceTo(other: Point3D) = Math.abs(this.x - other.x) +
            Math.abs(this.y - other.y) +
            Math.abs(this.z - other.z)
}

class TeleporterNanobots(private val bots: List<TeleporterNanobot>) {
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
        val possibilities = PriorityQueue<OverlapPossibility>(100, OverlapPossibility.best)

        val initialVolume = RectangularVolume(
                (bots.map { it.location.x }.min() ?: 0)..(bots.map { it.location.x }.max() ?: 0),
                (bots.map { it.location.y }.min() ?: 0)..(bots.map { it.location.y }.max() ?: 0),
                (bots.map { it.location.z }.min() ?: 0)..(bots.map { it.location.z }.max() ?: 0))

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

    fun split(): Pair<RectangularVolume, RectangularVolume> = when (listOf(x, y, z).map { it.size() }.max()) {
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