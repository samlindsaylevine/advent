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

    fun upperLeftOfMostPowerfulSquare(): Coordinates = box(Coordinates(0, 0), width - 3, height - 3)
            .maxBy { powerOfSquare(it) }
            ?: throw IllegalStateException("Box too small to contain any square")

    private fun powerOfSquare(upperLeft: Coordinates) = box(upperLeft, 3, 3)
            .map { powerLevel(it) }
            .sum()

    private fun box(upperLeft: Coordinates, width: Int, height: Int) = (upperLeft.x until upperLeft.x + width).asSequence().flatMap { x ->
        (upperLeft.y until upperLeft.y + height).asSequence().map { y -> Coordinates(x, y) }
    }

}

fun main(args: Array<String>) {
    val grid = FuelCellGrid(9005)

    println(grid.upperLeftOfMostPowerfulSquare())
}