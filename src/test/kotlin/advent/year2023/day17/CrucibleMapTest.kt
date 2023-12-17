package advent.year2023.day17

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CrucibleMapTest {

  @Test
  fun `least heat loss -- reference input -- 102`() {
    val input = """
      2413432311323
      3215453535623
      3255245654254
      3446585845452
      4546657867536
      1438598798454
      4457876987766
      3637877979653
      4654967986887
      4564679986453
      1224686865563
      2546548887735
      4322674655533
    """.trimIndent()
    val map = CrucibleMap(input)

    val loss = map.leastHeatLoss()

    assertThat(loss).isEqualTo(102)
  }

  @Test
  fun `least heat loss -- reference input, ultra crucible -- 94`() {
    val input = """
      2413432311323
      3215453535623
      3255245654254
      3446585845452
      4546657867536
      1438598798454
      4457876987766
      3637877979653
      4654967986887
      4564679986453
      1224686865563
      2546548887735
      4322674655533
    """.trimIndent()
    val map = CrucibleMap(input)

    val loss = map.leastHeatLoss(CrucibleType.ultra)

    assertThat(loss).isEqualTo(94)
  }

  @Test
  fun `least heat loss -- unfortunate example, ultra crucible -- 71`() {
    val input = """
      111111111111
      999999999991
      999999999991
      999999999991
      999999999991
    """.trimIndent()
    val map = CrucibleMap(input)

    val loss = map.leastHeatLoss(CrucibleType.ultra)

    assertThat(loss).isEqualTo(71)
  }
}