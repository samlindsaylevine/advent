package advent.year2022.day21

import advent.utils.Rational
import advent.utils.loadingCache
import java.io.File
import java.util.regex.Pattern

/**
 * --- Day 21: Monkey Math ---
 * The monkeys are back! You're worried they're going to try to steal your stuff again, but it seems like they're just
 * holding their ground and making various monkey noises at you.
 * Eventually, one of the elephants realizes you don't speak monkey and comes over to interpret. As it turns out, they
 * overheard you talking about trying to find the grove; they can show you a shortcut if you answer their riddle.
 * Each monkey is given a job: either to yell a specific number or to yell the result of a math operation. All of the
 * number-yelling monkeys know their number from the start; however, the math operation monkeys need to wait for two
 * other monkeys to yell a number, and those two other monkeys might also be waiting on other monkeys.
 * Your job is to work out the number the monkey named root will yell before the monkeys figure it out themselves.
 * For example:
 * root: pppw + sjmn
 * dbpl: 5
 * cczh: sllz + lgvd
 * zczc: 2
 * ptdq: humn - dvpt
 * dvpt: 3
 * lfqf: 4
 * humn: 5
 * ljgn: 2
 * sjmn: drzm * dbpl
 * sllz: 4
 * pppw: cczh / lfqf
 * lgvd: ljgn * ptdq
 * drzm: hmdt - zczc
 * hmdt: 32
 *
 * Each line contains the name of a monkey, a colon, and then the job of that monkey:
 *
 * A lone number means the monkey's job is simply to yell that number.
 * A job like aaaa + bbbb means the monkey waits for monkeys aaaa and bbbb to yell each of their numbers; the monkey
 * then yells the sum of those two numbers.
 * aaaa - bbbb means the monkey yells aaaa's number minus bbbb's number.
 * Job aaaa * bbbb will yell aaaa's number multiplied by bbbb's number.
 * Job aaaa / bbbb will yell aaaa's number divided by bbbb's number.
 *
 * So, in the above example, monkey drzm has to wait for monkeys hmdt and zczc to yell their numbers. Fortunately, both
 * hmdt and zczc have jobs that involve simply yelling a single number, so they do this immediately: 32 and 2. Monkey
 * drzm can then yell its number by finding 32 minus 2: 30.
 * Then, monkey sjmn has one of its numbers (30, from monkey drzm), and already has its other number, 5, from dbpl.
 * This allows it to yell its own number by finding 30 multiplied by 5: 150.
 * This process continues until root yells a number: 152.
 * However, your actual situation involves considerably more monkeys. What number will the monkey named root yell?
 *
 * --- Part Two ---
 * Due to some kind of monkey-elephant-human mistranslation, you seem to have misunderstood a few key details about the
 * riddle.
 * First, you got the wrong job for the monkey named root; specifically, you got the wrong math operation. The correct
 * operation for monkey root should be =, which means that it still listens for two numbers (from the same two monkeys
 * as before), but now checks that the two numbers match.
 * Second, you got the wrong monkey for the job starting with humn:. It isn't a monkey - it's you. Actually, you got
 * the job wrong, too: you need to figure out what number you need to yell so that root's equality check passes. (The
 * number that appears after humn: in your input is now irrelevant.)
 * In the above example, the number you need to yell to pass root's equality test is 301. (This causes root to get the
 * same number, 150, from both of its monkeys.)
 * What number do you yell to pass root's equality test?
 *
 */
class ArithmeticMonkeys(private val monkeys: Map<String, ArithmeticMonkey>) {
  constructor(input: String) : this(input.lines().associate {
    val (name, action) = it.split(": ")
    name to ArithmeticMonkey.parse(action)
  })

  private fun get(name: String) = monkeys[name] ?: throw NoSuchElementException("No monkey $name")

  private fun calculateValue(name: String) = get(name).invoke(this)
  private val valuesCache = loadingCache(::calculateValue)

  fun getValue(name: String): LinearExpression = valuesCache.get(name)

  fun withMe(): ArithmeticMonkeys {
    val root = this.get("root")
    val newRoot = if (root is TwoTermMonkey) {
      EquationSolvingMonkey(root.firstName, root.secondName)
    } else {
      throw IllegalStateException("Root monkey does not have two terms! $root")
    }
    val newMonkeys = monkeys.toMutableMap().apply {
      put("root", newRoot)
      put("humn", HumanMonkey)
    }
    return ArithmeticMonkeys(newMonkeys)
  }
}

sealed class ArithmeticMonkey {
  companion object {
    fun parse(action: String): ArithmeticMonkey = sequenceOf(
      ConstantMonkey::tryParse,
      SumMonkey::tryParse,
      DifferenceMonkey::tryParse,
      ProductMonkey::tryParse,
      QuotientMonkey::tryParse
    ).firstNotNullOfOrNull { it(action) }
      ?: throw IllegalArgumentException("Unparseable monkey action \"$action\"")
  }

  abstract operator fun invoke(monkeys: ArithmeticMonkeys): LinearExpression
}

/**
 * Represents a linear expression in one variable: a coefficient times that variable, plus a constant.
 *
 * The general case of this problem results in arbitrary polynomials in our human variable, that we have to
 * multiply, divide, and then find roots of!
 *
 * We're going to make some simplifying assumptions that our input (like the example) doesn't actually result in
 * a) any polynomials of degree 2 or higher, i.e., any cases where a value with the human variable in it is multiplied
 *    by another
 * b) any fractions with the human variable in the denominator
 *
 * We'll fail quickly with an exception if either of these assumptions is violated. These assumptions let our
 * implementation be MUCH simpler and only have to deal with these linear expressions.
 */
data class LinearExpression(val variableCoefficient: Rational, val constant: Rational) {
  operator fun plus(other: LinearExpression) =
    LinearExpression(this.variableCoefficient + other.variableCoefficient, this.constant + other.constant)

  operator fun minus(other: LinearExpression) =
    LinearExpression(this.variableCoefficient - other.variableCoefficient, this.constant - other.constant)

  operator fun times(other: LinearExpression) = when {
    this.variableCoefficient.isNonZero() && other.variableCoefficient.isNonZero() -> throw IllegalStateException("Uh-oh: trying to create a higher degree polynomial by multiplication! $this * $other")
    else -> LinearExpression(
      this.variableCoefficient * other.constant + other.variableCoefficient * this.constant,
      this.constant * other.constant
    )
  }

  operator fun div(other: LinearExpression) = when {
    other.variableCoefficient.isNonZero() -> throw IllegalStateException("Uh-oh: trying to divide by a non-constant expression! $this * $other")
    else -> LinearExpression(this.variableCoefficient / other.constant, this.constant / other.constant)
  }

  private fun Rational.isNonZero() = this != Rational.of(0, 1)

  fun toLong() = when {
    variableCoefficient.isNonZero() -> throw IllegalStateException("Non-constant expression $this")
    constant.denominator != 1L -> throw IllegalStateException("Non-integer expression $this")
    else -> constant.numerator
  }
}

data class ConstantMonkey(val constant: Long) : ArithmeticMonkey() {
  companion object {
    fun tryParse(action: String): ConstantMonkey? = "(\\d+)".toRegex()
      .matchEntire(action)
      ?.let { ConstantMonkey(it.groupValues.first().toLong()) }
  }

  override fun invoke(monkeys: ArithmeticMonkeys) = LinearExpression(Rational.of(0, 1), Rational.of(constant, 1))
}

abstract class TwoTermMonkey(val firstName: String, val secondName: String) : ArithmeticMonkey()

private fun <T : ArithmeticMonkey> parseTwoTerms(
  action: String,
  operator: String,
  constructor: (String, String) -> T
) =
  "(\\w+) ${Pattern.quote(operator)} (\\w+)".toRegex()
    .matchEntire(action)
    ?.let { match ->
      val (first, second) = match.destructured
      constructor(first, second)
    }

class SumMonkey(firstName: String, secondName: String) : TwoTermMonkey(firstName, secondName) {
  companion object {
    fun tryParse(action: String): SumMonkey? = parseTwoTerms(action, "+", ::SumMonkey)
  }

  override fun invoke(monkeys: ArithmeticMonkeys) = monkeys.getValue(firstName) + monkeys.getValue(secondName)
}

class DifferenceMonkey(firstName: String, secondName: String) : TwoTermMonkey(firstName, secondName) {
  companion object {
    fun tryParse(action: String): DifferenceMonkey? = parseTwoTerms(action, "-", ::DifferenceMonkey)
  }

  override fun invoke(monkeys: ArithmeticMonkeys) = monkeys.getValue(firstName) - monkeys.getValue(secondName)
}

class ProductMonkey(firstName: String, secondName: String) : TwoTermMonkey(firstName, secondName) {
  companion object {
    fun tryParse(action: String): ProductMonkey? = parseTwoTerms(action, "*", ::ProductMonkey)
  }

  override fun invoke(monkeys: ArithmeticMonkeys) = monkeys.getValue(firstName) * monkeys.getValue(secondName)
}

class QuotientMonkey(firstName: String, secondName: String) : TwoTermMonkey(firstName, secondName) {
  companion object {
    fun tryParse(action: String): QuotientMonkey? = parseTwoTerms(action, "/", ::QuotientMonkey)
  }

  override fun invoke(monkeys: ArithmeticMonkeys) = monkeys.getValue(firstName) / monkeys.getValue(secondName)
}

object HumanMonkey : ArithmeticMonkey() {
  override fun invoke(monkeys: ArithmeticMonkeys) = LinearExpression(Rational.of(1, 1), Rational.of(0, 1))
}

class EquationSolvingMonkey(firstName: String, secondName: String) : TwoTermMonkey(firstName, secondName) {
  // Returns the value that the variable needs to take on for the two sides to be equal (as a linear expression
  // whose variable coefficient will always be 0).
  override fun invoke(monkeys: ArithmeticMonkeys): LinearExpression {
    val subtracted = monkeys.getValue(firstName) - monkeys.getValue(secondName)
    // This expression must equal zero.
    val variable = (Rational.of(-1, 1) * subtracted.constant) / subtracted.variableCoefficient
    return LinearExpression(Rational.of(0, 1), variable)
  }
}

fun main() {
  val input = File("src/main/kotlin/advent/year2022/day21/input.txt").readText().trim()

  val monkeys = ArithmeticMonkeys(input)

  println(monkeys.getValue("root").toLong())
  println(monkeys.withMe().getValue("root").toLong())
}