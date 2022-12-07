package advent.utils

fun String.digits() = this.split("").filter { it.isNotEmpty() }.map { it.toInt() }

fun String.findAllNumbers(): List<Int> {
  val regex = Regex("\\d+")
  val allMatches = regex.findAll(this)
  return allMatches.map { it.value.toInt() }.toList()
}

/**
 * Finds a number within a string; 0 for the first number, 1 for the next, etc.
 *
 * If there aren't that many numbers throws IndexOutOfBoundsException.
 */
fun String.findNumber(position: Int): Int = this.findAllNumbers()[position]

fun String.isNumber() = Regex("\\d+").matches(this)