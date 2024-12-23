package advent.year2024.day22

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class MonkeyExchangeMarketTest {

    @Test
    fun `next ten -- secret is 123 -- as reference example`() {
        val buyer = MonkeyBuyer(123)

        val nextTen = buyer.sequence().drop(1).take(10).toList()

        assertThat(nextTen).containsExactly(
            15887950,
            16495136,
            527345,
            704524,
            1553684,
            12683156,
            11100544,
            12249484,
            7753432,
            5908254
        )
    }

    @ParameterizedTest
    @CsvSource(
        "1, 8685429",
        "10, 4700978",
        "100, 15273692",
        "2024, 8667524",
    )
    fun `2000th secret number -- reference inputs -- reference values`(secret: Long, expected: Long) {
        val buyer = MonkeyBuyer(secret)

        val number = buyer.secretNumber(2000)

        assertThat(number).isEqualTo(expected)
    }

    @ParameterizedTest
    @CsvSource(
        "1, 7",
        "2, 7",
        "3, 0",
        "2024, 9"
    )
    fun `revenue from sequence -- reference monkey -- reference amount`(secret: Long, expected: Int) {
        val monkey = MonkeyBuyer(secret)

        val revenue = monkey.revenue(listOf(-2, 1, -1, 3))

        assertThat(revenue).isEqualTo(expected)
    }
}