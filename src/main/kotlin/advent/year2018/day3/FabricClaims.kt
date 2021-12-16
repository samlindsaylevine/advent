package advent.year2018.day3

import java.io.File
import java.lang.IllegalArgumentException

/**
 * --- Day 3: No Matter How You Slice It ---
 * The Elves managed to locate the chimney-squeeze prototype fabric for Santa's suit (thanks to someone who helpfully
 * wrote its box IDs on the wall of the warehouse in the middle of the night).  Unfortunately, anomalies are still
 * affecting them - nobody can even agree on how to cut the fabric.
 * The whole piece of fabric they're working on is a very large square - at least 1000 inches on each side.
 * Each Elf has made a claim about which area of fabric would be ideal for Santa's suit.  All claims have an ID and
 * consist of a single rectangle with edges parallel to the edges of the fabric.  Each claim's rectangle is defined as
 * follows:
 * 
 * The number of inches between the left edge of the fabric and the left edge of the rectangle.
 * The number of inches between the top edge of the fabric and the top edge of the rectangle.
 * The width of the rectangle in inches.
 * The height of the rectangle in inches.
 * 
 * A claim like #123 @ 3,2: 5x4 means that claim ID 123 specifies a rectangle 3 inches from the left edge, 2 inches
 * from the top edge, 5 inches wide, and 4 inches tall. Visually, it claims the square inches of fabric represented by
 * # (and ignores the square inches of fabric represented by .) in the diagram below:
 * ...........
 * ...........
 * ...#####...
 * ...#####...
 * ...#####...
 * ...#####...
 * ...........
 * ...........
 * ...........
 * 
 * The problem is that many of the claims overlap, causing two or more claims to cover part of the same areas.  For
 * example, consider the following claims:
 * #1 @ 1,3: 4x4
 * #2 @ 3,1: 4x4
 * #3 @ 5,5: 2x2
 * 
 * Visually, these claim the following areas:
 * ........
 * ...2222.
 * ...2222.
 * .11XX22.
 * .11XX22.
 * .111133.
 * .111133.
 * ........
 * 
 * The four square inches marked with X are claimed by both 1 and 2. (Claim 3, while adjacent to the others, does not
 * overlap either of them.)
 * If the Elves all proceed with their own plans, none of them will have enough fabric. How many square inches of
 * fabric are within two or more claims?
 * 
 * --- Part Two ---
 * Amidst the chaos, you notice that exactly one claim doesn't overlap by even a single square inch of fabric with any
 * other claim. If you can somehow draw attention to it, maybe the Elves will be able to make Santa's suit after all!
 * For example, in the claims above, only claim 3 is intact after all claims are made.
 * What is the ID of the only claim that doesn't overlap?
 * 
 */
class FabricClaims(claims: List<FabricClaim>) {
    companion object {
        fun parse(input: String) = FabricClaims(input.trim()
                .split("\n")
                .map { FabricClaim.parse(it) })
    }

    private val claimsByPoint = claims.asSequence()
            .flatMap { claim -> claim.points().map { point -> Pair(point, claim) } }
            .groupBy { it.first }

    val overlappingSquareInches: Int = claimsByPoint.values.count { it.size >= 2 }

    val nonOverlappingClaims: Set<Int> = claims
            .asSequence()
            .filter { claim ->
                claims.all { otherClaim ->
                    claim == otherClaim || claim.overlap(otherClaim).isEmpty()
                }
            }
            .map { it.number }
            .toSet()
}

data class FabricClaim(val number: Int,
                       val x: Int,
                       val y: Int,
                       val width: Int,
                       val height: Int) {
    companion object {
        private val REGEX = "#(\\d+) @ (\\d+),(\\d+): (\\d+)x(\\d+)".toRegex()

        fun parse(input: String): FabricClaim {
            val match = REGEX.matchEntire(input) ?: throw IllegalArgumentException("Unparseable claim $input")

            return FabricClaim(match.groupValues[1].toInt(),
                    match.groupValues[2].toInt(),
                    match.groupValues[3].toInt(),
                    match.groupValues[4].toInt(),
                    match.groupValues[5].toInt())
        }
    }

    fun points(): Sequence<Point> = (x until x + width).asSequence()
            .flatMap { x -> (y until y + height).asSequence().map { y -> Point(x, y) } }

    fun overlap(other: FabricClaim): Set<Point> = this.points().toSet().intersect(other.points().toSet())
}

data class Point(val x: Int, val y: Int)


fun main() {
    val input = File("src/main/kotlin/advent/year2018/day3/input.txt")
            .readText()
            .trim()

    val claims = FabricClaims.parse(input)

    println(claims.overlappingSquareInches)
    println(claims.nonOverlappingClaims)


}