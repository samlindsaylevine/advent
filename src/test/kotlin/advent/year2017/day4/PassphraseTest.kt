package advent.year2017.day4

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class PassphraseTest {

    @ParameterizedTest
    @CsvSource(
            "aa bb cc dd ee, false",
            "aa bb cc dd aa, true",
            "aa bb cc dd aaa, false")
    fun `hasDuplicateWords -- reference input -- reference output`(phrase: String, duplicates: Boolean) {
        val passphrase = Passphrase(phrase)

        assertThat(passphrase.hasDuplicateWords()).isEqualTo(duplicates)
    }

    @ParameterizedTest
    @CsvSource(
            "abcde fghij, false",
            "abcde xyz ecdab, true",
            "a ab abc abd abf abj, false",
            "iiii oiii ooii oooi oooo, false",
            "oiii ioii iioi iiio, true")
    fun `hasAnagrams -- reference input -- reference output`(phrase: String, duplicates: Boolean) {
        val passphrase = Passphrase(phrase)

        assertThat(passphrase.hasAnagrams()).isEqualTo(duplicates)
    }
}