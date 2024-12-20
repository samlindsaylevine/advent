package advent.year2024.day20

import advent.meta.readInput
import advent.utils.Point
import advent.utils.takeWhileInclusive
import kotlin.math.absoluteValue

class Racetrack(val start: Point, val end: Point, val walls: Set<Point>) {
    companion object {
        fun of(input: String): Racetrack {
            val pointsToChars = input.trim().lines().flatMapIndexed { y, line ->
                line.mapIndexed { x, c -> Point(x, y) to c }
            }

            val start = pointsToChars.first { (_, c) -> c == 'S' }.first
            val end = pointsToChars.first { (_, c) -> c == 'E' }.first
            val walls = pointsToChars.filter { (_, c) -> c == '#' }
                .map { (point, _) -> point }
                .toSet()

            return Racetrack(start, end, walls)
        }
    }

    // Calculate our path from start to end, which we believe to be a pure racetrack not including any decisions.
    // We'll double check that assumption here as well.
    private val path: List<Point> by lazy {
        // The sequence is pairs of (previous, current) so that we know which way to go.
        generateSequence(start to start) { (previous, current) ->
            val nextOptions = current.adjacentNeighbors.filter { it !in walls && it != previous }
            if (nextOptions.size != 1) throw IllegalStateException("Expected a pure racetrack but at $current we have next options $nextOptions")
            val next = nextOptions.first()
            current to next
        }.takeWhileInclusive { (_, current) -> current != end }
            .map { (_, current) -> current }
            .toList()
    }

    // Given a point, returns what position that point is on the path from start to end. If the point is not on the
    // path, it will not be contained in this map.
    private val pointToPathIndex: Map<Point, Int> = path.withIndex().associate { it.value to it.index }

    private fun cheats(): Sequence<Cheat> = path.asSequence().flatMap { it.cheats() }

    private fun Point.cheats(): List<Cheat> {
        val cheatDistance = 2
        val deltas = (-cheatDistance..cheatDistance).flatMap { deltaX ->
            val maxVertical = cheatDistance - deltaX.absoluteValue
            (-maxVertical..maxVertical).map { deltaY -> Point(deltaX, deltaY) }
        }
        val ends = deltas.map { this + it }
        return ends.map { Cheat(this, it) }
    }

    class Cheat(val start: Point, val end: Point)

    private fun Cheat.amountSaved(): Int? {
        val startIndex = pointToPathIndex[start]
        val endIndex = pointToPathIndex[end]
        if (startIndex == null || endIndex == null) return null
        val distance = endIndex - startIndex
        // We do still have to spend the amount of time moving from start to end of the cheat!
        return distance - (end.distanceFrom(start))
    }

    fun countCheatsSaving(amount: Int) = cheats().count { it.amountSaved() == amount }
    fun countCheatsSavingAtLeast(amount: Int) = cheats().count {
        val saved = it.amountSaved()
        saved != null && saved >= amount
    }
}

fun main() {
    val racetrack = Racetrack.of(readInput())

    println(racetrack.countCheatsSavingAtLeast(100))
}