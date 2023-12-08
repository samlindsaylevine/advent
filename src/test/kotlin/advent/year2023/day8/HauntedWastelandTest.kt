package advent.year2023.day8

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class HauntedWastelandTest {

  @Test
  fun `steps to ZZZ -- reference input -- 2 steps`() {
    val input = """
      RL

      AAA = (BBB, CCC)
      BBB = (DDD, EEE)
      CCC = (ZZZ, GGG)
      DDD = (DDD, DDD)
      EEE = (EEE, EEE)
      GGG = (GGG, GGG)
      ZZZ = (ZZZ, ZZZ)
    """.trimIndent()

    val wasteland = HauntedWasteland(input)
    val steps = wasteland.steps(from = "AAA", to = "ZZZ")

    assertThat(steps).isEqualTo(2)
  }

  @Test
  fun `steps to ZZZ -- reference input with cycling instructions -- 6 steps`() {
    val input = """
      LLR

      AAA = (BBB, BBB)
      BBB = (AAA, ZZZ)
      ZZZ = (ZZZ, ZZZ)
    """.trimIndent()

    val wasteland = HauntedWasteland(input)
    val steps = wasteland.steps(from = "AAA", to = "ZZZ")

    assertThat(steps).isEqualTo(6)
  }

  @Test
  fun `ghost steps -- reference input -- 6 steps`() {
    val input = """
      LR

      11A = (11B, XXX)
      11B = (XXX, 11Z)
      11Z = (11B, XXX)
      22A = (22B, XXX)
      22B = (22C, 22C)
      22C = (22Z, 22Z)
      22Z = (22B, 22B)
      XXX = (XXX, XXX)
    """.trimIndent()

    val wasteland = HauntedWasteland(input)
    val steps = wasteland.ghostSteps()

    assertThat(steps).isEqualTo(6)
  }
}