package advent.year2018.day13

import advent.year2018.day10.Coordinates
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MineCartStateTest {

    private val referenceInput = """
        /->-\
        |   |  /----\
        | /-+--+-\  |
        | | |  | v  |
        \-+-/  \-+--/
          \------/

    """.trimIndent()

    @Test
    fun `advancedToCrash -- reference input -- crash is at 7,3`() {
        val carts = MineCartState.parse(referenceInput)

        val crashed = carts.advancedToCrash()
        val crashLocations = crashed.collisionLocations

        assertThat(crashLocations).containsExactly(Coordinates(7, 3))
    }

    private val partTwoInput = """
        />-<\
        |   |
        | /<+-\
        | | | v
        \>+</ |
          |   ^
          \<->/
    """.trimIndent()

    @Test
    fun `advancedToOneCart -- reference input -- remaining cart is at 6,4`() {
        val carts = MineCartState.parse(partTwoInput)

        val final = carts.advancedToOneCart()

        assertThat(final.cartLocations()).containsExactly(Coordinates(6, 4))
    }


    @Test
    fun `next -- facing each other and adjacent -- crashes`() {
        val facingEachOther = "-><-"
        val carts = MineCartState.parse(facingEachOther)

        val next = carts.next()

        assertThat(next.cartLocations()).isEmpty()
    }

}