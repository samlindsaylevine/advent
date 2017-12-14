package advent.year2017.day14

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class FragmentedDiskTest {


    @ParameterizedTest
    @CsvSource("0, 0000",
            "e, 1110",
            "a0c2017, 1010000011000010000000010111")
    fun `toBinaryString -- hex inputs -- to left-padded binary`(hex: String, expected: String) {
        val binary = FragmentedDisk.toBinaryString(hex)

        assertThat(binary).isEqualTo(expected)
    }

    @Test
    fun `usedSquares -- reference input -- reference output`() {
        val disk = FragmentedDisk(key = "flqrgnkx")

        val used = disk.usedSquares()

        assertThat(used).isEqualTo(8108)
    }

    @Test
    fun `regions -- reference input -- reference output`() {
        val disk = FragmentedDisk(key = "flqrgnkx")

        val regions = disk.regions()

        assertThat(regions).isEqualTo(1242)
    }
}