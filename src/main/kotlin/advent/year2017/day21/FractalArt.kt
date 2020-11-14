package advent.year2017.day21

import java.io.File

class FractalArtBook(private val rules: List<FractalArtRule>) {
    constructor(input: String) : this(input.trim().split("\n")
            .map { FractalArtRule.fromLine(it) })

    fun next(image: FractalArtImage): FractalArtImage {
        val partitioned = image.partition()

        return partitioned.map { it.map { square -> nextSquare(square) } }
                .merge()
    }

    /**
     * Finds the next iteration for a single partitioned square, checking it against the rules.
     */
    private fun nextSquare(square: FractalArtImage): FractalArtImage {
        val keys = square.equivalentRuleKeys()

        val rule = rules.firstOrNull { rule -> square.equivalentRuleKeys().contains(rule.from) }
                ?: throw IllegalArgumentException("Could not find rule from any key $keys")

        return rule.to
    }
}

data class FractalArtRule(val from: FractalArtImage, val to: FractalArtImage) {
    companion object {
        private val REGEX = "(.*) => (.*)".toRegex()
        fun fromLine(line: String): FractalArtRule {
            val match = REGEX.matchEntire(line.trim()) ?: throw IllegalArgumentException("Unparseable rule $line")
            return FractalArtRule(FractalArtImage(match.groupValues[1], lineDelimiter = "/"),
                    FractalArtImage(match.groupValues[2], lineDelimiter = "/"))
        }
    }
}

data class FractalArtImage internal constructor(val pixels: List<List<Boolean>>) {
    constructor(pattern: String, lineDelimiter: String = "\n") : this(pattern.trim().split(lineDelimiter)
            .map { line -> line.toCharArray().map { it == '#' }.toList() })

    override fun toString() = pixels.joinToString(separator = "\n")
    { line -> line.joinToString(separator = "") { if (it) "#" else "." } }

    fun onPixelCount() = pixels.asSequence()
            .flatMap { it.asSequence() }
            .count { it }

    fun partition(): List<List<FractalArtImage>> {
        val size = pixels.size
        val squareSize = if (size % 2 == 0) 2 else 3
        val numSquares = size / squareSize

        return (0 until numSquares).map { y ->
            (0 until numSquares).map { x -> square(x, y, squareSize) }
        }
    }

    private fun square(squareX: Int, squareY: Int, size: Int): FractalArtImage {
        val minX = squareX * size
        val minY = squareY * size

        val squarePixels = (minY until minY + size).map { y ->
            (minX until minX + size).map { x -> pixels[y][x] }
        }

        return FractalArtImage(squarePixels)
    }

    fun flipped(): FractalArtImage = FractalArtImage(this.pixels.reversed())
    fun rotated(): FractalArtImage = FractalArtImage((0 until pixels.size).map { y ->
        (0 until pixels.size).map { x -> pixels[pixels.size - 1 - x][y] }
    })

    fun equivalentRuleKeys() = listOf(this,
            this.rotated(),
            this.rotated().rotated(),
            this.rotated().rotated().rotated(),
            this.flipped(),
            this.flipped().rotated(),
            this.flipped().rotated().rotated(),
            this.flipped().rotated().rotated().rotated())
}


fun List<List<FractalArtImage>>.merge(): FractalArtImage {
    return FractalArtImage(this.flatMap { it.merge() })
}

private fun List<FractalArtImage>.merge(): List<List<Boolean>> {
    if (this.isEmpty()) return listOf()

    val size = this.first().pixels.size
    return (0 until size).map { rowNum -> this.flatMap { image -> image.pixels[rowNum] } }
}

fun main() {
    val input = File("src/main/kotlin/advent/year2017/day21/input.txt")
            .readText()

    val book = FractalArtBook(input)

    val starterImage = FractalArtImage("""
            .#.
            ..#
            ###
        """.trimIndent())

    val finalImage = (1..5).fold(starterImage) { image, _ -> book.next(image) }
    println(finalImage.onPixelCount())
    val secondPartImage = (1..18).fold(starterImage) { image, _ -> book.next(image) }
    println(secondPartImage.onPixelCount())
}