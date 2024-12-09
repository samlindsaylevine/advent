package advent.year2024.day9

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DiskMapTest {

    @Test
    fun `parsed -- reference input -- 00   111   2   333 44 5555 6666 777 888899`() {
        val input = """
            2333133121414131402
        """.trimIndent()
        val diskMap = DiskMap(input)

        val string = diskMap.toString()

        assertThat(string).isEqualTo("00...111...2...333.44.5555.6666.777.888899")
    }

    @Test
    fun `compacted -- reference input --0099811188827773336446555566`() {
        val input = """
            2333133121414131402
        """.trimIndent()
        val diskMap = DiskMap(input)

        val string = diskMap.compactedByBlock().toString()

        assertThat(string).isEqualTo("0099811188827773336446555566")
    }

    @Test
    fun `compacted checksum -- reference input -- 1928`() {
        val input = """
            2333133121414131402
        """.trimIndent()
        val diskMap = DiskMap(input)

        val checksum = diskMap.compactedByBlock().checksum()

        assertThat(checksum).isEqualTo(1928)
    }

    @Test
    fun `compacted by file -- reference input -- reference result`() {
        val input = """
            2333133121414131402
        """.trimIndent()
        val diskMap = DiskMap(input)

        val string = diskMap.compactedByFile().toString()

        assertThat(string).isEqualTo("00992111777.44.333....5555.6666.....8888..")
    }

    @Test
    fun `compacted by file checksum -- reference input -- 2858`() {
        val input = """
            2333133121414131402
        """.trimIndent()
        val diskMap = DiskMap(input)

        val checksum = diskMap.compactedByFile().checksum()

        assertThat(checksum).isEqualTo(2858)
    }

}