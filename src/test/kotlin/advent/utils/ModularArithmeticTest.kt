package advent.utils

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class ModularArithmeticTest {


  @ParameterizedTest
  @CsvSource("53, 10007",
          "3111071, 1889",
          "1889, 3111071")
  fun `multiplicative inverse -- sample input -- really is the inverse`(x: Long, m: Long) {
    val actual = multiplicativeInverse(x, m)

    println(actual)

    assertThat((actual * x) % m).isEqualTo(1)
  }

  @ParameterizedTest(name = "exp mod -- {0}^{1} mod {2} -- {3}")
  @CsvSource("2, 4, 100, 16",
          "2, 4, 10, 6",
          "2, 3, 100, 8",
          "2, 3, 5, 3",
          "3, 6, 1000, 729",
          "3, 6, 10, 9")
  fun `exp mod -- sample input -- gives expected output`(x: Long, n: Long, m: Long, expected: Long) {
    val actual = expMod(x, n, m)

    assertThat(actual).isEqualTo(expected)
  }

  @Test
  fun `chineseRemainderSolution -- some sample values -- really satisfies the original equations`() {
    val a = 312L
    val p = 1789L

    val b = 642L
    val q = 1889L

    val solution = chineseRemainderSolution(ModularConstraint(a, p), ModularConstraint(b, q))

    assertThat(solution % p).isEqualTo(a)
    assertThat(solution % q).isEqualTo(b)
  }

  @Test
  fun `chineseRemainderSolution -- multiple values -- really satisfies the original equations`() {
    val a = 0L
    val p = 1789L

    val b = 36L
    val q = 37L

    val c = 45L
    val r = 47L

    val d = 1886L
    val s = 1889L


    val solution = chineseRemainderSolution(listOf(
            ModularConstraint(a, p),
            ModularConstraint(b, q),
            ModularConstraint(c, r),
            ModularConstraint(d, s)
    ))

    println(solution)

    assertThat(solution % p).isEqualTo(a)
    assertThat(solution % q).isEqualTo(b)
    assertThat(solution % r).isEqualTo(c)
    assertThat(solution % s).isEqualTo(d)
  }
}