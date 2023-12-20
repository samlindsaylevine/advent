package advent.year2023.day20

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ModuleMachineTest {

  @Test
  fun `pulse product -- first example, after pushing button 1000 times -- 32000000`() {
    val input = """
      broadcaster -> a, b, c
      %a -> b
      %b -> c
      %c -> inv
      &inv -> a
    """.trimIndent()
    val machine = ModuleMachine.of(input)

    machine.pressButton(times = 1000)
    val product = machine.pulseSentProduct()

    assertThat(product).isEqualTo(32000000)
  }

  @Test
  fun `pulse product -- second example, after pushing button 1000 times -- 11687500`() {
    val input = """
      broadcaster -> a
      %a -> inv, con
      &inv -> b
      %b -> con
      &con -> output
    """.trimIndent()
    val machine = ModuleMachine.of(input)

    machine.pressButton(times = 1000)
    val product = machine.pulseSentProduct()

    assertThat(product).isEqualTo(11687500)
  }
}