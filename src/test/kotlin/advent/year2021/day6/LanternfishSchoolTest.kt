package advent.year2021.day6

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class LanternfishSchoolTest {

  private val input = "3,4,3,1,2"

  @Test
  fun `size -- reference input, 18 days -- 26 fish`() {
    val school = LanternfishSchool(input)

    val size = school.next(18).size()

    assertThat(size).isEqualTo(26)
  }

  @Test
  fun `size -- reference input, 80 days -- 5934 fish`() {
    val school = LanternfishSchool(input)

    val size = school.next(80).size()

    assertThat(size).isEqualTo(5934)
  }

  @Test
  fun `size -- reference input, 256 days -- 26984457539 fish`() {
    val school = LanternfishSchool(input)

    val size = school.next(256).size()

    assertThat(size).isEqualTo(26984457539L)
  }
}