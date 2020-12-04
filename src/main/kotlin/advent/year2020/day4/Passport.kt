package advent.year2020.day4

import java.io.File

data class Passport(val fields: Map<String, String>) {
  companion object {
    // Map of required fields to the test for that field.
    val requiredFields: Map<String, (String) -> Boolean> = mapOf(
            "byr" to { it.inRange(1920..2002) },
            "iyr" to { it.inRange(2010..2020) },
            "eyr" to { it.inRange(2020..2030) },
            "hgt" to {
              when {
                it.endsWith("cm") -> it.substringBeforeLast("cm").inRange(150..193)
                it.endsWith("in") -> it.substringBeforeLast("in").inRange(59..76)
                else -> false
              }
            },
            "hcl" to { "#[0-9a-f]{6}".toRegex().matches(it) },
            "ecl" to { it in setOf("amb", "blu", "brn", "gry", "grn", "hzl", "oth") },
            "pid" to { "[0-9]{9}".toRegex().matches(it) })

    private fun String.inRange(range: IntRange): Boolean {
      val num = this.toIntOrNull()
      return num != null && num in range
    }

    fun parse(record: String): Passport = record.split("\\s+".toRegex())
            .filter { it.isNotBlank() }
            .map { it.toPair() }
            .toMap()
            .let(::Passport)

    fun parseBatch(input: String): List<Passport> = input.split("\n\n")
            .map(Passport::parse)

    private fun String.toPair(): Pair<String, String> {
      val colonIndex = this.indexOf(":")
      return this.substring(0, colonIndex) to this.substring(colonIndex + 1)
    }
  }

  val hasValidKeys = fields.keys.containsAll(requiredFields.keys)

  val isValid = requiredFields.all {
    val value = fields[it.key]
    value != null && it.value(value)
  }
}

fun main() {
  val input = File("src/main/kotlin/advent/year2020/day4/input.txt")
          .readText()

  val passports = Passport.parseBatch(input)

  println(passports.count { it.hasValidKeys })
  println(passports.count { it.isValid })
}