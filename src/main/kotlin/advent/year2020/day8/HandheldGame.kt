package advent.year2020.day8

import java.io.File

class HandheldGame(val instructions: List<HandheldGameInstruction>,
                   var accumulator: Int = 0,
                   var instructionPointer: Int = 0) {

  companion object {
    fun parse(input: String) = input.trim()
            .lines()
            .map(HandheldGameInstruction::parse)
            .let(::HandheldGame)
  }

  private val visitedInstructions = mutableSetOf<Int>()

  fun run(): RunResult {
    while (instructionPointer in instructions.indices) {
      if (visitedInstructions.contains(instructionPointer)) return RunResult.INFINITE_LOOP
      visitedInstructions.add(instructionPointer)
      val instruction = instructions[instructionPointer]
      accumulator = instruction.newAccumulatorValue(accumulator)
      instructionPointer += instruction.nextInstructionOffset()
    }
    return RunResult.SUCCESS
  }

  // Get the sequence of all possible "fixed" versions of this program, with a no-op or jump instruction flopped.
  fun mutations() = this.instructions.asSequence().mapIndexedNotNull { i, instruction ->
    when (instruction) {
      is NoOperation -> HandheldGame(this.instructions.updated(i, Jump(instruction.amount)))
      is Jump -> HandheldGame(this.instructions.updated(i, NoOperation(instruction.amount)))
      else -> null
    }
  }

  fun fixed() = this.mutations()
          .first { it.run() == RunResult.SUCCESS }
}

private fun <T> List<T>.updated(index: Int, element: T): List<T> = this.toMutableList()
        .apply { this[index] = element }

enum class RunResult {
  INFINITE_LOOP,
  SUCCESS
}

sealed class HandheldGameInstruction(val amount: Int) {
  companion object {
    private val REGEX = "(.*) (.*)".toRegex()

    fun parse(line: String): HandheldGameInstruction {
      val match = REGEX.matchEntire(line) ?: throw IllegalArgumentException("Unparseable instruction $line")

      val operation = match.groupValues[1]
      val argument = match.groupValues[2].toSignedInt()

      return when (operation) {
        "nop" -> NoOperation(argument)
        "acc" -> Accumulate(argument)
        "jmp" -> Jump(argument)
        else -> throw IllegalArgumentException("Unparseable instruction $line")
      }
    }

    private fun String.toSignedInt() = this.substringAfter("+").toInt()
  }

  open fun nextInstructionOffset(): Int = 1
  open fun newAccumulatorValue(oldValue: Int) = oldValue
}

class NoOperation(amount: Int) : HandheldGameInstruction(amount)

class Accumulate(amount: Int) : HandheldGameInstruction(amount) {
  override fun newAccumulatorValue(oldValue: Int) = oldValue + amount
}

class Jump(amount: Int) : HandheldGameInstruction(amount) {
  override fun nextInstructionOffset() = amount
}

fun main() {
  val input = File("src/main/kotlin/advent/year2020/day8/input.txt")
          .readText()

  val game = HandheldGame.parse(input)

  game.run()
  println(game.accumulator)
  println(game.fixed().accumulator)
}