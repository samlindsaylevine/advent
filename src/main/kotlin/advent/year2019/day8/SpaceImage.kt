package advent.year2019.day8

import java.io.File
import java.lang.IllegalStateException

class SpaceImage(val layers: List<SpaceImageLayer>) {
    companion object {
        fun parse(input: String, width: Int, height: Int): SpaceImage {
            val numbers = input.trim()
                    .split("")
                    .filter { it.isNotEmpty() }
                    .map { it.toInt() }

            val layers = numbers.chunked(width * height)
                    .map { SpaceImageLayer.parse(it, width) }

            return SpaceImage(layers)
        }
    }

    private fun pixelAt(x: Int, y: Int) = layers.map { it.pixelAt(x, y) }
            .first { it != 2 }

    val width = if (layers.isEmpty()) 0 else {
        layers.first().rows.first().size
    }

    val height = if (layers.isEmpty()) 0 else {
        layers.first().rows.size
    }

    override fun toString(): String {
        if (layers.isEmpty()) return ""

        return (0 until height).joinToString("\n") { y ->
            (0 until width).joinToString("") { x ->
                if (pixelAt(x, y) == 1) "*" else " "
            }
        }
    }
}

class SpaceImageLayer(val rows: List<List<Int>>) {
    companion object {
        fun parse(input: List<Int>, width: Int) = SpaceImageLayer(input.chunked(width))
    }

    fun count(digit: Int) = rows.flatten().count { it == digit }

    fun pixelAt(x: Int, y: Int) = rows[y][x]
}

fun main() {
    val input = File("src/main/kotlin/advent/year2019/day8/input.txt")
            .readText()

    val image = SpaceImage.parse(input, width = 25, height = 6)

    val fewestZeroDigits = image.layers.minBy { it.count(0) }
            ?: throw IllegalStateException("Should have been layers")
    println(fewestZeroDigits.count(1) * fewestZeroDigits.count(2))

    println(image)
}