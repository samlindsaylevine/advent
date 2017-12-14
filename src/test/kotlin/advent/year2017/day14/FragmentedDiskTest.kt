package advent.year2017.day14

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FragmentedDiskTest {

    @Test
    fun `usedSquares -- reference input -- reference output`() {
        val disk = FragmentedDisk(key = "flqrgnkx")

        val used = disk.usedSquares()

        assertThat(used).isEqualTo(8108)
    }
}