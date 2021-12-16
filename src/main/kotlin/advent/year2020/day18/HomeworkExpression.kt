package advent.year2020.day18

import java.io.File
import java.util.*

/**
 * --- Day 18: Operation Order ---
 * As you look out the window and notice a heavily-forested continent slowly appear over the horizon, you are
 * interrupted by the child sitting next to you. They're curious if you could help them with their math homework.
 * Unfortunately, it seems like this "math" follows different rules than you remember.
 * The homework (your puzzle input) consists of a series of expressions that consist of addition (+), multiplication
 * (*), and parentheses ((...)). Just like normal math, parentheses indicate that the expression inside must be
 * evaluated before it can be used by the surrounding expression. Addition still finds the sum of the numbers on both
 * sides of the operator, and multiplication still finds the product.
 * However, the rules of operator precedence have changed. Rather than evaluating multiplication before addition, the
 * operators have the same precedence, and are evaluated left-to-right regardless of the order in which they appear.
 * For example, the steps to evaluate the expression 1 + 2 * 3 + 4 * 5 + 6 are as follows:
 * 1 + 2 * 3 + 4 * 5 + 6
 *   3   * 3 + 4 * 5 + 6
 *       9   + 4 * 5 + 6
 *          13   * 5 + 6
 *              65   + 6
 *                  71
 * 
 * Parentheses can override this order; for example, here is what happens if parentheses are added to form 1 + (2 * 3)
 * + (4 * (5 + 6)):
 * 1 + (2 * 3) + (4 * (5 + 6))
 * 1 +    6    + (4 * (5 + 6))
 *      7      + (4 * (5 + 6))
 *      7      + (4 *   11   )
 *      7      +     44
 *             51
 * 
 * Here are a few more examples:
 * 
 * 2 * 3 + (4 * 5) becomes 26.
 * 5 + (8 * 3 + 9 + 3 * 4 * 3) becomes 437.
 * 5 * 9 * (7 * 3 * 3 + 9 * 3 + (8 + 6 * 4)) becomes 12240.
 * ((2 + 4 * 9) * (6 + 9 * 8 + 6) + 6) + 2 + 4 * 2 becomes 13632.
 * 
 * Before you can help with the homework, you need to understand it yourself. Evaluate the expression on each line of
 * the homework; what is the sum of the resulting values?
 * 
 * --- Part Two ---
 * You manage to answer the child's questions and they finish part 1 of their homework, but get stuck when they reach
 * the next section: advanced math.
 * Now, addition and multiplication have different precedence levels, but they're not the ones you're familiar with.
 * Instead, addition is evaluated before multiplication.
 * For example, the steps to evaluate the expression 1 + 2 * 3 + 4 * 5 + 6 are now as follows:
 * 1 + 2 * 3 + 4 * 5 + 6
 *   3   * 3 + 4 * 5 + 6
 *   3   *   7   * 5 + 6
 *   3   *   7   *  11
 *      21       *  11
 *          231
 * 
 * Here are the other examples from above:
 * 
 * 1 + (2 * 3) + (4 * (5 + 6)) still becomes 51.
 * 2 * 3 + (4 * 5) becomes 46.
 * 5 + (8 * 3 + 9 + 3 * 4 * 3) becomes 1445.
 * 5 * 9 * (7 * 3 * 3 + 9 * 3 + (8 + 6 * 4)) becomes 669060.
 * ((2 + 4 * 9) * (6 + 9 * 8 + 6) + 6) + 2 + 4 * 2 becomes 23340.
 * 
 * What do you get if you add up the results of evaluating the homework problems using these new rules?
 * 
 */
class HomeworkExpression(input: String) {

  private fun tokenize(input: String): List<String> {
    val regex = """(\d+|\+|\*|-|\(|\))""".toRegex()
    return regex.findAll(input).map { it.groupValues[1] }.toList()
  }

  val valueWithoutPrecedence by lazy {
    tokenize(input)
            .toReversePolishNotation()
            .evaluateReversePolishNotation()
  }

  val valueWithPrecedence by lazy {
    tokenize(input)
            .toReversePolishNotation(operatorPrecedence = listOf("+", "*"))
            .evaluateReversePolishNotation()
  }
}

/**
 * Convert a list of arithmetic tokens (including parentheses) into reverse polish notation (i.e., operators as
 * postfixes) without parentheses.
 *
 * Using Djikstra's shunting-yard algorithm as per https://en.wikipedia.org/wiki/Shunting-yard_algorithm
 *
 * We closely follow the algorithm in detail there, but we do not have function tokens, only operator tokens.
 *
 * We assume that when operators are of equal precedence, they are left-associative.
 *
 * @param operatorPrecedence A list of operator precedence -- earlier in the list = higher precedence. Operators not in
 * the list are treated as highest precedence.
 */
fun List<String>.toReversePolishNotation(operatorPrecedence: List<String> = emptyList()): List<String> {

  fun String.precedence() = operatorPrecedence.indexOf(this)

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
      }
      else -> {
        while (operatorStack.isNotEmpty() && operatorStack.peek() != "(" &&
                operatorStack.peek().precedence() <= token.precedence()) {
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

  println(homework.sumOf { it.valueWithoutPrecedence })
  println(homework.sumOf { it.valueWithPrecedence })
}