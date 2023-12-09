package advent.utils

fun String.digits() = this.split("").filter { it.isNotEmpty() }.map { it.toInt() }

fun String.findAllNumbers(): List<Int> = findAllNumbers(String::toInt)
fun String.findAllLongs(): List<Long> = findAllNumbers(String::toLong)

private fun <T> String.findAllNumbers(transform: (String) -> T): List<T> {
  val regex = Regex("-?\\d+")
  val allMatches = regex.findAll(this)
  return allMatches.map { transform(it.value) }.toList()
}

/**
 * Finds a number within a string; 0 for the first number, 1 for the next, etc.
 *
 * If there aren't that many numbers throws IndexOutOfBoundsException.
 */
fun String.findNumber(position: Int): Int = this.findAllNumbers()[position]

fun String.isNumber() = Regex("\\d+").matches(this)