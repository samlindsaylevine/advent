package advent.year2017.day21

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FractalArtTest {

    private val starterImage = """
            .#.
            ..#
            ###
        """.trimIndent()

    private val referenceBook = """
        ../.# => ##./#../...
        .#./..#/### => #..#/..../..../#..#
    """.trimIndent()

    @Test
    fun `parse and toString -- starter image -- yields original`() {
        val image = FractalArtImage(starterImage)

        val string = image.toString()

        assertThat(string).isEqualTo(starterImage)
    }

    @Test
    fun `onPixelCount -- reference image -- 5 pixels`() {
        val image = FractalArtImage(starterImage)

        val count = image.onPixelCount()

        assertThat(count).isEqualTo(5)
    }

    @Test
    fun `next -- reference book & start -- reference 1st iteration result`() {
        val book = FractalArtBook(referenceBook)
        val start = FractalArtImage(starterImage)

        val next = book.next(start)

        val expected = FractalArtImage("""
            #..#
            ....
            ....
            #..#
        """.trimIndent())
        assertThat(next).isEqualTo(expected)
    }

    @Test
    fun `next -- reference book & 1st iteration -- reference 2nd iteration result`() {
        val book = FractalArtBook(referenceBook)
        val first = FractalArtImage("""
            #..#
            ....
            ....
            #..#
        """.trimIndent())

        val second = book.next(first)

        val expected = FractalArtImage("""
            ##.##.
            #..#..
            ......
            ##.##.
            #..#..
            ......
        """.trimIndent())
        assertThat(second).isEqualTo(expected)
        assertThat(second.onPixelCount()).isEqualTo(12)
    }

    @Test
    fun `partition -- reference image -- reference partition result`() {
        val image = FractalArtImage("""
            #..#
            ....
            ....
            #..#
        """.trimIndent())

        val partition = image.partition()

        val expected = listOf(
                listOf(
                        FractalArtImage("""
                            #.
                            ..
                            """.trimIndent()),
                        FractalArtImage("""
                            .#
                            ..
                            """.trimIndent())),
                listOf(
                        FractalArtImage("""
                            ..
                            #.
                            """.trimIndent()),
                        FractalArtImage("""
                            ..
                            .#
                            """.trimIndent())))
        assertThat(partition).isEqualTo(expected)
    }

    @Test
    fun `merge -- reference merge input -- reference merge result`() {
        val input = listOf(
                listOf(
                        FractalArtImage("""
                            ##.
                            #..
                            ...
                            """.trimIndent()),
                        FractalArtImage("""
                            ##.
                            #..
                            ...
                            """.trimIndent())),
                listOf(
                        FractalArtImage("""
                            ##.
                            #..
                            ...
                            """.trimIndent()),
                        FractalArtImage("""
                            ##.
                            #..
                            ...
                            """.trimIndent())))

        val merged = input.merge()

        val expected = FractalArtImage("""
            ##.##.
            #..#..
            ......
            ##.##.
            #..#..
            ......
        """.trimIndent())
        assertThat(merged).isEqualTo(expected)
    }

    @Test
    fun `flipped -- a normal image -- is vertically flipped`() {
        val input = FractalArtImage("""
            ##.###
            #....#
            ......
            #..##.
            #....#
            ......
        """.trimIndent())

        val flipped = input.flipped()

        val expected = FractalArtImage("""
            ......
            #....#
            #..##.
            ......
            #....#
            ##.###
        """.trimIndent())
        assertThat(flipped).isEqualTo(expected)
    }

    @Test
    fun `rotated -- a normal image -- is rotated a quarter turn clockwise`() {
        val input = FractalArtImage("""
            ##.###
            #....#
            ......
            #..##.
            #....#
            ......
        """.trimIndent())

        val rotated = input.rotated()

        val expected = FractalArtImage("""
            .##.##
            .....#
            ......
            ..#..#
            ..#..#
            .#..##
        """.trimIndent())
        assertThat(rotated).isEqualTo(expected)
    }
}