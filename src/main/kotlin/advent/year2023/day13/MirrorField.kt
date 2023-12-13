package advent.year2023.day13

import advent.utils.Point
import java.io.File
import kotlin.math.min

/**
 * --- Day 13: Point of Incidence ---
 * With your help, the hot springs team locates an appropriate spring which launches you neatly and precisely up to the
 * edge of Lava Island.
 * There's just one problem: you don't see any lava.
 * You do see a lot of ash and igneous rock; there are even what look like gray mountains scattered around. After a
 * while, you make your way to a nearby cluster of mountains only to discover that the valley between them is
 * completely full of large mirrors.  Most of the mirrors seem to be aligned in a consistent way; perhaps you should
 * head in that direction?
 * As you move through the valley of mirrors, you find that several of them have fallen from the large metal frames
 * keeping them in place. The mirrors are extremely flat and shiny, and many of the fallen mirrors have lodged into the
 * ash at strange angles. Because the terrain is all one color, it's hard to tell where it's safe to walk or where
 * you're about to run into a mirror.
 * You note down the patterns of ash (.) and rocks (#) that you see as you walk (your puzzle input); perhaps by
 * carefully analyzing these patterns, you can figure out where the mirrors are!
 * For example:
 * #.##..##.
 * ..#.##.#.
 * ##......#
 * ##......#
 * ..#.##.#.
 * ..##..##.
 * #.#.##.#.
 *
 * #...##..#
 * #....#..#
 * ..##..###
 * #####.##.
 * #####.##.
 * ..##..###
 * #....#..#
 *
 * To find the reflection in each pattern, you need to find a perfect reflection across either a horizontal line
 * between two rows or across a vertical line between two columns.
 * In the first pattern, the reflection is across a vertical line between two columns; arrows on each of the two
 * columns point at the line between the columns:
 * 123456789
 *     ><
 * #.##..##.
 * ..#.##.#.
 * ##......#
 * ##......#
 * ..#.##.#.
 * ..##..##.
 * #.#.##.#.
 *     ><
 * 123456789
 *
 * In this pattern, the line of reflection is the vertical line between columns 5 and 6. Because the vertical line is
 * not perfectly in the middle of the pattern, part of the pattern (column 1) has nowhere to reflect onto and can be
 * ignored; every other column has a reflected column within the pattern and must match exactly: column 2 matches
 * column 9, column 3 matches 8, 4 matches 7, and 5 matches 6.
 * The second pattern reflects across a horizontal line instead:
 * 1 #...##..# 1
 * 2 #....#..# 2
 * 3 ..##..### 3
 * 4v#####.##.v4
 * 5^#####.##.^5
 * 6 ..##..### 6
 * 7 #....#..# 7
 *
 * This pattern reflects across the horizontal line between rows 4 and 5. Row 1 would reflect with a hypothetical row
 * 8, but since that's not in the pattern, row 1 doesn't need to match anything. The remaining rows match: row 2
 * matches row 7, row 3 matches row 6, and row 4 matches row 5.
 * To summarize your pattern notes, add up the number of columns to the left of each vertical line of reflection; to
 * that, also add 100 multiplied by the number of rows above each horizontal line of reflection. In the above example,
 * the first pattern's vertical line has 5 columns to its left and the second pattern's horizontal line has 4 rows
 * above it, a total of 405.
 * Find the line of reflection in each of the patterns in your notes. What number do you get after summarizing all of
 * your notes?
 *
 * --- Part Two ---
 * You resume walking through the valley of mirrors and - SMACK! - run directly into one. Hopefully nobody was
 * watching, because that must have been pretty embarrassing.
 * Upon closer inspection, you discover that every mirror has exactly one smudge: exactly one . or # should be the
 * opposite type.
 * In each pattern, you'll need to locate and fix the smudge that causes a different reflection line to be valid. (The
 * old reflection line won't necessarily continue being valid after the smudge is fixed.)
 * Here's the above example again:
 * #.##..##.
 * ..#.##.#.
 * ##......#
 * ##......#
 * ..#.##.#.
 * ..##..##.
 * #.#.##.#.
 *
 * #...##..#
 * #....#..#
 * ..##..###
 * #####.##.
 * #####.##.
 * ..##..###
 * #....#..#
 *
 * The first pattern's smudge is in the top-left corner. If the top-left # were instead ., it would have a different,
 * horizontal line of reflection:
 * 1 ..##..##. 1
 * 2 ..#.##.#. 2
 * 3v##......#v3
 * 4^##......#^4
 * 5 ..#.##.#. 5
 * 6 ..##..##. 6
 * 7 #.#.##.#. 7
 *
 * With the smudge in the top-left corner repaired, a new horizontal line of reflection between rows 3 and 4 now
 * exists. Row 7 has no corresponding reflected row and can be ignored, but every other row matches exactly: row 1
 * matches row 6, row 2 matches row 5, and row 3 matches row 4.
 * In the second pattern, the smudge can be fixed by changing the fifth symbol on row 2 from . to #:
 * 1v#...##..#v1
 * 2^#...##..#^2
 * 3 ..##..### 3
 * 4 #####.##. 4
 * 5 #####.##. 5
 * 6 ..##..### 6
 * 7 #....#..# 7
 *
 * Now, the pattern has a different horizontal line of reflection between rows 1 and 2.
 * Summarize your notes as before, but instead use the new different reflection lines. In this example, the first
 * pattern's new horizontal line has 3 rows above it and the second pattern's new horizontal line has 1 row above it,
 * summarizing to the value 400.
 * In each pattern, fix the smudge and find the different line of reflection. What number do you get after summarizing
 * the new reflection line in each pattern in your notes?
 *
 */
class MirrorFields(private val fields: List<MirrorField>) {
  constructor(input: String) : this(input.trim().split("\n\n").map(::MirrorField))

  fun summarize() = fields.sumOf { it.value() }
  fun desmudged() = MirrorFields(fields.map { it.desmudged() })
}

class MirrorField(private val rocks: Set<Point>, val width: Int, val height: Int,
        // These values are used if this field is a desmudge; if so, it cannot have the same reflection lines as the
        // original. So, we store them as values that cannot be the result when calculating new reflection lines.
                  val ignoreReflectionX: Int? = null, val ignoreReflectionY: Int? = null) {
  constructor(input: String) : this(input.trim()
          .lines()
          .flatMapIndexed { y, line ->
            line.flatMapIndexed { x, c -> if (c == '#') listOf(Point(x, y)) else emptyList() }
          }
          .toSet(),
          input.trim().lines().maxOf { it.length },
          input.trim().lines().size)

  // Null if there is no horizontal line of reflection.
  // The value of Y is the value this is before: e.g., if this is after the first row, value is 1.
  val horizontalLineY: Int? by lazy {
    (1 until height).firstOrNull { y -> (y != ignoreReflectionY) && this.hasHorizontalLine(y) }
  }

  private fun hasHorizontalLine(y: Int): Boolean {
    val numRows = min(y, height - y)
    val above = rocks.filter { it.y in ((y - numRows) until y) }.toSet()
    val below = rocks.filter { it.y in (y until (y + numRows)) }.toSet()
    return above == below.map { it.reflectedVertically(y) }.toSet()
  }

  // The line is at (if you think about it in non-integer terms) acrossX - 0.5. The distance between this and that line
  // is thus acrossX - this.x - 0.5. Then we add that to the line to get 2 * acrossX - this.x - 1.
  private fun Point.reflectedHorizontally(acrossX: Int) = Point(2 * acrossX - this.x - 1, this.y)

  // The same logic as above for X.
  private fun Point.reflectedVertically(acrossY: Int) = Point(this.x, 2 * acrossY - this.y - 1)

  // Null if there is no vertical line of reflection.
  // The value of X is the value this is before: e.g., if this is after the first column, value is 1.
  val verticalLineX: Int? by lazy {
    (1 until width).firstOrNull { x -> (x != ignoreReflectionX) && this.hasVerticalLine(x) }
  }

  private fun hasVerticalLine(x: Int): Boolean {
    val numColumns = min(x, width - x)
    val left = rocks.filter { it.x in ((x - numColumns) until x) }.toSet()
    val right = rocks.filter { it.x in (x until (x + numColumns)) }.toSet()
    return left == right.map { it.reflectedHorizontally(x) }.toSet()
  }

  fun value() = (verticalLineX ?: 0) + 100 * (horizontalLineY ?: 0)

  private fun hasDifferentLinesFrom(other: MirrorField): Boolean {
    val (horizontal, vertical) = other.horizontalLineY to other.verticalLineX
    return (horizontal != null || vertical != null) && (horizontal to vertical) != (this.horizontalLineY to this.verticalLineX)
  }

  private fun allPoints(): Sequence<Point> = (0 until height).asSequence()
          .flatMap { y ->
            (0 until width).asSequence().map { x -> Point(x, y) }
          }

  private fun desmudgePossibilities(): Sequence<MirrorField> = allPoints()
          .map { point ->
            if (point in rocks) {
              MirrorField(rocks - point, width, height,
                      ignoreReflectionX = this.verticalLineX, ignoreReflectionY = this.horizontalLineY)
            } else {
              MirrorField(rocks + point, width, height,
                      ignoreReflectionX = this.verticalLineX, ignoreReflectionY = this.horizontalLineY)
            }
          }

  fun desmudged() = desmudgePossibilities().first { this.hasDifferentLinesFrom(it) }
}

fun main() {
  val input = File("src/main/kotlin/advent/year2023/day13/input.txt").readText().trim()
  val fields = MirrorFields(input)

  println(fields.summarize())
  println(fields.desmudged().summarize())
}