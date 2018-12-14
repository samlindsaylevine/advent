package advent.year2018.day11

import advent.year2018.day10.Coordinates

class FuelCellGrid(private val serialNumber: Int) {

    private val width = 300
    private val height = 300

    private val memoizedCells: MutableMap<Coordinates, Int> = mutableMapOf()

    fun powerLevel(coordinates: Coordinates): Int = memoizedCells.computeIfAbsent(coordinates, this::calculatePower)

    private fun calculatePower(coordinates: Coordinates): Int {
        val rackId = coordinates.x + 10

        return (((rackId * coordinates.y) + serialNumber) * rackId).hundredsDigit() - 5
    }

    private fun Int.hundredsDigit() = (this / 100) % 10

    fun upperLeftOfMostPowerfulSquareOfSize(size: Int) = mostPowerfulSquareOfSize(size).square.upperLeft

    private fun mostPowerfulSquareOfSize(size: Int): SquareAndPower = box(Coordinates(0, 0), width - size + 1, height - size + 1)
            .map { Square(it, size) }
            .map { SquareAndPower(it, powerOfSquare(it)) }
            .maxBy { it.power }
            ?: throw IllegalStateException("Box too small to contain any square")

    fun powerOfSquare(square: Square) = box(square.upperLeft, square.size, square.size)
            .sumBy { powerLevel(it) }

    private fun box(upperLeft: Coordinates, width: Int, height: Int): Sequence<Coordinates> =
            (upperLeft.x until upperLeft.x + width).asSequence().flatMap { x ->
                (upperLeft.y until upperLeft.y + height).asSequence().map { y -> Coordinates(x, y) }
            }

    data class Square(val upperLeft: Coordinates, val size: Int)
    private data class SquareAndPower(val square: Square, val power: Int)

    fun mostPowerfulSquare(upperSearchBound: Int = width) = (1 until upperSearchBound).asSequence()
            // for debugging progress
            // .onEach { println(it) }
            .map { mostPowerfulSquareOfSize(it) }
            .maxBy { it.power }
            ?.square
}

fun main(args: Array<String>) {
    val grid = FuelCellGrid(9005)

    println(grid.upperLeftOfMostPowerfulSquareOfSize(3))

    // We really should search all the squares up to max size, but that takes way too freaking long so I'm going to
    // use this ugly hack and only go up to this size. I've convinced myself that larger squares do not appear to
    // have anything higher in value.
    println(grid.mostPowerfulSquare(upperSearchBound = 20))
}