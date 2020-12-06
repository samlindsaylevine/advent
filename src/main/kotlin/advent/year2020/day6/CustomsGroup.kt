package advent.year2020.day6

import java.io.File

class CustomsGroups(val groups: List<CustomsGroup>) {
  constructor(input: String) : this(input.trim()
          .split("\n\n")
          .map(::CustomsGroup))
}

class CustomsGroup(val people: Set<CustomsPerson>) {
  constructor(input: String) : this(input.lines().map(::CustomsPerson).toSet())

  val distinctYesAnswers = people.map { it.yesAnswers }.reduce(Set<Char>::union)
  val overlappingYesAnswers = people.map { it.yesAnswers }.reduce(Set<Char>::intersect)
}

class CustomsPerson(val yesAnswers: Set<Char>) {
  constructor(line: String) : this(line.toCharArray().toSet())
}

fun main() {
  val input = File("src/main/kotlin/advent/year2020/day6/input.txt")
          .readText()

  val plane = CustomsGroups(input)

  println(plane.groups.sumOf { it.distinctYesAnswers.count() })
  println(plane.groups.sumOf { it.overlappingYesAnswers.count() })
}