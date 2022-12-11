package advent.year2022.day11

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ThrowingMonkeysTest {

  private val input = """
    Monkey 0:
      Starting items: 79, 98
      Operation: new = old * 19
      Test: divisible by 23
        If true: throw to monkey 2
        If false: throw to monkey 3
    
    Monkey 1:
      Starting items: 54, 65, 75, 74
      Operation: new = old + 6
      Test: divisible by 19
        If true: throw to monkey 2
        If false: throw to monkey 0
    
    Monkey 2:
      Starting items: 79, 60, 97
      Operation: new = old * old
      Test: divisible by 13
        If true: throw to monkey 1
        If false: throw to monkey 3
    
    Monkey 3:
      Starting items: 74
      Operation: new = old + 3
      Test: divisible by 17
        If true: throw to monkey 0
        If false: throw to monkey 1
  """.trimIndent()

  @Test
  fun `monkey business -- sample input, 20 rounds -- 10605`() {
    val monkeys = ThrowingMonkeys(input)

    monkeys.next(20)

    assertThat(monkeys.monkeyBusiness()).isEqualTo(10605)
  }

  @Test
  fun `monkey business -- sample input, not relaxed, 10000 rounds -- 2713310158`() {
    val monkeys = ThrowingMonkeys(input, relaxed = false)

    monkeys.next(10000)

    assertThat(monkeys.monkeyBusiness()).isEqualTo(2713310158)
  }
}