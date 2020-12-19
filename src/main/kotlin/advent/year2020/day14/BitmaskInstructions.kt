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
        is SetMask -> mask = Bitmask(it.mask)
        is SetMemory -> memory.values[it.address] = mask.apply(it.value)
      }
    }

    return memory
  }

  fun executeQuantum(): BitmaskMemory {
    val memory = BitmaskMemory()
    var mask = Bitmask("X".repeat(36))

    instructions.forEach { instruction ->
      when (instruction) {
        is SetMask -> mask = Bitmask(instruction.mask)
        is SetMemory -> mask.applyQuantum(instruction.address).possibleValues().forEach { address ->
          memory.values[address] = instruction.value
        }
      }
    }

    return memory
  }
}

data class Bitmask(val mask: String) {
  fun apply(number: Long): Long = number.let(::applyOnes).let(::applyZeroes)

  private fun applyOnes(number: Long) = mask.replace('X', '0')
          .toLong(radix = 2)
          .or(number)

  private fun applyZeroes(number: Long) = mask.replace('X', '1')
          .toLong(radix = 2)
          .and(number)

  fun applyQuantum(number: Long): Bitmask {
    val otherAsString = number.toString(radix = 2)
            .padStart(length = this.mask.length, padChar = '0')

    return mask.toCharArray().zip(otherAsString.toCharArray())
            .map {
              when (it.first) {
                '1' -> '1'
                'X' -> 'X'
                else -> it.second
              }
            }
            .joinToString(separator = "")
            .let(::Bitmask)
  }

  fun possibleValues(): List<Long> = when {
    mask.count { it == 'X' } == 0 -> listOf(mask.dropWhile { it == '0' }.toLong(radix = 2))
    else -> listOf('0', '1').flatMap { Bitmask(mask.replaceFirst('X', it)).possibleValues() }
  }
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

fun String.substringBetween(openingDelimiter: String, closingDelimiter: String) =
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
  println(instructions.executeQuantum().sum())
}