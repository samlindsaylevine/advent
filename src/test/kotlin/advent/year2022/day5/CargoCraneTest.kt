package advent.year2022.day5

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CargoCraneTest {

  @Test
  fun `parse -- reference input -- reference stacks and instructions`() {
    val input = """
          [D]    
      [N] [C]    
      [Z] [M] [P]
       1   2   3 
      
      move 1 from 2 to 1
      move 3 from 1 to 3
      move 2 from 2 to 1
      move 1 from 1 to 2
    """.trimIndent()

    val crane = CargoCrane(input)

    val expectedStacks = CrateStacks(
      listOf(
        listOf('N', 'Z'),
        listOf('D', 'C', 'M'),
        listOf('P')
      )
    )
    assertThat(crane.stacks).isEqualTo(expectedStacks)
    val expectedInstructions = listOf(
      CraneInstruction(count = 1, from = 2, to = 1),
      CraneInstruction(count = 3, from = 1, to = 3),
      CraneInstruction(count = 2, from = 2, to = 1),
      CraneInstruction(count = 1, from = 1, to = 2),
    )
    assertThat(crane.instructions).isEqualTo(expectedInstructions)
  }

  @Test
  fun `top crates -- reference input -- CMZ`() {
    val input = """
          [D]    
      [N] [C]    
      [Z] [M] [P]
       1   2   3 
      
      move 1 from 2 to 1
      move 3 from 1 to 3
      move 2 from 2 to 1
      move 1 from 1 to 2
    """.trimIndent()
    val crane = CargoCrane(input)

    assertThat(crane.topCrates()).isEqualTo("CMZ")
  }

  @Test
  fun `top crates -- reference input, not one at a time -- MCD`() {
    val input = """
          [D]    
      [N] [C]    
      [Z] [M] [P]
       1   2   3 
      
      move 1 from 2 to 1
      move 3 from 1 to 3
      move 2 from 2 to 1
      move 1 from 1 to 2
    """.trimIndent()
    val crane = CargoCrane(input)

    assertThat(crane.topCrates(oneAtATime = false)).isEqualTo("MCD")
  }
}