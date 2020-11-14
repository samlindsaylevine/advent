package advent.year2017.day9

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class GarbageStreamTest {

  @ParameterizedTest(name = "groupCount -- stream {0} -- has {1} groups")
  @CsvSource(delimiter = '=', value = [
    "{} = 1",
    "{{{}}} = 3",
    "{{},{}} = 3",
    "{{{},{},{{}}}} = 6",
    "{<{},{},{{}}>} = 1",
    "{<a>,<a>,<a>,<a>} = 1",
    "{{<a>},{<a>},{<a>},{<a>}} = 5",
    "{{<!>},{<!>},{<!>},{<a>}} = 2"
  ])
  fun `groupCount -- reference input -- reference output`(input: String, expected: Int) {
    val stream = GarbageStream(input)

    val groupCount = stream.groupCount()

    assertThat(groupCount).isEqualTo(expected)
  }

  @ParameterizedTest(name = "totalScore -- stream {0} -- has total score {1}")
  @CsvSource(delimiter = '=', value = [
    "{} = 1",
    "{{{}}} = 6",
    "{{},{}} = 5",
    "{{{},{},{{}}}} = 16",
    "{{<ab>},{<ab>},{<ab>},{<ab>}} = 9",
    "{{<!!>},{<!!>},{<!!>},{<!!>}} = 9",
    "{{<a!>},{<a!>},{<a!>},{<ab>}} = 3"
  ])
  fun `totalScore -- reference input -- reference output`(input: String, expected: Int) {
    val stream = GarbageStream(input)

    val totalScore = stream.totalScore()

    assertThat(totalScore).isEqualTo(expected)
  }

  @ParameterizedTest(name = "uncancelledGarbageCount -- stream {0} -- has {1} uncancelled garbage characters")
  @CsvSource(delimiter = '=', value = [
    "{<>} = 0",
    "{<random characters>} = 17",
    "{<<<<>} = 3",
    "{<{!>}>} = 2",
    "{<!!>} = 0",
    "{<!!!>>} = 0",
    "{<{o\"i!a,<{i<a>} = 10"
  ])
  fun `uncancelledGarbageCount -- reference input -- reference output`(input: String, expected: Int) {
    val stream = GarbageStream(input)

    val uncancelledGarbageCount = stream.uncancelledGarbageCount()

    assertThat(uncancelledGarbageCount).isEqualTo(expected)
  }


}