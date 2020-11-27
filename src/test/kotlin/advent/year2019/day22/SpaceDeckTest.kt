package advent.year2019.day22

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class SpaceDeckTest {

  @Test
  fun `dealIntoNewStack -- reference example -- reverses`() {
    val result = DealIntoNewStack(SpaceDeck(10))

    assertThat(result.cards).containsExactly(9, 8, 7, 6, 5, 4, 3, 2, 1, 0)
  }

  @Test
  fun `cut -- reference example, 3 cards -- reference output`() {
    val result = Cut(3)(SpaceDeck(10))

    assertThat(result.cards).containsExactly(3, 4, 5, 6, 7, 8, 9, 0, 1, 2)
  }

  @Test
  fun `cut -- reference example, -4 cards -- reference output`() {
    val result = Cut(-4)(SpaceDeck(10))

    assertThat(result.cards).containsExactly(6, 7, 8, 9, 0, 1, 2, 3, 4, 5)
  }

  @Test
  fun `dealWithIncrement -- reference example -- reference output`() {
    val result = DealWithIncrement(3)(SpaceDeck(10))

    assertThat(result.cards).containsExactly(0, 7, 4, 1, 8, 5, 2, 9, 6, 3)
  }

  @Test
  fun `shuffle -- reference example one -- reference output`() {
    val result = listOf(
            "deal with increment 7",
            "deal into new stack",
            "deal into new stack"
    ).shuffle(SpaceDeck(10))

    assertThat(result.cards).containsExactly(0, 3, 6, 9, 2, 5, 8, 1, 4, 7)
  }

  @Test
  fun `shuffle -- reference example two -- reference output`() {
    val result = listOf(
            "cut 6",
            "deal with increment 7",
            "deal into new stack"
    ).shuffle(SpaceDeck(10))

    assertThat(result.cards).containsExactly(3, 0, 7, 4, 1, 8, 5, 2, 9, 6)
  }

  @Test
  fun `shuffle -- reference example three -- reference output`() {
    val result = listOf(
            "deal with increment 7",
            "deal with increment 9",
            "cut -2"
    ).shuffle(SpaceDeck(10))

    assertThat(result.cards).containsExactly(6, 3, 0, 7, 4, 1, 8, 5, 2, 9)
  }

  @Test
  fun `shuffle -- reference example four -- reference output`() {
    val result = listOf(
            "deal into new stack",
            "cut -2",
            "deal with increment 7",
            "cut 8",
            "cut -4",
            "deal with increment 7",
            "cut 3",
            "deal with increment 9",
            "deal with increment 3",
            "cut -1"
    ).shuffle(SpaceDeck(10))

    assertThat(result.cards).containsExactly(9, 2, 5, 8, 1, 4, 7, 0, 3, 6)
  }

  /**
   * Don't have much of an example to unit test originalPosition of the whole instruction set with, except our answer
   * for part 1!
   */
  @Test
  fun `originalPosition -- puzzle input, part 1 answer -- 2019`() {
    val result = puzzleInput().originalPosition(4485, numCards = 10007)

    assertThat(result).isEqualTo(2019)
  }

  @ParameterizedTest(name = "originalPosition -- cut, position {0} -- started at {1}")
  @CsvSource("0, 3",
          "1, 4",
          "2, 5",
          "3, 6",
          "4, 7",
          "5, 8",
          "6, 9",
          "7, 0",
          "8, 1",
          "9, 2")
  fun `originalPosition -- cut -- reference values`(finalPosition: Long, expected: Long) {
    val result = Cut(3).originalPosition(finalPosition, numCards = 10)

    assertThat(result).isEqualTo(expected)
  }

  @ParameterizedTest(name = "originalPosition -- negative cut, position {0} -- started at {1}")
  @CsvSource("0, 6",
          "1, 7",
          "2, 8",
          "3, 9",
          "4, 0",
          "5, 1",
          "6, 2",
          "7, 3",
          "8, 4",
          "9, 5")
  fun `originalPosition -- negative cut -- reference values`(finalPosition: Long, expected: Long) {
    val result = Cut(-4).originalPosition(finalPosition, numCards = 10)

    assertThat(result).isEqualTo(expected)
  }

  @ParameterizedTest(name = "originalPosition -- deal into new stack, position {0} -- started at {1}")
  @CsvSource("0, 9",
          "1, 8",
          "2, 7",
          "3, 6",
          "4, 5",
          "5, 4",
          "6, 3",
          "7, 2",
          "8, 1",
          "9, 0")
  fun `originalPosition -- deal into new stack -- reference values`(finalPosition: Long, expected: Long) {
    val result = DealIntoNewStack.originalPosition(finalPosition, numCards = 10)

    assertThat(result).isEqualTo(expected)
  }

  @ParameterizedTest(name = "originalPosition -- deal into new stack, position {0} -- started at {1}")
  @CsvSource("0, 0",
          "1, 7",
          "2, 4",
          "3, 1",
          "4, 8",
          "5, 5",
          "6, 2",
          "7, 9",
          "8, 6",
          "9, 3")
  fun `originalPosition -- deal with increment -- reference values`(finalPosition: Long, expected: Long) {
    val result = DealWithIncrement(3).originalPosition(finalPosition, numCards = 10)

    assertThat(result).isEqualTo(expected)
  }

  @Test
  fun `multiplicative inverse -- sample input -- really is the inverse`() {
    val actual = multiplicativeInverse(53, 10007)

    println(actual)

    assertThat((actual * 53) % 10007).isEqualTo(1)
  }

  @ParameterizedTest(name = "exp mod -- {0}^{1} mod {2} -- {3}")
  @CsvSource("2, 4, 100, 16",
          "2, 4, 10, 6",
          "2, 3, 100, 8",
          "2, 3, 5, 3",
          "3, 6, 1000, 729",
          "3, 6, 10, 9")
  fun `exp mod -- sample input -- gives expected output`(x: Long, n: Long, m: Long, expected: Long) {
    val actual = expMod(x, n, m)

    assertThat(actual).isEqualTo(expected)
  }

  private fun List<String>.shuffle(startingDeck: SpaceDeck) = this.toInstructions().shuffle(startingDeck)
}