package advent.year2019.day22

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SpaceDeckTest {

  @Test
  fun `dealIntoNewStack -- reference example -- reverses`() {
    val result = SpaceDeck(10).dealIntoNewStack()

    assertThat(result.cards).containsExactly(9, 8, 7, 6, 5, 4, 3, 2, 1, 0)
  }

  @Test
  fun `cut -- reference example, 3 cards -- reference output`() {
    val result = SpaceDeck(10).cut(3)

    assertThat(result.cards).containsExactly(3, 4, 5, 6, 7, 8, 9, 0, 1, 2)
  }

  @Test
  fun `cut -- reference example, -4 cards -- reference output`() {
    val result = SpaceDeck(10).cut(-4)

    assertThat(result.cards).containsExactly(6, 7, 8, 9, 0, 1, 2, 3, 4, 5)
  }

  @Test
  fun `dealWithIncrement -- reference example -- reference output`() {
    val result = SpaceDeck(10).dealWithIncrement(3)

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
}