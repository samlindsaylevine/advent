package advent.year2021.day25

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SeaCucumbersTest {

  private val example = """
    v...>>.vv>
    .vv>>.vv..
    >>.>v>...v
    >>v>>.>.v.
    v>v.vv.v..
    >.>>..v...
    .vv..>.>v.
    v.v..>>v.v
    ....v..v.>
  """.trimIndent().let(::SeaCucumbers)

  @Test
  fun `timeToStop -- example -- 58`() {
    assertThat(example.timeToStop()).isEqualTo(58)
  }
}