package advent.year2017.day22

import java.io.File

class VirusGrid(private val infectedPositions: Set<Pair<Int, Int>>) {

    companion object {
        fun fromInput(input: String): VirusGrid {
            val lines = input.split("\n")

            val height = lines.size
            val width = lines.first().length

            val yOffset = (height - 1) / 2
            val xOffset = (width - 1) / 2

            val infectedPositions = (-xOffset..xOffset).flatMap { x ->
                (-yOffset..yOffset).map { y -> Pair(x, y) }
            }
                    .asSequence()
                    .filter { pair -> lines[-pair.second + yOffset][pair.first + xOffset] == '#' }
                    .toSet()

            return VirusGrid(infectedPositions)
        }
    }

    fun isInfected(x: Int, y: Int): Boolean = infectedPositions.contains(Pair(x, y))

    fun withInfected(x: Int, y: Int): VirusGrid = VirusGrid(infectedPositions + Pair(x, y))

    fun withCleaned(x: Int, y: Int): VirusGrid = VirusGrid(infectedPositions - Pair(x, y))
}

class CarrierTrip(val causedAnInfectionBursts: Int,
                  private val totalBursts: Int,
                  private val carrier: Carrier,
                  val grid: VirusGrid) {

    constructor(grid: VirusGrid) : this(0,
            0,
            Carrier(0, 0, Direction.UP),
            grid)

    fun next(): CarrierTrip {
        val currentNodeIsInfected = grid.isInfected(carrier.x, carrier.y)

        val turnedCarrier = if (currentNodeIsInfected) {
            carrier.copy(direction = carrier.direction.right())
        } else {
            carrier.copy(direction = carrier.direction.left())
        }

        val (nextGrid, newInfectionCount) = if (currentNodeIsInfected) {
            Pair(grid.withCleaned(carrier.x, carrier.y), causedAnInfectionBursts)
        } else {
            Pair(grid.withInfected(carrier.x, carrier.y), causedAnInfectionBursts + 1)
        }

        val nextCarrier = turnedCarrier.movedForward()

        return CarrierTrip(newInfectionCount,
                totalBursts + 1,
                nextCarrier,
                nextGrid)
    }

    fun afterBursts(count: Int): CarrierTrip = (0 until count).fold(this) { trip, _ -> trip.next() }
}

data class Carrier(val x: Int, val y: Int, val direction: Direction) {
    fun movedForward() = Carrier(this.x + direction.x, this.y + direction.y, direction)
}

enum class Direction(val x: Int, val y: Int) {
    UP(0, 1),
    RIGHT(1, 0),
    DOWN(0, -1),
    LEFT(-1, 0);

    companion object {
        private fun getByOrdinal(ordinal: Int) = Direction.values()[Math.floorMod(ordinal,
                Direction.values().size)]
    }

    fun right() = getByOrdinal(this.ordinal + 1)
    fun left() = getByOrdinal(this.ordinal - 1)
}

fun main(args: Array<String>) {
    val input = File("src/main/kotlin/advent/year2017/day22/input.txt")
            .readText()
            .trim()

    val grid = VirusGrid.fromInput(input)

    println(CarrierTrip(grid).afterBursts(10_000).causedAnInfectionBursts)
}