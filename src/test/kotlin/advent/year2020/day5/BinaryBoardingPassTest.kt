package advent.year2020.day5

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class BinaryBoardingPassTest {

  @ParameterizedTest
  @CsvSource("FBFBBFFRLR, 44, 5, 357",
          "BFFFBBFRRR, 70, 7, 567",
          "FFFBBBFRRR, 14, 7, 119",
          "BBFFBBFRLL, 102, 4, 820")
  fun `row, column, seat ID -- reference inputs -- reference values`(pass: String,
                                                                     row: Int, column: Int, seatId: Int) {
    val boardingPass = BinaryBoardingPass(pass)

    assertThat(boardingPass.row).isEqualTo(row)
    assertThat(boardingPass.column).isEqualTo(column)
    assertThat(boardingPass.seatId).isEqualTo(seatId)
  }
}