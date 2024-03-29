package advent.year2022.day21

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ArithmeticMonkeyTest {

  private val input = """
    root: pppw + sjmn
    dbpl: 5
    cczh: sllz + lgvd
    zczc: 2
    ptdq: humn - dvpt
    dvpt: 3
    lfqf: 4
    humn: 5
    ljgn: 2
    sjmn: drzm * dbpl
    sllz: 4
    pppw: cczh / lfqf
    lgvd: ljgn * ptdq
    drzm: hmdt - zczc
    hmdt: 32
  """.trimIndent()

  @Test
  fun `root -- reference input -- 152`() {
    val monkeys = ArithmeticMonkeys(input)

    assertThat(monkeys.getValue("root").toLong()).isEqualTo(152)
  }

  @Test
  fun `to shout -- reference input -- 301`() {
    val monkeys = ArithmeticMonkeys(input)
    val withMe = monkeys.withMe()

    assertThat(withMe.getValue("root").toLong()).isEqualTo(301)
  }
}