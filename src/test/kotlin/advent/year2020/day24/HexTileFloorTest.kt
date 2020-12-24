package advent.year2020.day24

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class HexTileFloorTest {

  private val input = """
    sesenwnenenewseeswwswswwnenewsewsw
    neeenesenwnwwswnenewnwwsewnenwseswesw
    seswneswswsenwwnwse
    nwnwneseeswswnenewneswwnewseswneseene
    swweswneswnenwsewnwneneseenw
    eesenwseswswnenwswnwnwsewwnwsene
    sewnenenenesenwsewnenwwwse
    wenwwweseeeweswwwnwwe
    wsweesenenewnwwnwsenewsenwwsesesenwne
    neeswseenwwswnwswswnw
    nenwswwsewswnenenewsenwsenwnesesenew
    enewnwewneswsewnwswenweswnenwsenwsw
    sweneswneswneneenwnewenewwneswswnese
    swwesenesewenwneswnwwneseswwne
    enesenwswwswneneswsenwnewswseenwsese
    wnwnesenesenenwwnenwsewesewsesesew
    nenewswnwewswnenesenwnesewesw
    eneswnwswnwsenenwnwnwwseeswneewsenese
    neswnwewnwnwseenwseesewsenwsweewe
    wseweeenwnesenwwwswnew
  """.trimIndent()

  @Test
  fun `countBlack -- reference example -- 10 tiles`() {
    val floor = HexTileFloor(input)

    val count = floor.tiles.size

    assertThat(count).isEqualTo(10)
  }

  @Test
  fun `advance 1 step -- reference example -- 15 tiles`() {
    val floor = HexTileFloor(input)

    val count = floor.next().tiles.size

    assertThat(count).isEqualTo(15)
  }

  @Test
  fun `advance 100 steps -- reference example -- 2208 tiles`() {
    val floor = HexTileFloor(input)

    val count = floor.next(100).tiles.size

    assertThat(count).isEqualTo(2208)
  }
}