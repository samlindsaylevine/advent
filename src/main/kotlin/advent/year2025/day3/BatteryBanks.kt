package advent.year2025.day3

import advent.meta.readInput

class BatteryBanks(val banks: List<BatteryBank>) {
  constructor(input: String) : this(input.trim().lines().map(::BatteryBank))

  fun totalOutputJoltage() = banks.sumOf { it.largestJoltage() }
}

class BatteryBank(val batteries: List<Int>) {
  constructor(input: String) : this(
    input.split("")
      .filter { it.isNotEmpty() }
      .map(String::toInt))

  fun largestJoltage(): Int {
    val tensDigit = batteries.dropLast(1).max()
    val tensDigitPlace = batteries.indexOf(tensDigit)
    val remainingBatteries = batteries.drop(tensDigitPlace + 1)
    val onesDigit = remainingBatteries.max()
    return 10 * tensDigit + onesDigit
  }
}

fun main() {
  val banks = BatteryBanks(readInput())

  println(banks.totalOutputJoltage())
}