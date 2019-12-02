package advent.year2019.day2

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class IntcodeProgramTest {

    @ParameterizedTest(name = "execute -- program {0} -- has output {1}")
    @CsvSource(value = ["1,9,10,3,2,3,11,0,99,30,40,50>3500,9,10,70,2,3,11,0,99,30,40,50",
        "1,0,0,0,99>2,0,0,0,99",
        "2,3,0,3,99>2,3,0,6,99",
        "2,4,4,5,99,0>2,4,4,5,99,9801",
        "1,1,1,4,99,5,6,0,99>30,1,1,4,2,5,6,0,99"
    ],
            delimiter = '>')
    fun `execute -- reference programs -- reference outputs`(valueString: String, expectedString: String) {
        val values = valueString.split(",").map { it.toInt() }
        val program = IntcodeProgram(values)

        val result = program.execute()

        val expected = expectedString.split(",").map { it.toInt() }
        assertThat(result).isEqualTo(expected)
    }

}