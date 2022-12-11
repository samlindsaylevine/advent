package advent.year2022.day11

import java.io.File

class ThrowingMonkeys(
  val monkeys: List<ThrowingMonkey>
) {
  constructor(input: String, relaxed: Boolean = true) : this(input
    .split("\n\n")
    .map { ThrowingMonkey.parse(it, relaxed) })

  fun monkeyBusiness(): Long = monkeys.asSequence()
    .map { it.inspectedCount }
    .sortedDescending()
    .take(2)
    .map { it.toLong() }
    .reduce(Long::times)

  fun next() = monkeys.forEach { it.next(this) }

  fun next(steps: Int) = repeat(steps) { this.next() }
}

class ThrowingMonkey(
  val operation: MonkeyOperation,
  val modulus: Long,
  val trueTarget: Int,
  val falseTarget: Int,
  val items: MutableList<Long>,
  var inspectedCount: Int = 0,
  val relaxed: Boolean
) {
  companion object {
    fun parse(input: String, relaxed: Boolean): ThrowingMonkey {
      val lines = input.lines()
      val items = lines[1].substringAfter("items: ").split(", ").map { it.toLong() }
      val operation = MonkeyOperation.parse(lines[2].substringAfter("new = "))
      val modulus = lines[3].substringAfter("divisible by ").toLong()
      val trueTarget = lines[4].substringAfter("monkey ").toInt()
      val falseTarget = lines[5].substringAfter("monkey ").toInt()
      return ThrowingMonkey(
        operation = operation,
        modulus = modulus,
        trueTarget = trueTarget,
        falseTarget = falseTarget,
        items = items.toMutableList(),
        relaxed = relaxed
      )
    }
  }

  fun next(monkeys: ThrowingMonkeys) {
    inspectedCount += items.size
    items.forEach { item ->
      val postInspect = this.operation(item)
      val postBoredom = if (relaxed) postInspect / 3 else postInspect
      val targetIndex = if (postBoredom % modulus == 0L) trueTarget else falseTarget
      monkeys.monkeys[targetIndex].items.add(postBoredom)
    }
    items.clear()
  }
}

sealed class MonkeyOperation {
  companion object {
    fun parse(input: String) = when {
      input == "old * old" -> MonkeySquare
      input.startsWith("old * ") -> MonkeyMultiply(input.substringAfter("* ").toInt())
      input.startsWith("old + ") -> MonkeyAdd(input.substringAfter("+ ").toInt())
      else -> throw IllegalArgumentException("Unrecognized operation $input")
    }
  }

  abstract operator fun invoke(old: Long): Long
}

class MonkeyAdd(val amount: Int) : MonkeyOperation() {
  override fun invoke(old: Long) = old + amount
}

class MonkeyMultiply(val amount: Int) : MonkeyOperation() {
  override fun invoke(old: Long) = old * amount
}

object MonkeySquare : MonkeyOperation() {
  override fun invoke(old: Long) = old * old
}

fun main() {
  val input = File("src/main/kotlin/advent/year2022/day11/input.txt").readText().trim()

  val monkeys = ThrowingMonkeys(input)
  monkeys.next(20)
  println(monkeys.monkeyBusiness())
}