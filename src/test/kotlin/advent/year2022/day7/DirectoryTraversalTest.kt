package advent.year2022.day7

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DirectoryTraversalTest {

  @Test
  fun `sumSize -- reference input, upper bound 100,000 -- 95437`() {
    val input = """
      ${'$'} cd /
      ${'$'} ls
      dir a
      14848514 b.txt
      8504156 c.dat
      dir d
      ${'$'} cd a
      ${'$'} ls
      dir e
      29116 f
      2557 g
      62596 h.lst
      ${'$'} cd e
      ${'$'} ls
      584 i
      ${'$'} cd ..
      ${'$'} cd ..
      ${'$'} cd d
      ${'$'} ls
      4060174 j
      8033020 d.log
      5626152 d.ext
      7214296 k
    """.trimIndent().lines()
    val traversal = DirectoryTraversal(input)

    val directory = traversal.discover()

    assertThat(directory.sumSize(100_000)).isEqualTo(95_437)
  }

  @Test
  fun `toDelete -- reference input -- 24933642`() {
    val input = """
      ${'$'} cd /
      ${'$'} ls
      dir a
      14848514 b.txt
      8504156 c.dat
      dir d
      ${'$'} cd a
      ${'$'} ls
      dir e
      29116 f
      2557 g
      62596 h.lst
      ${'$'} cd e
      ${'$'} ls
      584 i
      ${'$'} cd ..
      ${'$'} cd ..
      ${'$'} cd d
      ${'$'} ls
      4060174 j
      8033020 d.log
      5626152 d.ext
      7214296 k
    """.trimIndent().lines()
    val traversal = DirectoryTraversal(input)

    val directory = traversal.discover()

    assertThat(directory.toDelete()).isEqualTo(24933642)
  }
}