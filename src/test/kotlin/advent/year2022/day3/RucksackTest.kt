package advent.year2022.day3

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class RucksackTest {

  @CsvSource(
    "vJrwpWtwJgWrhcsFMMfFFhFp, p",
    "jqHRNqRjqzjGDLGLrsFMfFZSrLrFZsSL, L",
    "PmmdzqPrVvPwwTWBwg, P",
    "wMqvLMZHhHMvwLHjbvcjnnSBnvTQFn, v",
    "ttgJtRGJQctTZtZT, t",
    "CrZsJsPPZsGzwwsLwLmpwMDw, s"
  )
  @ParameterizedTest
  fun `item in both -- reference inputs -- reference values`(input: String, expectedItem: Char) {
    val rucksack = Rucksack(input)

    assertThat(rucksack.itemInBoth).isEqualTo(expectedItem)
  }

  @CsvSource(
    "vJrwpWtwJgWrhcsFMMfFFhFp, 16",
    "jqHRNqRjqzjGDLGLrsFMfFZSrLrFZsSL, 38",
    "PmmdzqPrVvPwwTWBwg, 42",
    "wMqvLMZHhHMvwLHjbvcjnnSBnvTQFn, 22",
    "ttgJtRGJQctTZtZT, 20",
    "CrZsJsPPZsGzwwsLwLmpwMDw, 19"
  )
  @ParameterizedTest
  fun `priority -- reference inputs -- reference values`(input: String, expectedPriority: Int) {
    val rucksack = Rucksack(input)

    assertThat(rucksack.priority).isEqualTo(expectedPriority)
  }

  @Test
  fun `badge priority -- reference input -- 70`() {
    val input = """
      vJrwpWtwJgWrhcsFMMfFFhFp
      jqHRNqRjqzjGDLGLrsFMfFZSrLrFZsSL
      PmmdzqPrVvPwwTWBwg
      wMqvLMZHhHMvwLHjbvcjnnSBnvTQFn
      ttgJtRGJQctTZtZT
      CrZsJsPPZsGzwwsLwLmpwMDw
    """.trimIndent()
    val rucksacks = input.asRuckSacks()

    assertThat(rucksacks.badgePriority()).isEqualTo(70)
  }
}