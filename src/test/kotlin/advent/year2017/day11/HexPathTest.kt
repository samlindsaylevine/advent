package advent.year2017.day11

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.EnumSource

class HexPathTest {

    @ParameterizedTest(name = "endpoint distance -- {0} -- {1} steps away")
    @CsvSource(delimiter = '=', value = *[
    "ne,ne,ne = 3",
    "ne,ne,sw,sw = 0",
    "ne,ne,s,s = 2",
    "se,sw,se,sw,sw = 3"
    ])
    fun `endpoint distance -- reference input -- reference output`(input: String, expected: Int) {
        val path = HexPath(input)

        val distance = path.endpoint.distanceFromOrigin()

        assertThat(distance).isEqualTo(expected)
    }

    @ParameterizedTest(name = "endpoint distance -- {0} -- 1 step away")
    @EnumSource(HexDirection::class)
    fun `endpoint distance -- all adjacent hexes -- 1 step away`(direction: HexDirection) {
        val path = HexPath(direction.name)

        val distance = path.endpoint.distanceFromOrigin()

        assertThat(distance).isEqualTo(1)
    }

    @ParameterizedTest(name = "furthestDistance -- {0} -- {1} steps away")
    @CsvSource(delimiter = '=', value = *[
    "ne,ne,ne = 3",
    "ne,ne,sw,sw = 2",
    "ne,ne,s,s = 2",
    "se,sw,se,sw,sw = 3"
    ])
    fun `furthestDistance -- reference inputs -- hand calculated output`(input: String, expected: Int) {
        val path = HexPath(input)

        val distance = path.furthestDistance

        assertThat(distance).isEqualTo(expected)
    }
}