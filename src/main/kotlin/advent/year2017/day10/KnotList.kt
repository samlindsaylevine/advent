package advent.year2017.day10

import java.io.File

class KnotList(size: Int = 256) {

    private var values = (0 until size).toList()
    var currentPosition = 0
    var skipSize = 0

    fun productOfFirstTwo(): Int = values[0] * values[1]

    fun values(): List<Int> = values

    fun applyLengths(lengths: List<Int>) = lengths.forEach { applyLength(it) }

    private fun applyLength(length: Int) {
        reverseElements(length)
        currentPosition = (currentPosition + length + skipSize) % values.size
        skipSize++
    }

    private fun reverseElements(length: Int) {
        if (currentPosition + length <= values.size) {
            // Reversed section did not wrap.
            values = values.subList(0, currentPosition) +
                    values.subList(currentPosition, currentPosition + length).reversed() +
                    values.subList(currentPosition + length, values.size)
        } else {
            // Reversed section did wrap.
            val overwrapCount = currentPosition + length - values.size

            val reversedSection = (values.subList(currentPosition, values.size) +
                    values.subList(0, overwrapCount)).reversed()

            values = reversedSection.subList(reversedSection.size - overwrapCount, reversedSection.size) +
                    values.subList(overwrapCount, currentPosition) +
                    reversedSection.subList(0, reversedSection.size - overwrapCount)
        }
    }

}

fun main(args: Array<String>) {
    val input = File("src/main/kotlin/advent/year2017/day10/input.txt").readText()
            .trim()
            .split(",")
            .map { it.toInt() }
    val knotList = KnotList()
    knotList.applyLengths(input)

    println(knotList.productOfFirstTwo())
}