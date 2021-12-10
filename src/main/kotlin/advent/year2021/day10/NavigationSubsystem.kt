package advent.year2021.day10

import java.io.File

class NavigationSubsystem(val lines: List<String>) {
  companion object {
    fun validate(line: String) = validate(emptyList(), line)

    private fun validate(chunkOpeners: List<Char>, remainingLine: String): LineValidation = when {
      remainingLine.isEmpty() && chunkOpeners.isEmpty() -> ValidLine
      remainingLine.isEmpty() && chunkOpeners.isNotEmpty() -> IncompleteLine
      else -> {
        val nextChar = remainingLine.first()
        when {
          nextChar.isOpening() -> validate(chunkOpeners + nextChar, remainingLine.drop(1))
          nextChar.matchingOpener() == chunkOpeners.last() -> validate(chunkOpeners.dropLast(1), remainingLine.drop(1))
          else -> CorruptedLine(nextChar)
        }
      }
    }

    private fun Char.isOpening() = this in setOf('(', '[', '{', '<')
    private fun Char.matchingOpener(): Char = when (this) {
      ')' -> '('
      ']' -> '['
      '}' -> '{'
      '>' -> '<'
      else -> throw IllegalArgumentException("Unrecognized closing character $this")
    }
  }

  fun corruptedSyntaxErrorScore() = lines.map(::validate)
    .filterIsInstance<CorruptedLine>()
    .sumOf { it.score }
}

sealed class LineValidation
class CorruptedLine(val firstIllegalCharacter: Char) : LineValidation() {
  val score: Int = when (firstIllegalCharacter) {
    ')' -> 3
    ']' -> 57
    '}' -> 1197
    '>' -> 25137
    else -> throw IllegalArgumentException("Unrecognized closing character $firstIllegalCharacter")
  }
}

object ValidLine : LineValidation()
object IncompleteLine : LineValidation()

fun main() {
  val navigation = File("src/main/kotlin/advent/year2021/day10/input.txt")
    .readLines()
    .let(::NavigationSubsystem)

  println(navigation.corruptedSyntaxErrorScore())
}