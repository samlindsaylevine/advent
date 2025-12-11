package advent.year2025.day11

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DataDeviceTest {

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

    val count = devices.countPathsTo("out")

    assertThat(count).isEqualTo(5)
  }
}