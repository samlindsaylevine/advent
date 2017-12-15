package advent.year2017.day15

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DuelingGeneratorTest {

    @Test
    fun `sequence -- reference generator A -- has reference first 5 values`() {
        val generator = DuelingGenerator.a(65)

        val firstValues = generator.sequence.take(5).toList()

        assertThat(firstValues).containsExactly(1092455, 1181022009, 245556042, 1744312007, 1352636452)
    }

    @Test
    fun `sequence -- reference generator B -- has reference first 5 values`() {
        val generator = DuelingGenerator.b(8921)

        val firstValues = generator.sequence.take(5).toList()

        assertThat(firstValues).containsExactly(430625591, 1233683848, 1431495498, 137874439, 285222916)
    }

    @Test
    fun `lowest16Bits -- 245556042 -- 1110001101001010`() {
        val lowest16Bits = DuelingGenerator.lowest16Bits(245556042)

        assertThat(lowest16Bits.toString(2)).isEqualTo("1110001101001010")
    }

    @Test
    fun `matchCount -- 5 pairs of reference generators -- 1 match`() {
        val generatorA = DuelingGenerator.a(65)
        val generatorB = DuelingGenerator.b(8921)

        val matchCount = generatorA.matchCount(generatorB, 5)

        assertThat(matchCount).isEqualTo(1)
    }

    @Test
    fun `sequence -- picky generator A -- has reference first 5 values`() {
        val generator = DuelingGenerator.pickyA(65)

        val firstValues = generator.sequence.take(5).toList()

        assertThat(firstValues).containsExactly(1352636452, 1992081072, 530830436, 1980017072, 740335192)
    }

    @Test
    fun `sequence -- picky generator B -- has reference first 5 values`() {
        val generator = DuelingGenerator.pickyB(8921)

        val firstValues = generator.sequence.take(5).toList()

        assertThat(firstValues).containsExactly(1233683848, 862516352, 1159784568, 1616057672, 412269392)
    }

    @Test
    fun `matchCount -- 1055 steps of picky generators -- 0 matches`() {
        val generatorA = DuelingGenerator.pickyA(65)
        val generatorB = DuelingGenerator.pickyB(8921)

        val matchCount = generatorA.matchCount(generatorB, 1055)

        assertThat(matchCount).isEqualTo(0)
    }

    @Test
    fun `matchCount -- 1056 steps of picky generators -- 1 match`() {
        val generatorA = DuelingGenerator.pickyA(65)
        val generatorB = DuelingGenerator.pickyB(8921)

        val matchCount = generatorA.matchCount(generatorB, 1056)

        assertThat(matchCount).isEqualTo(1)
    }

    @Test
    fun `matchCount -- 5,000,000 steps of picky generators -- 309 matches`() {
        val generatorA = DuelingGenerator.pickyA(65)
        val generatorB = DuelingGenerator.pickyB(8921)

        val matchCount = generatorA.matchCount(generatorB, 5_000_000)

        assertThat(matchCount).isEqualTo(309)
    }
}