package advent.year2017.day23

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class CoprocessorTest {

    @ParameterizedTest(name = "isComposite -- {1} -- is composite")
    @CsvSource("4", "6", "9", "12", "144", "1000000")
    fun `isComposite -- composite number -- is composite`(input: Int) {
        assertThat(input.isComposite()).isTrue()
    }

    @ParameterizedTest(name = "isComposite -- {1} -- is not composite")
    @CsvSource("2", "3", "5", "7", "11", "13", "17", "317", "100003")
    fun `isComposite -- prime number -- is not composite`(input: Int) {
        assertThat(input.isComposite()).isFalse()
    }
}