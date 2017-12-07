package advent.year2017.day7

import advent.year2017.day7.RecursiveCircus.CircusProgram
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RecursiveCircusTest {

    private val referenceInput = """pbga (66)
xhth (57)
ebii (61)
havc (66)
ktlj (57)
fwft (72) -> ktlj, cntj, xhth
qoyq (66)
padx (45) -> pbga, havc, qoyq
tknk (41) -> ugml, padx, fwft
jptl (61)
ugml (68) -> gyxo, ebii, jptl
gyxo (61)
cntj (57)"""

    @Test
    fun `CircusProgram fromText -- no children -- returns program`() {
        val input = "pbga (66)"

        val output = CircusProgram.fromText(input)

        val expected = CircusProgram("pbga", 66, listOf())
        assertThat(output.childNames).isEqualTo(expected.childNames)
    }

    @Test
    fun `CircusProgram fromText -- with children -- returns program`() {
        val input = "fwft (72) -> ktlj, cntj, xhth"

        val output = CircusProgram.fromText(input)

        assertThat(output).isEqualTo(CircusProgram("fwft",
                72,
                listOf("ktlj", "cntj", "xhth")))
    }

    @Test
    fun `bottomProgramName -- reference input -- tknk`() {
        val circus = RecursiveCircus(referenceInput)

        val name = circus.bottomProgramName()

        assertThat(name).isEqualTo("tknk")
    }

    @Test
    fun `weightToBalance -- referenceInput -- 60`() {
        val circus = RecursiveCircus(referenceInput)

        val newWeight = circus.newWeightToBalance()

        assertThat(newWeight).isEqualTo(60)
    }


}