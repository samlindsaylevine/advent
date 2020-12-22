package advent.year2020.day20

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SatelliteTilesTest {

  val input = """
      Tile 2311:
      ..##.#..#.
      ##..#.....
      #...##..#.
      ####.#...#
      ##.##.###.
      ##...#.###
      .#.#.#..##
      ..#....#..
      ###...#.#.
      ..###..###

      Tile 1951:
      #.##...##.
      #.####...#
      .....#..##
      #...######
      .##.#....#
      .###.#####
      ###.##.##.
      .###....#.
      ..#.#..#.#
      #...##.#..

      Tile 1171:
      ####...##.
      #..##.#..#
      ##.#..#.#.
      .###.####.
      ..###.####
      .##....##.
      .#...####.
      #.##.####.
      ####..#...
      .....##...

      Tile 1427:
      ###.##.#..
      .#..#.##..
      .#.##.#..#
      #.#.#.##.#
      ....#...##
      ...##..##.
      ...#.#####
      .#.####.#.
      ..#..###.#
      ..##.#..#.

      Tile 1489:
      ##.#.#....
      ..##...#..
      .##..##...
      ..#...#...
      #####...#.
      #..#.#.#.#
      ...#.#.#..
      ##.#...##.
      ..##.##.##
      ###.##.#..

      Tile 2473:
      #....####.
      #..#.##...
      #.##..#...
      ######.#.#
      .#...#.#.#
      .#########
      .###.#..#.
      ########.#
      ##...##.#.
      ..###.#.#.

      Tile 2971:
      ..#.#....#
      #...###...
      #.#.###...
      ##.##..#..
      .#####..##
      .#..####.#
      #..#.#..#.
      ..####.###
      ..#.#.###.
      ...#.#.#.#

      Tile 2729:
      ...#.#.#.#
      ####.#....
      ..#.#.....
      ....#..#.#
      .##..##.#.
      .#.####...
      ####.#.#..
      ##.####...
      ##..#.##..
      #.##...##.

      Tile 3079:
      #.#.#####.
      .#..######
      ..#.......
      ######....
      ####.#..#.
      .#...#.##.
      #.#####.##
      ..#.###...
      ..#.......
      ..#.###...
    """.trimIndent()

  @Test
  fun `solve -- reference input -- reference corner product`() {
    val tiles = SatelliteTiles(input)

    val solution = tiles.solve()
    val product = solution.cornerProduct()

    assertThat(product).isEqualTo(20899048083289L)
  }

  @Test
  fun `water roughness -- reference input -- 273`() {
    val tiles = SatelliteTiles(input)

    val solution = tiles.solve()
    val roughness = solution.toImage().waterRoughness

    assertThat(roughness).isEqualTo(273)
  }
}