package advent.year2025.day6

import advent.meta.readInput

/**
 * --- Day 6: Trash Compactor ---
 * After helping the Elves in the kitchen, you were taking a break and helping them re-enact a movie scene when you
 * over-enthusiastically jumped into the garbage chute!
 * A brief fall later, you find yourself in a garbage smasher. Unfortunately, the door's been magnetically sealed.
 * As you try to find a way out, you are approached by a family of cephalopods! They're pretty sure they can get the
 * door open, but it will take some time. While you wait, they're curious if you can help the youngest cephalopod with
 * her math homework.
 * Cephalopod math doesn't look that different from normal math. The math worksheet (your puzzle input) consists of a
 * list of problems; each problem has a group of numbers that need to be either added (+) or multiplied (*) together.
 * However, the problems are arranged a little strangely; they seem to be presented next to each other in a very long
 * horizontal list. For example:
 * 123 328  51 64
 *  45 64  387 23
 *   6 98  215 314
 * *   +   *   +
 *
 * Each problem's numbers are arranged vertically; at the bottom of the problem is the symbol for the operation that
 * needs to be performed. Problems are separated by a full column of only spaces. The left/right alignment of numbers
 * within each problem can be ignored.
 * So, this worksheet contains four problems:
 *
 * 123 * 45 * 6 = 33210
 * 328 + 64 + 98 = 490
 * 51 * 387 * 215 = 4243455
 * 64 + 23 + 314 = 401
 *
 * To check their work, cephalopod students are given the grand total of adding together all of the answers to the
 * individual problems. In this worksheet, the grand total is 33210 + 490 + 4243455 + 401 = 4277556.
 * Of course, the actual worksheet is much wider. You'll need to make sure to unroll it completely so that you can read
 * the problems clearly.
 * Solve the problems on the math worksheet. What is the grand total found by adding together all of the answers to the
 * individual problems?
 *
 * --- Part Two ---
 * The big cephalopods come back to check on how things are going. When they see that your grand total doesn't match
 * the one expected by the worksheet, they realize they forgot to explain how to read cephalopod math.
 * Cephalopod math is written right-to-left in columns. Each number is given in its own column, with the most
 * significant digit at the top and the least significant digit at the bottom. (Problems are still separated with a
 * column consisting only of spaces, and the symbol at the bottom of the problem is still the operator to use.)
 * Here's the example worksheet again:
 * 123 328  51 64
 *  45 64  387 23
 *   6 98  215 314
 * *   +   *   +
 *
 * Reading the problems right-to-left one column at a time, the problems are now quite different:
 *
 * The rightmost problem is 4 + 431 + 623 = 1058
 * The second problem from the right is 175 * 581 * 32 = 3253600
 * The third problem from the right is 8 + 248 + 369 = 625
 * Finally, the leftmost problem is 356 * 24 * 1 = 8544
 *
 * Now, the grand total is 1058 + 3253600 + 625 + 8544 = 3263827.
 * Solve the problems on the math worksheet again. What is the grand total found by adding together all of the answers
 * to the individual problems?
 *
 */
class MathWorksheet(numberLines: List<String>, val operators: List<(Long, Long) -> Long>) {
  companion object {
    fun of(input: String): MathWorksheet {
      val lines = input.lines().filterNot { it.isBlank() }

      val numberLines = lines.dropLast(1)
      val operators: List<(Long, Long) -> Long> = lines.last()
        .trim()
        .split("\\s".toRegex())
        .filter { it.isNotBlank() }
        .map {
          when (it) {
            "*" -> Long::times
            "+" -> Long::plus
            else -> throw IllegalArgumentException("Unrecognized operator $it")
          }
        }
      return MathWorksheet(numberLines, operators)
    }
  }

  private fun lineToNumbers(line: String) = line.trim()
    .split("\\s".toRegex())
    .filterNot { it.isBlank() }
    .map(String::toInt)

  /**
   * This is a list of the lines, each line containing its numbers.
   */
  private val numbersReadHorizontally: List<List<Int>> = numberLines.map(::lineToNumbers)

  /**
   * This is a list of the numbers contained within each problem.
   */
  private val numbersReadVertically: List<List<Int>> = numberLines.rotateLeft()
    .joinToString("\n")
    .split("\n\\s*\n".toRegex())
    .map { group ->
      group.split("\n").map { it.trim().toInt() }
    }

  fun grandTotal(): Long = numbersReadHorizontally.first().indices.sumOf { i ->
    val values = numbersReadHorizontally.map { it[i].toLong() }
    values.reduce(operators[i])
  }

  fun verticalTotal() = numbersReadVertically.mapIndexed { i, numbers ->
    numbers.map(Int::toLong).reduce(operators[operators.size - 1 - i])
  }.sum()
}

fun List<String>.rotateLeft(): List<String> = this.first().indices.reversed().map { i ->
  this.joinToString(separator = "") { it[i].toString() }
}

fun main() {
  val worksheet = MathWorksheet.of(readInput())

  println(worksheet.grandTotal())
  println(worksheet.verticalTotal())
}