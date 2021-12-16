package advent.year2017.day21

import java.io.File

/**
 * --- Day 21: Fractal Art ---
 * You find a program trying to generate some art. It uses a strange process that involves repeatedly enhancing the
 * detail of an image through a set of rules.
 * The image consists of a two-dimensional square grid of pixels that are either on (#) or off (.). The program always
 * begins with this pattern:
 * .#.
 * ..#
 * ###
 * 
 * Because the pattern is both 3 pixels wide and 3 pixels tall, it is said to have a size of 3.
 * Then, the program repeats the following process:
 * 
 * If the size is evenly divisible by 2, break the pixels up into 2x2 squares, and convert each 2x2 square into a 3x3
 * square by following the corresponding enhancement rule.
 * Otherwise, the size is evenly divisible by 3; break the pixels up into 3x3 squares, and convert each 3x3 square into
 * a 4x4 square by following the corresponding enhancement rule.
 * 
 * Because each square of pixels is replaced by a larger one, the image gains pixels and so its size increases.
 * The artist's book of enhancement rules is nearby (your puzzle input); however, it seems to be missing rules.  The
 * artist explains that sometimes, one must rotate or flip the input pattern to find a match. (Never rotate or flip the
 * output pattern, though.) Each pattern is written concisely: rows are listed as single units, ordered top-down, and
 * separated by slashes. For example, the following rules correspond to the adjacent patterns:
 * ../.#  =  ..
 *           .#
 * 
 *                 .#.
 * .#./..#/###  =  ..#
 *                 ###
 * 
 *                         #..#
 * #..#/..../#..#/.##.  =  ....
 *                         #..#
 *                         .##.
 * 
 * When searching for a rule to use, rotate and flip the pattern as necessary.  For example, all of the following
 * patterns match the same rule:
 * .#.   .#.   #..   ###
 * ..#   #..   #.#   ..#
 * ###   ###   ##.   .#.
 * 
 * Suppose the book contained the following two rules:
 * ../.# => ##./#../...
 * .#./..#/### => #..#/..../..../#..#
 * 
 * As before, the program begins with this pattern:
 * .#.
 * ..#
 * ###
 * 
 * The size of the grid (3) is not divisible by 2, but it is divisible by 3. It divides evenly into a single square;
 * the square matches the second rule, which produces:
 * #..#
 * ....
 * ....
 * #..#
 * 
 * The size of this enhanced grid (4) is evenly divisible by 2, so that rule is used. It divides evenly into four
 * squares:
 * #.|.#
 * ..|..
 * --+--
 * ..|..
 * #.|.#
 * 
 * Each of these squares matches the same rule (../.# => ##./#../...), three of which require some flipping and
 * rotation to line up with the rule. The output for the rule is the same in all four cases:
 * ##.|##.
 * #..|#..
 * ...|...
 * ---+---
 * ##.|##.
 * #..|#..
 * ...|...
 * 
 * Finally, the squares are joined into a new grid:
 * ##.##.
 * #..#..
 * ......
 * ##.##.
 * #..#..
 * ......
 * 
 * Thus, after 2 iterations, the grid contains 12 pixels that are on.
 * How many pixels stay on after 5 iterations?
 * 
 * --- Part Two ---
 * How many pixels stay on after 18 iterations?
 * 
 */
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