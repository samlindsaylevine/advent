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
  // which is equivalent to NOT (A AND B AND C) AND D.
  // We can think of "NOT (A AND B AND C)" as "a pit is visible" and "D" as "the landing space is safe".
  fun walkSafely() = execute(
          // Since T starts false, this puts A into T.
          "OR A T",
          // This ANDs B onto T...
          "AND B T",
          // AND C...
          "AND C T",
          // And then puts NOT (A AND B AND C) into J.
          "NOT T J",
          // Finally ending with NOT (A AND B AND C) AND D in J.
          "AND D J",
          "WALK"
  )

  // If we try to use the original walking program, we die on a situation
  //
  // .................
  // .................
  // ..@..............
  // #####.#.#...#.###
  //    ABCDEFGH
  // where we try to jump onto the first island, and then can't jump again to make it onto H.
  //
  // That suggests we need an AND H in there as well! We don't want to jump to D, be unable to re-jump, and then die.
  // This survives this case for us, but then we die on
  //
  // .................
  // .................
  // ..@..............
  // #####...####.####
  //      ABCDEFGH
  //
  // because now at our first possible opportunity to clear A-C, we refuse to jump because now H is a pit.
  // That suggests we don't always want our "AND H" safety logic - we will only need it if _E_ is a pit, i.e., false.
  // So, we should be willing to jump if E or H is true,
  //
  // i.e., we want NOT (A AND B AND C) AND D AND (E OR H)
  //
  // which we can think of as "a pit is visible in our jump range, and the landing site is safe, and either we can walk
  // away from the landing site, or else jump again safely".


  fun runSafely() = execute(
          "OR A T",
          "AND B T",
          "AND C T",
          "NOT T J",
          "AND D J",
          // Now that we've moved not-T into J, we can reuse J by putting E OR H in there.
          "OR E T",
          "OR H T",
          "AND T J",
          "RUN"
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
  println(droid.runSafely())
}