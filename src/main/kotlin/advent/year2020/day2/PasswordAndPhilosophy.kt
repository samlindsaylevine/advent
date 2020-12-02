package advent.year2020.day2

import java.io.File

class PasswordAndPhilosophy(val password: String, val letter: String, val min: Int, val max: Int) {
  companion object {
    private val REGEX = """(\d+)-(\d+) (\w+): (\w+)""".toPattern()

    fun parse(input: String): PasswordAndPhilosophy {
      val match = REGEX.matcher(input)

      if (!match.matches()) throw IllegalArgumentException("Malformed input $input")

      return PasswordAndPhilosophy(match.group(4),
              match.group(3),
              match.group(1).toInt(),
              match.group(2).toInt())
    }
  }

  val isValidAsCount = password.toCharArray().count { it.toString() == letter } in min..max

  private fun String.letterAt(oneBasedIndex: Int) = this.toCharArray()[oneBasedIndex - 1].toString()

  val isValidAsPosition = (password.letterAt(min) == letter) xor (password.letterAt(max) == letter)
}

fun main() {
  val input = File("src/main/kotlin/advent/year2020/day2/input.txt")
          .readText()
          .trim()
          .lines()
          .map(PasswordAndPhilosophy.Companion::parse)

  println(input.count { it.isValidAsCount })
  println(input.count { it.isValidAsPosition })
}