package advent.year2025

import advent.year2025.day10.FactoryMachine
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FactoryMachineTest {

  @Test
  fun `minimum light button presses -- first example -- 2`() {
    val machine = FactoryMachine.of("[.##.] (3) (1,3) (2) (2,3) (0,2) (0,1) {3,5,4,7}")

    val presses = machine.minimumButtonLightPresses()

    assertThat(presses).isEqualTo(2)
  }

  @Test
  fun `minimum light button presses -- second example -- 3`() {
    val machine = FactoryMachine.of("[...#.] (0,2,3,4) (2,3) (0,4) (0,1,2) (1,2,3,4) {7,5,12,7,2}")

    val presses = machine.minimumButtonLightPresses()

    assertThat(presses).isEqualTo(3)
  }

  @Test
  fun `minimum light button presses -- third example -- 2`() {
    val machine = FactoryMachine.of("[.###.#] (0,1,2,3,4) (0,3,4) (0,1,2,4,5) (1,2) {10,11,11,5,10,5}")

    val presses = machine.minimumButtonLightPresses()

    assertThat(presses).isEqualTo(2)
  }


  @Test
  fun `minimum joltage button presses -- first example -- 10`() {
    val machine = FactoryMachine.of("[.##.] (3) (1,3) (2) (2,3) (0,2) (0,1) {3,5,4,7}")

    val presses = machine.minimumButtonJoltagePresses()

    assertThat(presses).isEqualTo(10)
  }

  @Test
  fun `minimum joltage button presses -- second example -- 12`() {
    val machine = FactoryMachine.of("[...#.] (0,2,3,4) (2,3) (0,4) (0,1,2) (1,2,3,4) {7,5,12,7,2}")

    val presses = machine.minimumButtonJoltagePresses()

    assertThat(presses).isEqualTo(12)
  }

  @Test
  fun `minimum joltage button presses -- third example -- 11`() {
    val machine = FactoryMachine.of("[.###.#] (0,1,2,3,4) (0,3,4) (0,1,2,4,5) (1,2) {10,11,11,5,10,5}")

    val presses = machine.minimumButtonJoltagePresses()

    assertThat(presses).isEqualTo(11)
  }
}