package advent.year2020.day10

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class JoltageAdaptersTest {

  @Test
  fun `differencesOfSize -- reference input, 1 jolt -- 7 differences`() {
    val adapters = JoltageAdapters("""
      16
      10
      15
      5
      1
      11
      7
      19
      6
      12
      4
    """.trimIndent())

    val result = adapters.differencesOfSize(1)

    assertThat(result).isEqualTo(7)
  }

  @Test
  fun `differencesOfSize -- reference input, 3 jolts -- 5 differences`() {
    val adapters = JoltageAdapters("""
      16
      10
      15
      5
      1
      11
      7
      19
      6
      12
      4
    """.trimIndent())

    val result = adapters.differencesOfSize(3)

    assertThat(result).isEqualTo(5)
  }

  @Test
  fun `differencesOfSize -- larger reference input, 1 jolt -- 22 differences`() {
    val adapters = JoltageAdapters("""
      28
      33
      18
      42
      31
      14
      46
      20
      48
      47
      24
      23
      49
      45
      19
      38
      39
      11
      1
      32
      25
      35
      8
      17
      7
      9
      4
      2
      34
      10
      3
    """.trimIndent())

    val result = adapters.differencesOfSize(1)

    assertThat(result).isEqualTo(22)
  }

  @Test
  fun `differencesOfSize -- larger reference input, 3 jolts -- 10 differences`() {
    val adapters = JoltageAdapters("""
      28
      33
      18
      42
      31
      14
      46
      20
      48
      47
      24
      23
      49
      45
      19
      38
      39
      11
      1
      32
      25
      35
      8
      17
      7
      9
      4
      2
      34
      10
      3
    """.trimIndent())

    val result = adapters.differencesOfSize(3)

    assertThat(result).isEqualTo(10)
  }
}