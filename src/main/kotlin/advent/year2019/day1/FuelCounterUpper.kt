package advent.year2019.day1

import java.io.File

class FuelCounterUpper {

    fun fuelRequired(mass: Int): Int = (mass / 3) - 2

    fun totalFuelRequired(mass: Int): Int {
        val fuel = fuelRequired(mass)
        return if (fuel <= 0) 0 else fuel + totalFuelRequired(fuel)
    }
}

fun main() {
    val counter = FuelCounterUpper()

    val lines = File("src/main/kotlin/advent/year2019/day1/input.txt")
            .readLines()
            .map { it.toInt() }

    val total = lines.sumBy { counter.fuelRequired(it) }
    println(total)

    val completeTotal = lines.sumBy { counter.totalFuelRequired(it) }
    println(completeTotal)
}