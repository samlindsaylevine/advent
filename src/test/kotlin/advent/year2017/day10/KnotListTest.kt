package advent.year2017.day10

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class KnotListTest {

  @Test
  fun `applyLengths -- reference input size 5 -- reference output`() {
    val knotList = KnotList(5)

    knotList.applyLengths(listOf(3, 4, 1, 5))

    assertThat(knotList.values()).containsExactly(3, 4, 2, 1, 0)
    assertThat(knotList.productOfFirstTwo()).isEqualTo(12)
  }

  @ParameterizedTest(name = "knotHash -- {0} -- becomes {1}")
  @CsvSource(delimiter = '=', value = [
    "'' = a2582a3a0e66e6e86e3812dcb672a272",
    "AoC 2017 = 33efeb34ea91902bb2f59c9920caa6cd",
    "1,2,3 = 3efbe78a8d82f29979031a4aa0b16a9d",
    "1,2,4 = 63960835bcdc130f0b66d7ff4f6a5a8e"
  ])
  fun `knotHash -- reference input -- reference output`(input: String, expected: String) {
    val knotHash = KnotHash(input)

    val hex = knotHash.hex

    assertThat(hex).isEqualTo(expected)
  }


  @ParameterizedTest(name = "toHex -- {0} -- is {1}")
  @CsvSource("0, 00",
          "1, 01",
          "9, 09",
          "255, ff")
  fun `toHex -- number -- padded hex equivalent`(input: Int, expected: String) {
    val result = KnotHash.toHex(input)

    assertThat(result).isEqualTo(expected)
  }
}