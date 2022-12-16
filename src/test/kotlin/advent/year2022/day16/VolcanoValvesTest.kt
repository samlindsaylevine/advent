package advent.year2022.day16

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class VolcanoValvesTest {

  private val input = """
    Valve AA has flow rate=0; tunnels lead to valves DD, II, BB
    Valve BB has flow rate=13; tunnels lead to valves CC, AA
    Valve CC has flow rate=2; tunnels lead to valves DD, BB
    Valve DD has flow rate=20; tunnels lead to valves CC, AA, EE
    Valve EE has flow rate=3; tunnels lead to valves FF, DD
    Valve FF has flow rate=0; tunnels lead to valves EE, GG
    Valve GG has flow rate=0; tunnels lead to valves FF, HH
    Valve HH has flow rate=22; tunnel leads to valve GG
    Valve II has flow rate=0; tunnels lead to valves AA, JJ
    Valve JJ has flow rate=21; tunnel leads to valve II
  """.trimIndent()

  @Test
  fun `maxPressureReleased -- reference input -- 1651`() {
    val valves = VolcanoValves(input)

    assertThat(valves.maxPressureReleased()).isEqualTo(1651)
  }

  @Test
  fun `maxPressureReleased -- reference input, with elephant -- 1707`() {
    val valves = VolcanoValves(input)

    assertThat(valves.maxPressureReleased(withElephant = true)).isEqualTo(1707)
  }
}