package advent.year2020.day23

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CrabCupsTest {

  @Test
  fun `10 moves -- reference input -- reference result`() {
    val cups = CrabCups("389125467")

    cups.next(10)
    println("Final: $cups")
    val labels = cups.labels()

    assertThat(labels).isEqualTo("92658374")
  }

  @Test
  fun `100 moves -- reference input -- reference result`() {
    val cups = CrabCups("389125467")

    cups.next(100)
    val labels = cups.labels()

    assertThat(labels).isEqualTo("67384529")
  }

  @Test
  fun `10_000_000 moves -- extended input -- reference label product`() {
    val cups = CrabCups.extended("389125467")

    cups.next(10_000_000)
    val product = cups.labelProduct()

    assertThat(product).isEqualTo(149245887792L)
  }
}