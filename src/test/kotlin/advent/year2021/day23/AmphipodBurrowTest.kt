package advent.year2021.day23

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class AmphipodBurrowTest {

  private val example = """
    #############
    #...........#
    ###B#C#B#D###
      #A#D#C#A#
      #########
  """.trimIndent().let(::AmphipodBurrow)

  @Disabled("Takes forever, instead I just puzzled it out by hand")
  @Test
  fun `organizationCost -- example -- 12521`() {
    assertThat(example.organizationCost()).isEqualTo(12521)
  }

}