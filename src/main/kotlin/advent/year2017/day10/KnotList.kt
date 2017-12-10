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

class KnotHash(input: String) {

    companion object {
        // Unexplained numbers used in algorithm description - possible NSA backdoor?
        private val MAGIC_END_SEQUENCE = listOf(17, 31, 73, 47, 23)

        private val NUM_ROUNDS = 64

        private val BLOCK_SIZE = 16

        private fun <T> blocks(blockSize: Int, input: List<T>): List<List<T>> =
                (0 until input.size / blockSize).map { input.subList(it * blockSize, (it + 1) * blockSize) }

        fun toHex(input: Int, minDigits: Int = 2): String {
            var hex = input.toString(16)

            while (hex.length < minDigits) {
                hex = "0" + hex
            }

            return hex
        }
    }

    val hex: String

    init {
        val lengths = input.toCharArray().map { it.toInt() }.toList() + MAGIC_END_SEQUENCE
        val list = KnotList()
        (1..NUM_ROUNDS).forEach { list.applyLengths(lengths) }
        val sparseHash = list.values()
        val denseHash = blocks(BLOCK_SIZE, sparseHash).map { it.reduce { x, y -> x.xor(y) } }
        hex = denseHash.joinToString(separator = "", transform = { toHex(it) })
    }
}

fun main(args: Array<String>) {
    val input = File("src/main/kotlin/advent/year2017/day10/input.txt").readText().trim()

    val knotList = KnotList()
    knotList.applyLengths(input.split(",")
            .map { it.toInt() })

    println(knotList.productOfFirstTwo())

    println(KnotHash(input).hex)
}