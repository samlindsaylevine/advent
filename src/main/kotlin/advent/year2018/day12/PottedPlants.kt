package advent.year2018.day12

import java.io.File

/**
 * --- Day 12: Subterranean Sustainability ---
 * The year 518 is significantly more underground than your history books implied.  Either that, or you've arrived in a
 * vast cavern network under the North Pole.
 * After exploring a little, you discover a long tunnel that contains a row of small pots as far as you can see to your
 * left and right.  A few of them contain plants - someone is trying to grow things in these geothermally-heated caves.
 * The pots are numbered, with 0 in front of you.  To the left, the pots are numbered -1, -2, -3, and so on; to the
 * right, 1, 2, 3.... Your puzzle input contains a list of pots from 0 to the right and whether they do (#) or do not
 * (.) currently contain a plant, the initial state. (No other pots currently contain plants.) For example, an initial
 * state of #..##.... indicates that pots 0, 3, and 4 currently contain plants.
 * Your puzzle input also contains some notes you find on a nearby table: someone has been trying to figure out how
 * these plants spread to nearby pots.  Based on the notes, for each generation of plants, a given pot has or does not
 * have a plant based on whether that pot (and the two pots on either side of it) had a plant in the last generation.
 * These are written as LLCRR => N, where L are pots to the left, C is the current pot being considered, R are the pots
 * to the right, and N is whether the current pot will have a plant in the next generation. For example:
 * 
 * A note like ..#.. => . means that a pot that contains a plant but with no plants within two pots of it will not have
 * a plant in it during the next generation.
 * A note like ##.## => . means that an empty pot with two plants on each side of it will remain empty in the next
 * generation.
 * A note like .##.# => # means that a pot has a plant in a given generation if, in the previous generation, there were
 * plants in that pot, the one immediately to the left, and the one two pots to the right, but not in the ones
 * immediately to the right and two to the left.
 * 
 * It's not clear what these plants are for, but you're sure it's important, so you'd like to make sure the current
 * configuration of plants is sustainable by determining what will happen after 20 generations.
 * For example, given the following input:
 * initial state: #..#.#..##......###...###
 * 
 * ...## => #
 * ..#.. => #
 * .#... => #
 * .#.#. => #
 * .#.## => #
 * .##.. => #
 * .#### => #
 * #.#.# => #
 * #.### => #
 * ##.#. => #
 * ##.## => #
 * ###.. => #
 * ###.# => #
 * ####. => #
 * 
 * For brevity, in this example, only the combinations which do produce a plant are listed. (Your input includes all
 * possible combinations.) Then, the next 20 generations will look like this:
 *                  1         2         3     
 *        0         0         0         0     
 *  0: ...#..#.#..##......###...###...........
 *  1: ...#...#....#.....#..#..#..#...........
 *  2: ...##..##...##....#..#..#..##..........
 *  3: ..#.#...#..#.#....#..#..#...#..........
 *  4: ...#.#..#...#.#...#..#..##..##.........
 *  5: ....#...##...#.#..#..#...#...#.........
 *  6: ....##.#.#....#...#..##..##..##........
 *  7: ...#..###.#...##..#...#...#...#........
 *  8: ...#....##.#.#.#..##..##..##..##.......
 *  9: ...##..#..#####....#...#...#...#.......
 * 10: ..#.#..#...#.##....##..##..##..##......
 * 11: ...#...##...#.#...#.#...#...#...#......
 * 12: ...##.#.#....#.#...#.#..##..##..##.....
 * 13: ..#..###.#....#.#...#....#...#...#.....
 * 14: ..#....##.#....#.#..##...##..##..##....
 * 15: ..##..#..#.#....#....#..#.#...#...#....
 * 16: .#.#..#...#.#...##...#...#.#..##..##...
 * 17: ..#...##...#.#.#.#...##...#....#...#...
 * 18: ..##.#.#....#####.#.#.#...##...##..##..
 * 19: .#..###.#..#.#.#######.#.#.#..#.#...#..
 * 20: .#....##....#####...#######....#.#..##.
 * 
 * The generation is shown along the left, where 0 is the initial state.  The pot numbers are shown along the top,
 * where 0 labels the center pot, negative-numbered pots extend to the left, and positive pots extend toward the right.
 * Remember, the initial state begins at pot 0, which is not the leftmost pot used in this example.
 * After one generation, only seven plants remain.  The one in pot 0 matched the rule looking for ..#.., the one in pot
 * 4 matched the rule looking for .#.#., pot 9 matched .##.., and so on.
 * In this example, after 20 generations, the pots shown as # contain plants, the furthest left of which is pot -2, and
 * the furthest right of which is pot 34. Adding up all the numbers of plant-containing pots after the 20th generation
 * produces 325.
 * After 20 generations, what is the sum of the numbers of all pots which contain a plant?
 * 
 * --- Part Two ---
 * You realize that 20 generations aren't enough.  After all, these plants will need to last another 1500 years to even
 * reach your timeline, not to mention your future.
 * After fifty billion (50000000000) generations, what is the sum of the numbers of all pots which contain a plant?
 * 
 */
class PottedPlants(private val potsWithPlants: Set<Int>,
                   private val generationsElapsed: Int,
                   private val rules: Map<PlantRuleCondition, Boolean>) {

    companion object {
        fun parse(input: String): PottedPlants {
            val lines = input.trim().split("\n")

            val initialState = lines[0].removePrefix("initial state: ")
                    .toCharArray()
                    .asSequence()
                    .withIndex()
                    .filter { it.value.isPlant() }
                    .map { it.index }
                    .toSet()

            val ruleLines = lines.subList(2, lines.size)
            val rules = ruleLines.map { parseRule(it) }
                    .toMap()

            return PottedPlants(initialState, 0, rules)
        }

        private fun parseRule(input: String): Pair<PlantRuleCondition, Boolean> {
            val chars = input.toCharArray()

            return Pair(PlantRuleCondition(chars[0].isPlant(),
                    chars[1].isPlant(),
                    chars[2].isPlant(),
                    chars[3].isPlant(),
                    chars[4].isPlant()),
                    chars.last().isPlant())
        }

        private fun Char.isPlant() = (this == '#')
    }

    private fun hasPlant(index: Int) = potsWithPlants.contains(index)

    private fun nextGeneration(): PottedPlants {
        val min = (potsWithPlants.minOrNull() ?: 0) - 2
        val max = (potsWithPlants.maxOrNull() ?: 0) + 2

        return PottedPlants((min..max).filter(this::hasPlantInNextGeneration).toSet(),
                generationsElapsed + 1,
                rules)
    }

    private fun hasPlantInNextGeneration(index: Int): Boolean {
        val condition = PlantRuleCondition(hasPlant(index - 2),
                hasPlant(index - 1),
                hasPlant(index),
                hasPlant(index + 1),
                hasPlant(index + 2))

        return rules[condition] ?: false
    }

    fun after(generations: Int) = (1..generations).fold(this) { plants, _ -> plants.nextGeneration() }

    fun sumOfPotNumbers() = potsWithPlants.sum()
    fun numPots() = potsWithPlants.size
}

data class PlantRuleCondition(val twoLeft: Boolean,
                              val left: Boolean,
                              val center: Boolean,
                              val right: Boolean,
                              val twoRight: Boolean)

fun main() {
    val input = File("src/main/kotlin/advent/year2018/day12/input.txt")
            .readText()
            .trim()

    val plants = PottedPlants.parse(input)

    println(plants.after(20).sumOfPotNumbers())

    // We definitely can't actually execute 50 billion generations. There isn't an actual loop in states (I checked) -
    // the sum increases without bound. Furthermore, there isn't even a loop in patterns (if you see if the whole
    // state could be translated a fixed distance and match a previous state). Some parts of the pattern must be moving
    // while others are stable.
    // However, we did notice that we eventually hit a fixed number of pots!
    // The following investigation checks the long term behavior of the sum:

    var currentPlants = plants
    var previous = currentPlants
    for (i in 1..500) {
        currentPlants = previous.after(1)
        val delta = currentPlants.sumOfPotNumbers() - previous.sumOfPotNumbers()
        println("Step $i, ${currentPlants.numPots()} pots, sum ${currentPlants.sumOfPotNumbers()}, delta $delta")
        previous = currentPlants
    }

    // After step 124 there are always 109 pots, and after step 126 the sum always increases by 109 per step.
    // Step 126 has a sum of 14900, so...
    val longRunSum = { stepNum: Long -> (stepNum - 126) * 109 + 14900 }

    println(longRunSum(50_000_000_000))
}