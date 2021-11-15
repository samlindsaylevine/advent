package advent.year2018.day5

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ReactivePolymerTest {

    @Test
    fun `reduced -- abBA -- reduces to nothing`() {
        val original = ReactivePolymer("abBA")

        val reduced = original.reduced()

        assertThat(reduced.formula).isEmpty()
    }

    @Test
    fun `reduced -- aabAAB -- same as original`() {
        val original = ReactivePolymer("aabAAB")

        val reduced = original.reduced()

        assertThat(reduced.formula).isEqualTo("aabAAB")
    }

    @Test
    fun `reduced -- dabAcCaCBAcCcaDA -- dabCBAcaDA`() {
        val original = ReactivePolymer("dabAcCaCBAcCcaDA")

        val reduced = original.reduced()

        assertThat(reduced.formula).isEqualTo("dabCBAcaDA")
    }

    @Test
    fun `bestImprovement -- dabAcCaCBAcCcaDA -- daDA`() {
        val original = ReactivePolymer("dabAcCaCBAcCcaDA")

        val bestImprovement = original.bestImprovement()

        assertThat(bestImprovement.formula).isEqualTo("daDA")
    }
}