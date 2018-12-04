package advent.year2018.day3

import java.io.File
import java.lang.IllegalArgumentException

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


fun main(args: Array<String>) {
    val input = File("src/main/kotlin/advent/year2018/day3/input.txt")
            .readText()
            .trim()

    val claims = FabricClaims.parse(input)

    println(claims.overlappingSquareInches)
    println(claims.nonOverlappingClaims)


}