package advent.year2022.day21

import advent.utils.loadingCache
import java.io.File
import java.util.regex.Pattern

class ArithmeticMonkeys(private val monkeys: Map<String, ArithmeticMonkey>) {
  constructor(input: String) : this(input.lines().associate {
    val (name, action) = it.split(": ")
    name to ArithmeticMonkey.parse(action)
  })

  private fun get(name: String) = monkeys[name] ?: throw NoSuchElementException("No monkey $name")

  private fun calculateValue(name: String) = get(name).invoke(this)
  private val valuesCache = loadingCache(::calculateValue)

  fun getValue(name: String): Long = valuesCache.get(name)
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

  abstract operator fun invoke(monkeys: ArithmeticMonkeys): Long
}

data class ConstantMonkey(val constant: Long) : ArithmeticMonkey() {
  companion object {
    fun tryParse(action: String): ConstantMonkey? = "(\\d+)".toRegex()
      .matchEntire(action)
      ?.let { ConstantMonkey(it.groupValues.first().toLong()) }
  }

  override fun invoke(monkeys: ArithmeticMonkeys) = constant
}

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

data class SumMonkey(val firstName: String, val secondName: String) : ArithmeticMonkey() {
  companion object {
    fun tryParse(action: String): SumMonkey? = parseTwoTerms(action, "+", ::SumMonkey)
  }

  override fun invoke(monkeys: ArithmeticMonkeys) = monkeys.getValue(firstName) + monkeys.getValue(secondName)
}

data class DifferenceMonkey(val firstName: String, val secondName: String) : ArithmeticMonkey() {
  companion object {
    fun tryParse(action: String): DifferenceMonkey? = parseTwoTerms(action, "-", ::DifferenceMonkey)
  }

  override fun invoke(monkeys: ArithmeticMonkeys) = monkeys.getValue(firstName) - monkeys.getValue(secondName)
}

data class ProductMonkey(val firstName: String, val secondName: String) : ArithmeticMonkey() {
  companion object {
    fun tryParse(action: String): ProductMonkey? = parseTwoTerms(action, "*", ::ProductMonkey)
  }

  override fun invoke(monkeys: ArithmeticMonkeys) = monkeys.getValue(firstName) * monkeys.getValue(secondName)
}

data class QuotientMonkey(val firstName: String, val secondName: String) : ArithmeticMonkey() {
  companion object {
    fun tryParse(action: String): QuotientMonkey? = parseTwoTerms(action, "/", ::QuotientMonkey)
  }

  override fun invoke(monkeys: ArithmeticMonkeys) = monkeys.getValue(firstName) / monkeys.getValue(secondName)
}

fun main() {
  val input = File("src/main/kotlin/advent/year2022/day21/input.txt").readText().trim()

  val monkeys = ArithmeticMonkeys(input)

  println(monkeys.getValue("root"))
}