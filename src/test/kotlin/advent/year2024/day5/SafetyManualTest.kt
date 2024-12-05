package advent.year2024.day5

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class SafetyManualTest {
    val referenceInput = """
            47|53
            97|13
            97|61
            97|47
            75|29
            61|13
            75|53
            29|13
            97|29
            53|29
            61|53
            97|53
            61|29
            47|13
            75|47
            97|75
            47|61
            75|61
            47|29
            75|13
            53|13

            75,47,61,53,29
            97,61,53,29,13
            75,29,13
            75,97,47,61,53
            61,13,29
            97,13,75,29,47
        """.trimIndent()

    @Test
    fun `middleNumberSum -- reference input -- 143`() {
        val manual = SafetyManual(referenceInput)

        val sum = manual.middleNumberSum()

        assertThat(sum).isEqualTo(143)
    }

    @Test
    fun `corrected sum -- reference input -- 123`() {
        val manual = SafetyManual(referenceInput)

        val sum = manual.correctedMiddleNumberSum()

        assertThat(sum).isEqualTo(123)
    }

    @ParameterizedTest(name = "corrected update -- {1} -- becomes {2}")
    @CsvSource(
        delimiterString = " -> ", value = [
            "75,97,47,61,53 -> 97,75,47,61,53",
            "61,13,29 -> 61,29,13",
            "97,13,75,29,47 -> 97,75,47,29,13"
        ]
    )
    fun `corrected updates`(inputString: String, expectedString: String) {
        val input = inputString.split(",").map { it.toInt() }
        val expected = expectedString.split(",").map { it.toInt() }
        val update = SafetyManual.Update(input)
        val manual = SafetyManual(referenceInput)

        val corrected = update.corrected(manual.rules)

        assertThat(corrected.pages).isEqualTo(expected)
    }

    @ParameterizedTest(name = "moveToAfter -- {0}, {1}, {2} -- becomes {3}")
    @CsvSource(
        delimiterString = " - ", value = [
            "0,1,2,3 - 1 - 2 - 0,1,2,3",
            "0,1,2,3 - 2 - 1 - 0,2,1,3",
            "0,1,2,3 - 2 - 0 - 1,2,0,3",
            "0,1,2,3 - 3 - 2 - 0,1,3,2",
        ]
    )
    fun moveToAfter(listString: String, before: Int, after: Int, expectedString: String) {
        val list = listString.split(",").map(String::toInt)
        val expected = expectedString.split(",").map(String::toInt)

        val moved = list.moveToAfter(before, after)

        assertThat(moved).isEqualTo(expected)
    }
}