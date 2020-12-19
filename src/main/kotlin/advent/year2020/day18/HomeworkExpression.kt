package advent.year2020.day18

import java.io.File
import java.util.*

class HomeworkExpression(input: String) {

  private val rpn = tokenize(input).toReversePolishNotation()

  private fun tokenize(input: String): List<String> {
    val regex = """(\d+|\+|\*|-|\(|\))""".toRegex()
    return regex.findAll(input).map { it.groupValues[1] }.toList()
  }

  val value by lazy { rpn.evaluateReversePolishNotation() }
}

/**
 * Convert a list of arithmetic tokens (including parentheses) into reverse polish notation (i.e., operators as
 * postfixes) without parentheses.
 *
 * Using Djikstra's shunting-yard algorithm as per https://en.wikipedia.org/wiki/Shunting-yard_algorithm
 */
fun List<String>.toReversePolishNotation(): List<String> {
  val output = mutableListOf<String>()
  val operatorStack = Stack<String>()

  this.forEach { token ->
    when {
      token.isNumeric() -> output.add(token)
      token == "(" -> operatorStack.add(token)
      token == ")" -> {
        while (operatorStack.isNotEmpty() && operatorStack.peek() != "(") {
          output.add(operatorStack.pop())
        }
        if (operatorStack.isNotEmpty() && operatorStack.peek() == "(") {
          operatorStack.pop()
        }
        if (operatorStack.isNotEmpty() && operatorStack.peek() != "(") {
          output.add(operatorStack.pop())
        }
      }
      else -> {
        // Precedence calculation would happen here but our operators have the same precedence.
        while (operatorStack.isNotEmpty() && operatorStack.peek() != "(") {
          output.add(operatorStack.pop())
        }
        operatorStack.add(token)
      }
    }
  }

  while (!operatorStack.isEmpty()) {
    output.add(operatorStack.pop())
  }

  return output
}

private fun String.isNumeric() = this.toLongOrNull()?.let { true } ?: false

/**
 * Evaluate a list of arithmetic tokens in reverse polish notation; i.e., postfix order.
 */
fun List<String>.evaluateReversePolishNotation(): Long {
  val numberStack = Stack<Long>()

  this.forEach {
    when (it) {
      "+" -> numberStack.add(numberStack.pop() + numberStack.pop())
      "*" -> numberStack.add(numberStack.pop() * numberStack.pop())
      "-" -> numberStack.add(-numberStack.pop() + numberStack.pop())
      else -> numberStack.add(it.toLong())
    }
  }

  if (numberStack.size != 1) throw IllegalArgumentException("Unbalanced expression $this; stack $numberStack")

  return numberStack.pop()
}

fun main() {
  val input = File("src/main/kotlin/advent/year2020/day18/input.txt")
          .readText()
          .trim()

  val homework = input.lines().map(::HomeworkExpression)

  println(homework.sumOf { it.value })
}