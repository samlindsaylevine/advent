package advent.year2025.day6

import advent.meta.readInput

class MathWorksheet(val numbers: List<List<Int>>, val operators: List<(Long, Long) -> Long>) {
  companion object {
    private fun lineToNumbers(line: String) = line.trim()
      .split("\\s".toRegex())
      .filterNot { it.isBlank() }
      .map(String::toInt)

    fun of(input: String): MathWorksheet {
      val lines = input.lines().filterNot { it.isBlank() }
      val numberLines = lines.dropLast(1)

      val numbers = numberLines.map(::lineToNumbers)
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
      return MathWorksheet(numbers, operators)
    }
  }

  fun grandTotal(): Long = numbers.first().indices.sumOf { i ->
    val values = numbers.map { it[i].toLong() }
    values.reduce(operators[i])
  }
}

fun main() {
  val worksheet = MathWorksheet.of(readInput())

  println(worksheet.grandTotal())
}