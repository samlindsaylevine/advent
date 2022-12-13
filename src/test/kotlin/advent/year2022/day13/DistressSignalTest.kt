package advent.year2022.day13

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class DistressSignalTest {

  @Test
  fun `parse -- sample list -- parsed successfully`() {
    val input = "[[1],[2,3,4]]"

    val packet = DistressSignalPacket.parse(input)

    val expected = DistressSignalList(
      listOf(
        DistressSignalList(listOf(DistressSignalInteger(1))),
        DistressSignalList(
          listOf(
            DistressSignalInteger(2),
            DistressSignalInteger(3),
            DistressSignalInteger(4)
          )
        )
      )
    )
    assertThat(packet).isEqualTo(expected)
  }

  @ParameterizedTest
  @CsvSource(
    delimiterString = "; ", value = [
      "[1,1,3,1,1]; [1,1,5,1,1]; true",
      "[[1],[2,3,4]]; [[1],4]; true",
      "[9]; [[8,7,6]]; false",
      "[[4,4],4,4]; [[4,4],4,4,4]; true",
      "[7,7,7,7]; [7,7,7]; false",
      "[]; [3]; true",
      "[[[]]]; [[]]; false",
      "[1,[2,[3,[4,[5,6,7]]]],8,9]; [1,[2,[3,[4,[5,6,0]]]],8,9]; false"
    ]
  )
  fun `compare -- reference examples -- reference ordering`(
    leftInput: String,
    rightInput: String,
    lessThan: Boolean
  ) {
    val left = DistressSignalPacket.parse(leftInput)
    val right = DistressSignalPacket.parse(rightInput)

    val result = left < right

    assertThat(result).isEqualTo(lessThan)
  }

  @Test
  fun `correct index sum -- reference example -- 13`() {
    val input = """
      [1,1,3,1,1]
      [1,1,5,1,1]

      [[1],[2,3,4]]
      [[1],4]

      [9]
      [[8,7,6]]

      [[4,4],4,4]
      [[4,4],4,4,4]

      [7,7,7,7]
      [7,7,7]

      []
      [3]

      [[[]]]
      [[]]

      [1,[2,[3,[4,[5,6,7]]]],8,9]
      [1,[2,[3,[4,[5,6,0]]]],8,9]
    """.trimIndent()
    val signal = PairedDistressSignal(input)

    assertThat(signal.correctIndexSum()).isEqualTo(13)
  }

  @Test
  fun `decoder key -- reference example -- 140`() {
    val input = """
      [1,1,3,1,1]
      [1,1,5,1,1]

      [[1],[2,3,4]]
      [[1],4]

      [9]
      [[8,7,6]]

      [[4,4],4,4]
      [[4,4],4,4,4]

      [7,7,7,7]
      [7,7,7]

      []
      [3]

      [[[]]]
      [[]]

      [1,[2,[3,[4,[5,6,7]]]],8,9]
      [1,[2,[3,[4,[5,6,0]]]],8,9]
    """.trimIndent()
    val signal = DistressSignal(input)

    assertThat(signal.decoderKey()).isEqualTo(140)
  }


}