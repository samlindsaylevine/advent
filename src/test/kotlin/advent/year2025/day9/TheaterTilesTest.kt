package advent.year2025.day9

import advent.utils.Point
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TheaterTilesTest {

  private val input = """
      7,1
      11,1
      11,7
      9,7
      9,5
      2,5
      2,3
      7,3
  """.trimIndent()

  @Test
  fun `largestRectangleArea -- reference example -- 50`() {
    val tiles = TheaterTiles(input)

    val area = tiles.largestRectangleArea()

    assertThat(area).isEqualTo(50L)
  }

  @Test
  fun `largestRedAndGreenRectangleArea -- reference example -- 24`() {
    val tiles = TheaterTiles(input)

    val area = tiles.largestRedAndGreenRectangleArea()

    assertThat(area).isEqualTo(24L)
  }

  @Test
  fun `rectanglePerimeterIsInPolygon -- reference example, biggest rectangle -- true`() {
    val tiles = TheaterTiles(input)

    val rectangle = Point(9, 5) to Point(2, 3)

    assertThat(tiles.rectangleIsInPolygon(rectangle)).isTrue
  }
}