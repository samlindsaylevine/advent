package advent.year2021.day20

import advent.utils.Point
import advent.utils.next
import java.io.File

/**
 * --- Day 20: Trench Map ---
 * With the scanners fully deployed, you turn their attention to mapping the floor of the ocean trench.
 * When you get back the image from the scanners, it seems to just be random noise. Perhaps you can combine an image
 * enhancement algorithm and the input image (your puzzle input) to clean it up a little.
 * For example:
 * ..#.#..#####.#.#.#.###.##.....###.##.#..###.####..#####..#....#..#..##..##
 * #..######.###...####..#..#####..##..#.#####...##.#.#..#.##..#.#......#.###
 * .######.###.####...#.##.##..#..#..#####.....#.#....###..#.##......#.....#.
 * .#..#..##..#...##.######.####.####.#.#...#.......#..#.#.#...####.##.#.....
 * .#..#...##.#.##..#...##.#.##..###.#......#.#.......#.#.#.####.###.##...#..
 * ...####.#..#..#.##.#....##..#.####....##...##..#...#......#.#.......#.....
 * ..##..####..#...#.#.#...##..#.#..###..#####........#..####......#..#
 *
 * #..#.
 * #....
 * ##..#
 * ..#..
 * ..###
 *
 * The first section is the image enhancement algorithm. It is normally given on a single line, but it has been wrapped
 * to multiple lines in this example for legibility. The second section is the input image, a two-dimensional grid of
 * light pixels (#) and dark pixels (.).
 * The image enhancement algorithm describes how to enhance an image by simultaneously converting all pixels in the
 * input image into an output image. Each pixel of the output image is determined by looking at a 3x3 square of pixels
 * centered on the corresponding input image pixel. So, to determine the value of the pixel at (5,10) in the output
 * image, nine pixels from the input image need to be considered: (4,9), (4,10), (4,11), (5,9), (5,10), (5,11), (6,9),
 * (6,10), and (6,11). These nine input pixels are combined into a single binary number that is used as an index in the
 * image enhancement algorithm string.
 * For example, to determine the output pixel that corresponds to the very middle pixel of the input image, the nine
 * pixels marked by [...] would need to be considered:
 * # . . # .
 * #[. . .].
 * #[# . .]#
 * .[. # .].
 * . . # # #
 *
 * Starting from the top-left and reading across each row, these pixels are ..., then #.., then .#.; combining these
 * forms ...#...#.. By turning dark pixels (.) into 0 and light pixels (#) into 1, the binary number 000100010 can be
 * formed, which is 34 in decimal.
 * The image enhancement algorithm string is exactly 512 characters long, enough to match every possible 9-bit binary
 * number. The first few characters of the string (numbered starting from zero) are as follows:
 * 0         10        20        30  34    40        50        60        70
 * |         |         |         |   |     |         |         |         |
 * ..#.#..#####.#.#.#.###.##.....###.##.#..###.####..#####..#....#..#..##..##
 *
 * In the middle of this first group of characters, the character at index 34 can be found: #. So, the output pixel in
 * the center of the output image should be #, a light pixel.
 * This process can then be repeated to calculate every pixel of the output image.
 * Through advances in imaging technology, the images being operated on here are infinite in size. Every pixel of the
 * infinite output image needs to be calculated exactly based on the relevant pixels of the input image. The small
 * input image you have is only a small region of the actual infinite input image; the rest of the input image consists
 * of dark pixels (.). For the purposes of the example, to save on space, only a portion of the infinite-sized input
 * and output images will be shown.
 * The starting input image, therefore, looks something like this, with more dark pixels (.) extending forever in every
 * direction not shown here:
 * ...............
 * ...............
 * ...............
 * ...............
 * ...............
 * .....#..#......
 * .....#.........
 * .....##..#.....
 * .......#.......
 * .......###.....
 * ...............
 * ...............
 * ...............
 * ...............
 * ...............
 *
 * By applying the image enhancement algorithm to every pixel simultaneously, the following output image can be
 * obtained:
 * ...............
 * ...............
 * ...............
 * ...............
 * .....##.##.....
 * ....#..#.#.....
 * ....##.#..#....
 * ....####..#....
 * .....#..##.....
 * ......##..#....
 * .......#.#.....
 * ...............
 * ...............
 * ...............
 * ...............
 *
 * Through further advances in imaging technology, the above output image can also be used as an input image! This
 * allows it to be enhanced a second time:
 * ...............
 * ...............
 * ...............
 * ..........#....
 * ....#..#.#.....
 * ...#.#...###...
 * ...#...##.#....
 * ...#.....#.#...
 * ....#.#####....
 * .....#.#####...
 * ......##.##....
 * .......###.....
 * ...............
 * ...............
 * ...............
 *
 * Truly incredible - now the small details are really starting to come through. After enhancing the original input
 * image twice, 35 pixels are lit.
 * Start with the original input image and apply the image enhancement algorithm twice, being careful to account for
 * the infinite size of the images. How many pixels are lit in the resulting image?
 *
 * --- Part Two ---
 * You still can't quite make out the details in the image. Maybe you just didn't enhance it enough.
 * If you enhance the starting input image in the above example a total of 50 times, 3351 pixels are lit in the final
 * output image.
 * Start again with the original input image and apply the image enhancement algorithm 50 times. How many pixels are
 * lit in the resulting image?
 *
 */
class ImageEnhancement(
  // The "enhancement algorithm", which should be 512 long, stored by index.
  val algorithm: List<Boolean>,
  // All the points in the finite box of the image. Values are true for on, false for off.
  val finitePoints: Map<Point, Boolean>,
  // What all the "points at infinity" are doing; true for on, false for off. Each "point at infinity" is neighbored by
  // only other points at infinity, and not ones in our finite box. As we advance generations, our box will get larger.
  val pointAtInfinity: Boolean
) {
  constructor(input: String) : this(
    algorithm = input.substringBefore("\n\n").trim().toCharArray().map { it == '#' },
    finitePoints = input.substringAfter("\n\n")
      .trim()
      .lines()
      .flatMapIndexed { y, line ->
        line.toCharArray().mapIndexed { x, c -> Point(x, y) to (c == '#') }
      }
      .toMap(),
    pointAtInfinity = false
  )

  fun next(): ImageEnhancement {
    val minX = finitePoints.minOf { it.key.x }
    val maxX = finitePoints.maxOf { it.key.x }
    val minY = finitePoints.minOf { it.key.y }
    val maxY = finitePoints.maxOf { it.key.y }

    // We're going to expand the bounding box by 1 in each direction and draw all those into our new finite box.
    val newXRange = (minX - 1)..(maxX + 1)
    val newYRange = (minY - 1)..(maxY + 1)

    val newPoints = newYRange.flatMap { y ->
      newXRange.map { x -> Point(x, y) to nextValueOf(Point(x, y)) }
    }.toMap()

    // The point at infinity is surrounded by others like itself, so it either becomes algorithm[0] or algorithm[511].
    val newPointAtInfinity = if (pointAtInfinity) algorithm[511] else algorithm[0]

    return ImageEnhancement(algorithm, newPoints, newPointAtInfinity)
  }

  private fun valueOf(point: Point): Boolean = finitePoints[point] ?: pointAtInfinity

  private fun nextValueOf(point: Point): Boolean {
    val neighbors = (point.eightNeighbors + point).sortedWith(
      Comparator.comparing<Point, Int> { it.y }
        .thenComparing(Comparator.comparing { it.x })
    )

    val index = neighbors.map { this.valueOf(it) }
      .joinToString("") { value -> if (value) "1" else "0" }
      .toInt(radix = 2)

    return algorithm[index]
  }

  fun next(steps: Int): ImageEnhancement = next(steps, ImageEnhancement::next)

  fun count(): Int = if (pointAtInfinity) {
    throw IllegalStateException("Infinity many points are on")
  } else {
    finitePoints.count { (_, isOn) -> isOn }
  }

  override fun toString(): String {
    val minX = finitePoints.minOf { it.key.x }
    val maxX = finitePoints.maxOf { it.key.x }
    val minY = finitePoints.minOf { it.key.y }
    val maxY = finitePoints.maxOf { it.key.y }

    return (minY..maxY).joinToString("\n") { y ->
      (minX..maxX).joinToString("") { x -> if (valueOf(Point(x, y))) "#" else "." }
    }
  }
}

fun main() {
  val image = File("src/main/kotlin/advent/year2021/day20/input.txt")
    .readText()
    .trim()
    .let(::ImageEnhancement)

  println(image.next(2).count())
  println(image.next(50).count())
}