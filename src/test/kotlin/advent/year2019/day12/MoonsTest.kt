package advent.year2019.day12

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MoonsTest {

    @Test
    fun `allPairs -- list -- gives all pairs`() {
        val input = listOf(1, 2, 3, 4)

        val allPairs = input.allPairs()

        assertThat(allPairs).containsExactlyInAnyOrder(
                1 to 2,
                1 to 3,
                2 to 3,
                1 to 4,
                2 to 4,
                3 to 4
        )
    }

    @Test
    fun `advance -- reference example, 1 step -- reference result`() {
        val moons = Moons(listOf(
                Moon(-1, 0, 2),
                Moon(2, -10, -7),
                Moon(4, -8, 8),
                Moon(3, 5, -1)
        ))
        moons.advance()

        assertThat(moons.get()).containsExactly(
                Moon(position = ThreeDVector(2, -1, 1), velocity = ThreeDVector(3, -1, -1)),
                Moon(position = ThreeDVector(3, -7, -4), velocity = ThreeDVector(1, 3, 3)),
                Moon(position = ThreeDVector(1, -7, 5), velocity = ThreeDVector(-3, 1, -3)),
                Moon(position = ThreeDVector(2, 2, 0), velocity = ThreeDVector(-1, -3, 1))
        )
    }

    @Test
    fun `advance -- reference example, 10 steps -- reference result`() {
        val moons = Moons(listOf(
                Moon(-1, 0, 2),
                Moon(2, -10, -7),
                Moon(4, -8, 8),
                Moon(3, 5, -1)
        ))
        repeat(10) { moons.advance() }

        assertThat(moons.get()).containsExactly(
                Moon(position = ThreeDVector(2, 1, -3), velocity = ThreeDVector(-3, -2, 1)),
                Moon(position = ThreeDVector(1, -8, 0), velocity = ThreeDVector(-1, 1, 3)),
                Moon(position = ThreeDVector(3, -6, 1), velocity = ThreeDVector(3, 2, -3)),
                Moon(position = ThreeDVector(2, 0, 4), velocity = ThreeDVector(1, -1, -1))
        )
    }

    @Test
    fun `totalEnergy -- reference example -- reference energy`() {
        val moons = Moons(listOf(
                Moon(-1, 0, 2),
                Moon(2, -10, -7),
                Moon(4, -8, 8),
                Moon(3, 5, -1)
        ))
        repeat(10) { moons.advance() }

        val energy = moons.totalEnergy()

        assertThat(energy).isEqualTo(179)
    }

    @Test
    fun `stepsToRepeat -- reference example -- 2772`() {
        val moons = Moons(listOf(
                Moon(-1, 0, 2),
                Moon(2, -10, -7),
                Moon(4, -8, 8),
                Moon(3, 5, -1)
        ))

        val steps = moons.stepsToRepeat()

        assertThat(steps).isEqualTo(2772)
    }

    @Test
    fun `stepsToRepeat -- second example -- 4686774924`() {
        val moons = Moons(listOf(
                Moon(-8, -10, 0),
                Moon(5, 5, 10),
                Moon(2, -7, 3),
                Moon(9, -8, -3)
        ))

        val steps = moons.stepsToRepeat()

        assertThat(steps).isEqualTo(4686774924L)
    }
}