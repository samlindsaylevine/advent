package advent.year2017.day2

import java.io.File

class CorruptedSpreadsheet(private val rows: List<List<Int>>) {

    constructor(lines: Sequence<String>) : this(parse(lines))

    companion object {
        fun parse(input: Sequence<String>): List<List<Int>> {
            return input.filter { it.isNotEmpty() }
                    .map { parseLine(it) }
                    .toList()
        }

        private fun parseLine(input: String): List<Int> {
            return input.trim().split("\\s+".toRegex()).map { it.toInt() }
        }
    }

    fun checksum(): Int = rows.map { checksum(it) }.sum()

    private fun checksum(values: List<Int>): Int = (values.max() ?: 0) - (values.min() ?: 0)

    fun evenlyDivisibleSum(): Int = rows.map { evenDivisionResult(it) }.sum()

    private fun evenDivisionResult(values: List<Int>): Int {
        for (value in values) {
            values.filter { value > it && (value % it == 0) }
                    .forEach { return value / it }
        }

        throw IllegalArgumentException("No even division in $values")
    }

}

fun main(args: Array<String>) {
    val input = File("src/main/kotlin/advent/year2017/day2/input.txt")
            .useLines { CorruptedSpreadsheet(it) }

    println(input.checksum())
    println(input.evenlyDivisibleSum())
}