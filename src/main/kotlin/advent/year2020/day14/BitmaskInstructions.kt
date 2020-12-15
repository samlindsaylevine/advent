package advent.year2020.day14

import java.io.File

class BitmaskInstructions(input: String) {
  private val instructions: List<BitmaskInstruction> = input.trim()
          .lines()
          .map(BitmaskInstruction::parse)

  fun execute(): BitmaskMemory {
    val memory = BitmaskMemory()
    var mask = Bitmask("X".repeat(36))

    instructions.forEach {
      when (it) {
        is SetMask -> mask = Bitmask((it.mask))
        is SetMemory -> memory.values[it.address] = mask.apply(it.value)
      }
    }

    return memory
  }
}

class Bitmask(val mask: String) {
  fun apply(number: Long): Long = number.let(::applyOnes).let(::applyZeroes)

  private fun applyOnes(number: Long) = mask.replace('X', '0')
          .toLong(radix = 2)
          .or(number)

  private fun applyZeroes(number: Long) = mask.replace('X', '1')
          .toLong(radix = 2)
          .and(number)
}

sealed class BitmaskInstruction {
  companion object {
    fun parse(line: String): BitmaskInstruction = when {
      line.startsWith("mask = ") -> SetMask(line.substringAfter("mask = "))
      line.startsWith("mem[") -> SetMemory(address = line.substringBetween("[", "]").toLong(),
              value = line.substringAfter(" = ").toLong())
      else -> throw IllegalArgumentException("Unrecognized instruction $line")
    }
  }
}

private fun String.substringBetween(openingDelimiter: String, closingDelimiter: String) =
        this.substringAfter(openingDelimiter).substringBefore(closingDelimiter)

private class SetMask(val mask: String) : BitmaskInstruction()
private class SetMemory(val address: Long, val value: Long) : BitmaskInstruction()

class BitmaskMemory(val values: MutableMap<Long, Long> = mutableMapOf()) {
  fun sum() = values.values.sum()
}

fun main() {
  val input = File("src/main/kotlin/advent/year2020/day14/input.txt")
          .readText()

  val instructions = BitmaskInstructions(input)

  println(instructions.execute().sum())
}