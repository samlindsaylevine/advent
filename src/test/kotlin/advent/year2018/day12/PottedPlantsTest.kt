package advent.year2018.day12

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PottedPlantsTest {

    private val referenceInput = """
        initial state: #..#.#..##......###...###

        ...## => #
        ..#.. => #
        .#... => #
        .#.#. => #
        .#.## => #
        .##.. => #
        .#### => #
        #.#.# => #
        #.### => #
        ##.#. => #
        ##.## => #
        ###.. => #
        ###.# => #
        ####. => #
    """.trimIndent()

    @Test
    fun `sumOfPotNumbers -- reference input, 20 generations -- 325`() {
        val plants = PottedPlants.parse(referenceInput)

        val sum = plants.after(20).sumOfPotNumbers()

        assertThat(sum).isEqualTo(325)
    }
}