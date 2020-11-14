package advent.year2018.day10

import java.io.File

class StarMessage(private val stars: List<MovingStar>) {

    companion object {
        fun parse(input: String) = StarMessage(input.trim().split("\n").map { MovingStar.parse(it) })
    }

    /**
     * We're going to guess that the message is visible at the time when they are tightest together.
     * We'll just suppose this happens at the point at which they fit in the minimum width.
     */
    fun message(): VisibleMessage {
        val tightestPositions = generateSequence(0, Int::inc)
                .map { positionsAt(it) }
                .withIndex()
                .zipWithNext()
                .first { it.second.value.maxX - it.second.value.minX > it.first.value.maxX - it.first.value.minX }
                .first

        return VisibleMessage(tightestPositions.index, render(tightestPositions.value))
    }

    private fun positionsAt(time: Int) = Positions(stars.map { it.positionAt(time) }.toSet())

    private fun render(positions: Positions): String {
        return (positions.minY..positions.maxY).joinToString("\n") { y ->
            (positions.minX..positions.maxX).joinToString("") { x -> if (positions.contains(Coordinates(x, y))) "#" else " " }
        }
    }

    private data class Positions(val coordinates: Set<Coordinates>) {
        val minX = coordinates.map { it.x }.minOrNull() ?: 0
        val minY = coordinates.map { it.y }.minOrNull() ?: 0
        val maxX = coordinates.map { it.x }.maxOrNull() ?: 0
        val maxY = coordinates.map { it.y }.maxOrNull() ?: 0

        fun contains(coordinate: Coordinates) = coordinates.contains(coordinate)
    }

    data class VisibleMessage(val time: Int, val display: String)
}

class MovingStar(private val position: Coordinates, private val velocity: Coordinates) {
    companion object {
        private const val COORDINATE_REGEX = "<\\s*(-?\\d+),\\s*(-?\\d+)>"
        private val REGEX = "position=$COORDINATE_REGEX velocity=$COORDINATE_REGEX".toRegex()

        fun parse(input: String): MovingStar {
            val match = REGEX.matchEntire(input) ?: throw IllegalArgumentException("Unparseable star $input")

            return MovingStar(Coordinates(match.groupValues[1].toInt(), match.groupValues[2].toInt()),
                    Coordinates(match.groupValues[3].toInt(), match.groupValues[4].toInt()))
        }
    }

    fun positionAt(time: Int) = position + time * velocity
}

data class Coordinates(val x: Int, val y: Int) {
    operator fun plus(other: Coordinates) = Coordinates(this.x + other.x, this.y + other.y)
}

operator fun Int.times(coordinates: Coordinates) = Coordinates(this * coordinates.x, this * coordinates.y)


fun main() {
    val input = File("src/main/kotlin/advent/year2018/day10/input.txt")
            .readText()
            .trim()

    val message = StarMessage.parse(input).message()

    println(message.display)
    println(message.time)
}