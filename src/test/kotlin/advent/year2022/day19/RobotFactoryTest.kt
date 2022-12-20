package advent.year2022.day19

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RobotFactoryTest {

  private val input = """
    Blueprint 1: Each ore robot costs 4 ore. Each clay robot costs 2 ore. Each obsidian robot costs 3 ore and 14 clay. Each geode robot costs 2 ore and 7 obsidian.
    Blueprint 2: Each ore robot costs 2 ore. Each clay robot costs 3 ore. Each obsidian robot costs 3 ore and 8 clay. Each geode robot costs 3 ore and 12 obsidian.
  """.trimIndent()

  @Test
  fun `max producible -- blueprint 1 -- 9`() {
    val factory = RobotFactory(input)

    val blueprint = factory.blueprints.first { it.id == 1 }

    assertThat(blueprint.maxProducible()).isEqualTo(9)
  }

  @Test
  fun `max producible -- blueprint 2 -- 12`() {
    val factory = RobotFactory(input)

    val blueprint = factory.blueprints.first { it.id == 2 }

    assertThat(blueprint.maxProducible()).isEqualTo(12)
  }

  @Test
  fun `quality levels -- factory -- sum to 33`() {
    val factory = RobotFactory(input)

    val sum = factory.blueprints.sumOf { it.qualityLevel() }

    assertThat(sum).isEqualTo(33)
  }

  @Test
  fun `max producible -- blueprint 1, 32 minutes -- 24`() {
    val factory = RobotFactory(input)

    val blueprint = factory.blueprints.first { it.id == 1 }

    assertThat(blueprint.maxProducible(time = 32)).isEqualTo(56)
  }

  @Test
  fun `max producible -- blueprint 2, 32 minutes -- 24`() {
    val factory = RobotFactory(input)

    val blueprint = factory.blueprints.first { it.id == 2 }

    assertThat(blueprint.maxProducible(time = 32)).isEqualTo(62)
  }
}