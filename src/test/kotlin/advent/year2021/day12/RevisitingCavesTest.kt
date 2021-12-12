package advent.year2021.day12

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RevisitingCavesTest {

  private val smallExample = RevisitingCaves(
    """
    start-A
    start-b
    A-c
    A-b
    b-d
    A-end
    b-end
  """.trimIndent()
  )

  private val mediumExample = RevisitingCaves(
    """
    dc-end
    HN-start
    start-kj
    dc-start
    dc-HN
    LN-dc
    HN-end
    kj-sa
    kj-HN
    kj-dc
  """.trimIndent()
  )

  private val largeExample = RevisitingCaves(
    """
    fs-end
    he-DX
    fs-he
    start-DX
    pj-DX
    end-zg
    zg-sl
    zg-pj
    pj-he
    RW-he
    fs-DX
    pj-RW
    zg-RW
    start-pj
    he-WI
    zg-he
    pj-fs
    start-RW
  """.trimIndent()
  )

  @Test
  fun `paths -- small input -- has expected paths`() {
    val paths = smallExample.paths()

    assertThat(paths).containsExactlyInAnyOrder(
      listOf("start", "A", "b", "A", "c", "A", "end"),
      listOf("start", "A", "b", "A", "end"),
      listOf("start", "A", "b", "end"),
      listOf("start", "A", "c", "A", "b", "A", "end"),
      listOf("start", "A", "c", "A", "b", "end"),
      listOf("start", "A", "c", "A", "end"),
      listOf("start", "A", "end"),
      listOf("start", "b", "A", "c", "A", "end"),
      listOf("start", "b", "A", "end"),
      listOf("start", "b", "end")
    )
  }

  @Test
  fun `paths -- medium input -- has 19 paths`() {
    val paths = mediumExample.paths()

    assertThat(paths).hasSize(19)
  }

  @Test
  fun `paths -- large input -- has 226 paths`() {
    val paths = largeExample.paths()

    assertThat(paths).hasSize(226)
  }

  @Test
  fun `paths -- small input, allow one revisit -- 36 paths`() {
    val paths = smallExample.paths(revisitStrategy = RevisitStrategy.SINGLE_SMALL)

    assertThat(paths).hasSize(36)
  }

  @Test
  fun `paths -- medium input, allow one revisit -- 103 paths`() {
    val paths = mediumExample.paths(revisitStrategy = RevisitStrategy.SINGLE_SMALL)

    assertThat(paths).hasSize(103)
  }

  @Test
  fun `paths -- large input, allow one revisit -- 3509 paths`() {
    val paths = largeExample.paths(revisitStrategy = RevisitStrategy.SINGLE_SMALL)

    assertThat(paths).hasSize(3509)
  }
}