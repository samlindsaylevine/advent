package advent.year2019.day3

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class IntersectingWireTest {

    @Test
    fun `closestIntersectionDistance -- R8,U5,L5,D3 and U7,R6,D4,L4 -- is 6`() {
        val firstWire = IntersectingWire("R8,U5,L5,D3")
        val secondWire = IntersectingWire("U7,R6,D4,L4")

        val distance = firstWire.closestIntersectionDistance(secondWire)

        assertThat(distance).isEqualTo(6)
    }

    @Test
    fun `closestIntersectionSteps -- R8,U5,L5,D3 and U7,R6,D4,L4 -- is 30`() {
        val firstWire = IntersectingWire("R8,U5,L5,D3")
        val secondWire = IntersectingWire("U7,R6,D4,L4")

        val distance = firstWire.closestIntersectionSteps(secondWire)

        assertThat(distance).isEqualTo(30)
    }

    @Test
    fun `closestIntersectionDistance -- second reference example -- is 159`() {
        val firstWire = IntersectingWire("R75,D30,R83,U83,L12,D49,R71,U7,L72")
        val secondWire = IntersectingWire("U62,R66,U55,R34,D71,R55,D58,R83")

        val distance = firstWire.closestIntersectionDistance(secondWire)

        assertThat(distance).isEqualTo(159)
    }

    @Test
    fun `closestIntersectionSteps -- second reference example -- is 610`() {
        val firstWire = IntersectingWire("R75,D30,R83,U83,L12,D49,R71,U7,L72")
        val secondWire = IntersectingWire("U62,R66,U55,R34,D71,R55,D58,R83")

        val distance = firstWire.closestIntersectionSteps(secondWire)

        assertThat(distance).isEqualTo(610)
    }

    @Test
    fun `closestIntersectionDistance -- third reference example -- is 135`() {
        val firstWire = IntersectingWire("R98,U47,R26,D63,R33,U87,L62,D20,R33,U53,R51")
        val secondWire = IntersectingWire("U98,R91,D20,R16,D67,R40,U7,R15,U6,R7")

        val distance = firstWire.closestIntersectionDistance(secondWire)

        assertThat(distance).isEqualTo(135)
    }

    @Test
    fun `closestIntersectionSteps -- third reference example -- is 410`() {
        val firstWire = IntersectingWire("R98,U47,R26,D63,R33,U87,L62,D20,R33,U53,R51")
        val secondWire = IntersectingWire("U98,R91,D20,R16,D67,R40,U7,R15,U6,R7")

        val distance = firstWire.closestIntersectionSteps(secondWire)

        assertThat(distance).isEqualTo(410)
    }
}