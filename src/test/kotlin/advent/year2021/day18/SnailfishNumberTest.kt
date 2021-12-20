package advent.year2021.day18

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class SnailfishNumberTest {

  @ParameterizedTest
  @CsvSource(
    delimiter = 'X', value = [
      "[1,2]",
      "[[1,2],3]",
      "[9,[8,7]]",
      "[[1,9],[8,5]]",
      "[[[[1,2],[3,4]],[[5,6],[7,8]]],9]",
      "[[[9,[3,8]],[[0,9],6]],[[[3,7],[4,9]],3]]",
      "[[[[1,3],[5,3]],[[1,3],[8,7]]],[[[4,9],[6,9]],[[8,2],[7,3]]]]"]
  )
  fun `parse -- then toString -- returns original`(input: String) {
    val number = SnailfishNumber.parse(input)

    assertThat(number.toString()).isEqualTo(input)
  }

  @Test
  fun `addition -- simple, no reduction required -- as per example`() {
    val sum = SnailfishNumber.parse("[1,2]") + SnailfishNumber.parse("[[3,4],5]")

    assertThat(sum.toString()).isEqualTo("[[1,2],[[3,4],5]]")
  }

  @ParameterizedTest
  @CsvSource(
    delimiter = ':', value = [
      "10:[5,5]",
      "11:[5,6]",
      "12:[6,6]"
    ]
  )
  fun `split -- simple -- as per examples`(input: String, expected: String) {
    val number = SnailfishNumber.parse(input)
    val split = number.reduced()

    assertThat(split.toString()).isEqualTo(expected)
  }

  @ParameterizedTest
  @CsvSource(
    delimiter = ':', value = [
      "[[[[[9,8],1],2],3],4]:[[[[0,9],2],3],4]",
      "[7,[6,[5,[4,[3,2]]]]]:[7,[6,[5,[7,0]]]]",
      "[[6,[5,[4,[3,2]]]],1]:[[6,[5,[7,0]]],3]",
      "[[3,[2,[1,[7,3]]]],[6,[5,[4,[3,2]]]]]:[[3,[2,[8,0]]],[9,[5,[7,0]]]]",
      "[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]:[[3,[2,[8,0]]],[9,[5,[7,0]]]]",
    ]
  )
  fun `single explode -- example inputs --- example resolutions`(input: String, expected: String) {
    val number = SnailfishNumber.parse(input)
    val reduced = number.reduced()

    assertThat(reduced.toString()).isEqualTo(expected)
  }

  @Test
  fun `sum -- single sum example -- reference output`() {
    val sum = SnailfishNumber.parse("[[[[4,3],4],4],[7,[[8,4],9]]]") + SnailfishNumber.parse("[1,1]")

    assertThat(sum.toString()).isEqualTo("[[[[0,7],4],[[7,8],[6,0]]],[8,1]]")
  }

  @Test
  fun `sum -- list of numbers example -- reference output`() {
    val numbers = """
      [[[0,[4,5]],[0,0]],[[[4,5],[2,6]],[9,5]]]
      [7,[[[3,7],[4,3]],[[6,3],[8,8]]]]
      [[2,[[0,8],[3,4]]],[[[6,7],1],[7,[1,6]]]]
      [[[[2,4],7],[6,[0,5]]],[[[6,8],[2,8]],[[2,1],[4,5]]]]
      [7,[5,[[3,8],[1,4]]]]
      [[2,[2,2]],[8,[8,1]]]
      [2,9]
      [1,[[[9,3],9],[[9,0],[0,7]]]]
      [[[5,[7,4]],7],1]
      [[[[4,2],2],6],[8,7]]
    """.trimIndent().lines().map(SnailfishNumber::parse)

    val sum = numbers.sum()

    assertThat(sum.toString()).isEqualTo("[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]")
  }

  @ParameterizedTest
  @CsvSource(
    delimiter = ':', value = [
      "[[1,2],[[3,4],5]]:143",
      "[[[[0,7],4],[[7,8],[6,0]]],[8,1]]:1384",
      "[[[[1,1],[2,2]],[3,3]],[4,4]]:445",
      "[[[[3,0],[5,3]],[4,4]],[5,5]]:791",
      "[[[[5,0],[7,4]],[5,5]],[6,6]]:1137",
      "[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]:3488"
    ]
  )
  fun `magnitude -- examples -- have reference magnitude`(input: String, expected: Long) {
    val number = SnailfishNumber.parse(input)

    assertThat(number.magnitude()).isEqualTo(expected)
  }

  @Test
  fun `largest pairwise magnitude -- reference example -- reference value`() {
    val homework = """
      [[[0,[5,8]],[[1,7],[9,6]]],[[4,[1,2]],[[1,4],2]]]
      [[[5,[2,8]],4],[5,[[9,9],0]]]
      [6,[[[6,2],[5,6]],[[7,6],[4,7]]]]
      [[[6,[0,7]],[0,9]],[4,[9,[9,0]]]]
      [[[7,[6,4]],[3,[1,3]]],[[[5,5],1],9]]
      [[6,[[7,3],[3,2]]],[[[3,8],[5,7]],4]]
      [[[[5,4],[7,7]],8],[[8,3],8]]
      [[9,3],[[9,9],[6,[4,9]]]]
      [[2,[[7,7],7]],[[5,8],[[9,3],[0,2]]]]
      [[[[5,2],5],[8,[3,7]]],[[5,[7,5]],[4,4]]]
    """.trimIndent().lines().map(SnailfishNumber::parse)

    val largest = homework.largestPairwiseMagnitude()

    assertThat(largest).isEqualTo(3993)
  }
}