package advent.year2021.day6

import advent.utils.merge
import java.io.File

class LanternfishSchool(
  private val countByTimer: Map<Int, Long>,
  private val generationLength: Int = 7
) {
  constructor(input: String) : this(input.split(",")
    .map { it.toInt() }
    .groupingBy { it }
    .eachCount()
    .mapValues { it.value.toLong() })

  fun size() = countByTimer.entries.sumOf { it.value }

  fun next(): LanternfishSchool {
    val countedDown = countByTimer.map { (timer, count) -> timer - 1 to count }
    val (stillCounting, spawning) = countedDown.partition { it.first >= 0 }
    val numSpawning = spawning.sumOf { it.second }
    val newFish = if (numSpawning > 0) {
      mapOf(generationLength - 1 to numSpawning, generationLength + 1 to numSpawning)
    } else {
      emptyMap()
    }
    return LanternfishSchool(stillCounting.toMap().merge(newFish))
  }

  fun next(days: Int) = tailrecNext(this, days)
  private tailrec fun tailrecNext(school: LanternfishSchool, days: Int): LanternfishSchool =
    if (days == 0) school else tailrecNext(school.next(), days - 1)
}

fun main() {
  val school = File("src/main/kotlin/advent/year2021/day6/input.txt")
    .readText()
    .trim()
    .let(::LanternfishSchool)

  println(school.next(80).size())
  println(school.next(256).size())
}