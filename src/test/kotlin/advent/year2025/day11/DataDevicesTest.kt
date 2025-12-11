package advent.year2025.day11

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DataDevicesTest {

  @Test
  fun `countPathsTo -- reference input -- 5 paths to out`() {
    val input = """
      aaa: you hhh
      you: bbb ccc
      bbb: ddd eee
      ccc: ddd eee fff
      ddd: ggg
      eee: out
      fff: out
      ggg: out
      hhh: ccc fff iii
      iii: out
    """.trimIndent()
    val devices = DataDevices(input)

    val count = devices.countPaths(origin = "you", target = "out")

    assertThat(count).isEqualTo(5)
  }

  @Test
  fun `countServerPaths -- reference input -- 2 paths`() {
    val input = """
      svr: aaa bbb
      aaa: fft
      fft: ccc
      bbb: tty
      tty: ccc
      ccc: ddd eee
      ddd: hub
      hub: fff
      eee: dac
      dac: fff
      fff: ggg hhh
      ggg: out
      hhh: out
    """.trimIndent()
    val devices = DataDevices(input)

    val count = devices.countServerPaths()

    assertThat(count).isEqualTo(2L)
  }
}