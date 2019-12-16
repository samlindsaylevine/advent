package advent.year2018.day24

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ImmuneSystemSimulationTest {

    @Test
    fun `runEntireCombat -- reference example -- 5216 units left`() {
        val input = """
            Immune System:
            17 units each with 5390 hit points (weak to radiation, bludgeoning) with an attack that does 4507 fire damage at initiative 2
            989 units each with 1274 hit points (immune to fire; weak to bludgeoning, slashing) with an attack that does 25 slashing damage at initiative 3

            Infection:
            801 units each with 4706 hit points (weak to radiation) with an attack that does 116 bludgeoning damage at initiative 1
            4485 units each with 2961 hit points (immune to radiation; weak to fire, cold) with an attack that does 12 slashing damage at initiative 4
        """.trimIndent()
        val simulation = ImmuneSystemSimulation.parse(input)

        val result = simulation.runEntireCombat()

        assertThat(result.winner).isEqualTo(ArmySide.INFECTION)
        assertThat(result.unitsLeft).isEqualTo(5216)
    }

    @Test
    fun `runEntireCombat -- boosted example -- immune wins with 51 units left`() {
        val input = """
            Immune System:
            17 units each with 5390 hit points (weak to radiation, bludgeoning) with an attack that does 4507 fire damage at initiative 2
            989 units each with 1274 hit points (immune to fire; weak to bludgeoning, slashing) with an attack that does 25 slashing damage at initiative 3

            Infection:
            801 units each with 4706 hit points (weak to radiation) with an attack that does 116 bludgeoning damage at initiative 1
            4485 units each with 2961 hit points (immune to radiation; weak to fire, cold) with an attack that does 12 slashing damage at initiative 4
        """.trimIndent()
        val simulation = ImmuneSystemSimulation.parse(input)

        val result = simulation.boosted(1570).runEntireCombat()

        assertThat(result.winner).isEqualTo(ArmySide.IMMUNE_SYSTEM)
        assertThat(result.unitsLeft).isEqualTo(51)
    }
}