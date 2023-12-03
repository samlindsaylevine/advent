package advent.year2023.day3

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class EngineSchematicTest {

    @Test
    fun `part number sum -- reference input -- 4361`() {
        val input = """
            467..114..
            ...*......
            ..35..633.
            ......#...
            617*......
            .....+.58.
            ..592.....
            ......755.
            ...${'$'}.*....
            .664.598..
        """.trimIndent()

        val schematic = EngineSchematic.of(input)
        val sum = schematic.partNumberSum()

        assertThat(sum).isEqualTo(4361)
    }

    @Test
    fun `gear ratio sum -- reference input -- 467835`() {
        val input = """
            467..114..
            ...*......
            ..35..633.
            ......#...
            617*......
            .....+.58.
            ..592.....
            ......755.
            ...${'$'}.*....
            .664.598..
        """.trimIndent()

        val schematic = EngineSchematic.of(input)
        val sum = schematic.gearRatioSum()

        assertThat(sum).isEqualTo(467835)
    }
}