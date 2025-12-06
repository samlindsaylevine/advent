package advent.year2025.day5

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class KitchenDatabaseTest {

  @Test
  fun `countFresh -- reference input -- 3`() {
    val input = """
     3-5
     10-14
     16-20
     12-18

     1
     5
     8
     11
     17
     32
   """.trimIndent()
    val database = KitchenDatabase.of(input)

    val count = database.countFresh()

    assertThat(count).isEqualTo(3)
  }

  @Test
  fun `totalFresh -- reference input -- 14`() {
    val input = """
     3-5
     10-14
     16-20
     12-18

     1
     5
     8
     11
     17
     32
   """.trimIndent()
    val database = KitchenDatabase.of(input)

    val total = database.totalFresh()

    assertThat(total).isEqualTo(14L)
  }
}