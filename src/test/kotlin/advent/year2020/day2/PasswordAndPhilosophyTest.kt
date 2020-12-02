package advent.year2020.day2

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PasswordAndPhilosophyTest {

  @Test
  fun `isValidAsCount -- 1-3 a abcde -- true`() {
    val password = PasswordAndPhilosophy.parse("1-3 a: abcde")

    assertThat(password.isValidAsCount).isTrue
  }

  @Test
  fun `isValidAsCount -- 1-3 b cdefg -- false`() {
    val password = PasswordAndPhilosophy.parse("1-3 b: cdefge")

    assertThat(password.isValidAsCount).isFalse
  }

  @Test
  fun `isValidAsCount -- 2-9 c ccccccccc -- true`() {
    val password = PasswordAndPhilosophy.parse("2-9 c: ccccccccc")

    assertThat(password.isValidAsCount).isTrue
  }

  @Test
  fun `isValidAsPosition -- 1-3 a abcde -- true`() {
    val password = PasswordAndPhilosophy.parse("1-3 a: abcde")

    assertThat(password.isValidAsPosition).isTrue
  }

  @Test
  fun `isValidAsPosition -- 1-3 b cdefg -- false`() {
    val password = PasswordAndPhilosophy.parse("1-3 b: cdefge")

    assertThat(password.isValidAsPosition).isFalse
  }

  @Test
  fun `isValidAsPosition -- 2-9 c ccccccccc -- false`() {
    val password = PasswordAndPhilosophy.parse("2-9 c: ccccccccc")

    assertThat(password.isValidAsPosition).isFalse
  }
}