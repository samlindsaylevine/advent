package advent.year2022.day5

import advent.utils.findAllNumbers
import advent.utils.findNumber
import advent.utils.maxOrThrow
import advent.utils.updated
import java.io.File

/**
 * --- Day 5: Supply Stacks ---
 * The expedition can depart as soon as the final supplies have been unloaded from the ships. Supplies are stored in
 * stacks of marked crates, but because the needed supplies are buried under many other crates, the crates need to be
 * rearranged.
 * The ship has a giant cargo crane capable of moving crates between stacks. To ensure none of the crates get crushed
 * or fall over, the crane operator will rearrange them in a series of carefully-planned steps. After the crates are
 * rearranged, the desired crates will be at the top of each stack.
 * The Elves don't want to interrupt the crane operator during this delicate procedure, but they forgot to ask her
 * which crate will end up where, and they want to be ready to unload them as soon as possible so they can embark.
 * They do, however, have a drawing of the starting stacks of crates and the rearrangement procedure (your puzzle
 * input). For example:
 *     [D]
 * [N] [C]
 * [Z] [M] [P]
 *  1   2   3
 *
 * move 1 from 2 to 1
 * move 3 from 1 to 3
 * move 2 from 2 to 1
 * move 1 from 1 to 2
 *
 * In this example, there are three stacks of crates. Stack 1 contains two crates: crate Z is on the bottom, and crate
 * N is on top. Stack 2 contains three crates; from bottom to top, they are crates M, C, and D. Finally, stack 3
 * contains a single crate, P.
 * Then, the rearrangement procedure is given. In each step of the procedure, a quantity of crates is moved from one
 * stack to a different stack. In the first step of the above rearrangement procedure, one crate is moved from stack 2
 * to stack 1, resulting in this configuration:
 * [D]
 * [N] [C]
 * [Z] [M] [P]
 *  1   2   3
 *
 * In the second step, three crates are moved from stack 1 to stack 3. Crates are moved one at a time, so the first
 * crate to be moved (D) ends up below the second and third crates:
 *         [Z]
 *         [N]
 *     [C] [D]
 *     [M] [P]
 *  1   2   3
 *
 * Then, both crates are moved from stack 2 to stack 1. Again, because crates are moved one at a time, crate C ends up
 * below crate M:
 *         [Z]
 *         [N]
 * [M]     [D]
 * [C]     [P]
 *  1   2   3
 *
 * Finally, one crate is moved from stack 1 to stack 2:
 *         [Z]
 *         [N]
 *         [D]
 * [C] [M] [P]
 *  1   2   3
 *
 * The Elves just need to know which crate will end up on top of each stack; in this example, the top crates are C in
 * stack 1, M in stack 2, and Z in stack 3, so you should combine these together and give the Elves the message CMZ.
 * After the rearrangement procedure completes, what crate ends up on top of each stack?
 *
 * --- Part Two ---
 * As you watch the crane operator expertly rearrange the crates, you notice the process isn't following your
 * prediction.
 * Some mud was covering the writing on the side of the crane, and you quickly wipe it away. The crane isn't a
 * CrateMover 9000 - it's a CrateMover 9001.
 * The CrateMover 9001 is notable for many new and exciting features: air conditioning, leather seats, an extra cup
 * holder, and the ability to pick up and move multiple crates at once.
 * Again considering the example above, the crates begin in the same configuration:
 *     [D]
 * [N] [C]
 * [Z] [M] [P]
 *  1   2   3
 *
 * Moving a single crate from stack 2 to stack 1 behaves the same as before:
 * [D]
 * [N] [C]
 * [Z] [M] [P]
 *  1   2   3
 *
 * However, the action of moving three crates from stack 1 to stack 3 means that those three moved crates stay in the
 * same order, resulting in this new configuration:
 *         [D]
 *         [N]
 *     [C] [Z]
 *     [M] [P]
 *  1   2   3
 *
 * Next, as both crates are moved from stack 2 to stack 1, they retain their order as well:
 *         [D]
 *         [N]
 * [C]     [Z]
 * [M]     [P]
 *  1   2   3
 *
 * Finally, a single crate is still moved from stack 1 to stack 2, but now it's crate C that gets moved:
 *         [D]
 *         [N]
 *         [Z]
 * [M] [C] [P]
 *  1   2   3
 *
 * In this example, the CrateMover 9001 has put the crates in a totally different order: MCD.
 * Before the rearrangement process finishes, update your simulation so that the Elves know where they should stand to
 * be ready to unload the final supplies. After the rearrangement procedure completes, what crate ends up on top of
 * each stack?
 *
 */
data class CrateStacks(val stacks: List<List<Char>>) {
  constructor(input: String) : this(input.lines().let { lines ->
    val numColumns = lines.last().findAllNumbers().maxOrThrow()
    val maxStackHeight = lines.size - 1
    (0 until numColumns).map { stackNumber ->
      val x = 4 * stackNumber + 1
      (0 until maxStackHeight).mapNotNull { y ->
        val char = lines[y][x]
        if (char == ' ') null else char
      }
    }
  })

  fun execute(instruction: CraneInstruction, oneAtATime: Boolean): CrateStacks {
    // Convert from problem's 1-indexed to our 0-indexed.
    val effectiveFrom = instruction.from - 1
    val effectiveTo = instruction.to - 1

    val taken = stacks[effectiveFrom].take(instruction.count)
    val dropped = if (oneAtATime) taken.reversed() else taken

    val newStacks = stacks.updated(effectiveFrom, stacks[effectiveFrom].drop(instruction.count))
      .updated(effectiveTo, dropped + stacks[effectiveTo])
    return CrateStacks(newStacks)
  }

  fun execute(instructions: List<CraneInstruction>, oneAtATime: Boolean) =
    instructions.fold(this) { stacks, instruction -> stacks.execute(instruction, oneAtATime) }

  fun topCrates() = stacks.map { it.first() }.joinToString(separator = "")
}

data class CraneInstruction(val count: Int, val from: Int, val to: Int) {
  constructor(input: String) : this(input.findNumber(0), input.findNumber(1), input.findNumber(2))
}

class CargoCrane(
  val stacks: CrateStacks,
  val instructions: List<CraneInstruction>
) {
  constructor(input: String) : this(
    CrateStacks(input.substringBefore("\n\n")),
    input.substringAfter("\n\n").lines().map(::CraneInstruction)
  )

  fun topCrates(oneAtATime: Boolean = true) = stacks.execute(instructions, oneAtATime).topCrates()
}

fun main() {
  val input = File("src/main/kotlin/advent/year2022/day5/input.txt").readText().trim()
  val crane = CargoCrane(input)

  println(crane.topCrates())
  println(crane.topCrates(oneAtATime = false))
}
