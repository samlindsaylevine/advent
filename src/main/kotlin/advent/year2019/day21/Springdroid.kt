package advent.year2019.day21

import advent.year2019.day13.parseIntcodeFromFile
import advent.year2019.day5.IntcodeComputer
import advent.year2019.day5.asInput

class Springdroid(private val program: List<Long>) {

  fun execute(vararg instructions: String): SpringdroidResult {
    val input = instructions.joinToString(separator = "\n", postfix = "\n")
            .toCharArray()
            .map { it.toLong() }
            .asInput()

    return IntcodeComputer().execute(program, input).output.toResult()
  }


  // It seems like whenever the droid jumps, it lands in square 4, and then walks again.
  // So, we definitely only want to jump when square 4 (D) is true.
  // It seems like we probably want to jump whenever any of the other squares is false.
  // That means we want J to be (NOT A or NOT B or NOT C) AND D
  // which is equivalent to NOT (A AND B AND C) AND D
  fun walkSafely() = execute("OR A T",
          "AND B T",
          "AND C T",
          "NOT T J",
          "AND D J",
          "WALK"
  )
}

private fun List<Long>.toResult(): SpringdroidResult = when {
  this.last() > 128L -> HullDamage(this.last())
  else -> DeathReport(String(this.map { it.toChar() }.toCharArray()))
}

sealed class SpringdroidResult
data class HullDamage(val amount: Long) : SpringdroidResult()
data class DeathReport(val rendering: String) : SpringdroidResult()

fun main() {
  val program = parseIntcodeFromFile("src/main/kotlin/advent/year2019/day21/input.txt")

  val droid = Springdroid(program)

  println(droid.walkSafely())
}