package advent.year2021.day8

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SevenSegmentDisplayTest {


  val input = """
    be cfbegad cbdgef fgaecd cgeb fdcge agebfd fecdb fabcd edb | fdgacbe cefdb cefbgd gcbe
    edbfga begcd cbg gc gcadebf fbgde acbgfd abcde gfcbed gfec | fcgedb cgb dgebacf gc
    fgaebd cg bdaec gdafb agbcfd gdcbef bgcad gfac gcb cdgabef | cg cg fdcagb cbg
    fbegcd cbd adcefb dageb afcb bc aefdc ecdab fgdeca fcdbega | efabcd cedba gadfec cb
    aecbfdg fbg gf bafeg dbefa fcge gcbea fcaegb dgceab fcbdga | gecf egdcabf bgf bfgea
    fgeab ca afcebg bdacfeg cfaedg gcfdb baec bfadeg bafgc acf | gebdcfa ecba ca fadegcb
    dbcfg fgd bdegcaf fgec aegbdf ecdfab fbedc dacgb gdcebf gf | cefg dcbef fcge gbcadfe
    bdfegc cbegaf gecbf dfcage bdacg ed bedf ced adcbefg gebcd | ed bcgafe cdgba cbgef
    egadfb cdbfeg cegd fecab cgb gbdefca cg fgcdab egfdb bfceg | gbdfcae bgc cg cgb
    gcafb gcf dcaebfg ecagb gf abcdeg gaef cafbge fdbac fegbdc | fgae cfgab fg bagce
  """.trimIndent()

  @Test
  fun `uniqueLengthsInOutput -- reference input -- 26`() {
    val entries = DisplayEntries(input)

    assertThat(entries.uniqueLengthsInOutput()).isEqualTo(26)
  }

  @Test
  fun `single entry -- mapping -- as per example`() {
    val entry = DisplayEntry(
      "acedgfb cdfbe gcdfa fbcad dab cefabd cdfgeb eafb cagedb ab" +
          " | cdfeb fcadb cdfeb cdbaf"
    )

    val mapping = entry.wiresToSegmentsMapping()

    assertThat(mapping).isEqualTo(
      mapOf(
        'd' to 'a',
        'e' to 'b',
        'a' to 'c',
        'f' to 'd',
        'g' to 'e',
        'b' to 'f',
        'c' to 'g'
      )
    )
  }

  @Test
  fun `single entry -- output number -- 5353`() {
    val entry = DisplayEntry(
      "acedgfb cdfbe gcdfa fbcad dab cefabd cdfgeb eafb cagedb ab" +
          " | cdfeb fcadb cdfeb cdbaf"
    )

    assertThat(entry.outputNumber()).isEqualTo(5353)
  }

  @Test
  fun `output sum -- reference input -- 61229`() {
    val entries = DisplayEntries(input)

    assertThat(entries.outputSum()).isEqualTo(61229)
  }
}