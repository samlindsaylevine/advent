package advent.year2018.day22

import advent.utils.Point
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class CaveSystemTest {


    @ParameterizedTest(name = "reference values -- at ({0}, {1}) -- geologic index {2}, erosion {3}, type {4}")
    @CsvSource("0, 0, 0, 510, ROCKY",
            "1, 0, 16807, 17317, WET",
            "0, 1, 48271, 8415, ROCKY",
            "1, 1, 145722555, 1805, NARROW",
            "10, 10, 0, 510, ROCKY")
    fun `index, erosion level, region type -- reference inputs -- reference values`(x: Int,
                                                                                    y: Int,
                                                                                    expectedIndex: Int,
                                                                                    expectedErosionLevel: Int,
                                                                                    expectedRegionType: RegionType) {
        val cave = CaveSystem(510, Point(10, 10))

        val point = Point(x, y)
        assertThat(cave.geologicalIndex(point)).isEqualTo(expectedIndex)
        assertThat(cave.erosionLevel(point)).isEqualTo(expectedErosionLevel)
        assertThat(cave.regionType(point)).isEqualTo(expectedRegionType)
    }

    @Test
    fun `map -- reference cave -- reference map`() {
        val cave = CaveSystem(510, Point(10, 10))

        val map = cave.map(Point(15, 15))

        val expected = """
            M=.|=.|.|=.|=|=.
            .|=|=|||..|.=...
            .==|....||=..|==
            =.|....|.==.|==.
            =|..==...=.|==..
            =||.=.=||=|=..|=
            |.=.===|||..=..|
            |..==||=.|==|===
            .=..===..=|.|||.
            .======|||=|=.|=
            .===|=|===T===||
            =|||...|==..|=.|
            =.=|=.=..=.||==|
            ||=|=...|==.=|==
            |=.=||===.|||===
            ||.|==.|.|.||=||
        """.trimIndent()
        assertThat(map).isEqualTo(expected)
    }

    @Test
    fun `rescueTime -- reference cave -- is 45`() {
        val cave = CaveSystem(510, Point(10, 10))

        val rescueTime = cave.rescueTime()

        assertThat(rescueTime).isEqualTo(45)
    }
}