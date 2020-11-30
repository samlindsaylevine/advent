package advent.year2019.day25

import advent.year2019.day13.parseIntcodeFromFile
import advent.year2019.day5.IntcodeComputer
import java.util.*
import java.util.concurrent.LinkedBlockingQueue

class StarshipTextAdventure(val program: List<Long>) {

  private val inputBuffer = LinkedBlockingQueue<Long>()

  private val scanner = Scanner(System.`in`).useDelimiter("\n")

  fun execute() {
    IntcodeComputer().execute(program, this::input, this::output)
  }

  private fun output(output: Long) = print(output.toChar())

  private tailrec fun input(): Long {
    val existing = inputBuffer.poll()

    if (existing != null) return existing

    val line = scanner.next()
    inputBuffer.addAll(line.toCharArray().map { it.toLong() } + 10L)
    return input()
  }
}

fun main() {
  val program = parseIntcodeFromFile("src/main/kotlin/advent/year2019/day25/input.txt")

  val adventure = StarshipTextAdventure(program)

  adventure.execute()

  // Note --
  // Playing the game let me through when I held
  // - space law space brochure
  // - astrolabe
  // - antenna
  // - weather machine
  //
  // Just did it via trial and error. (Drop something whenever I was too heavy; pick up something
  // when I was too late.)
}