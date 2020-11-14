package advent.year2017.day7

import java.io.File

class RecursiveCircus(lines: Sequence<String>) {

    constructor(text: String) : this(text
            .split("\n")
            .asSequence())

    private val nodesByName = mutableMapOf<String, CircusNode>()

    init {
        lines.map { it.trim() }
                .filter { it.isNotEmpty() }
                .map { CircusProgram.fromText(it) }
                .forEach { this.addProgram(it) }
    }

    fun bottomProgramName(): String? = bottomProgram()?.name

    fun newWeightToBalance(): Int? = bottomProgram()?.weightToBalance()

    private fun bottomProgram(): CircusNode? = nodesByName.values.find { it.parent == null }

    private fun addProgram(program: CircusProgram) {
        val node = getOrAddNode(program.name)
        node.weight = program.weight
        node.children = program.childNames.map {
            val childNode = getOrAddNode(it)
            childNode.parent = node
            childNode
        }
    }

    /**
     * When we hit forward references, we are inserting a placeholder node that we will update later.
     *
     * We assume that the dataset is consistent and will leave weird placeholder nodes if it is not.
     */
    private fun getOrAddNode(name: String) = nodesByName.computeIfAbsent(name,
            { CircusNode(name, 0, null, listOf()) })

    /**
     * Represents one line of input.
     */
    data class CircusProgram(val name: String, val weight: Int, val childNames: List<String>) {
        companion object {
            private val REGEX = "(\\w+) \\((\\d+)\\)( -> )?(.*)".toRegex()

            fun fromText(text: String): CircusProgram {
                val match = REGEX.matchEntire(text) ?: throw IllegalArgumentException("Unrecognized program yelling $text")

                val childrenText = match.groupValues[4].trim()
                val children = if (childrenText.isEmpty()) listOf() else match.groupValues[4].split(", ")

                return CircusProgram(match.groupValues[1], match.groupValues[2].toInt(),
                        children)
            }
        }
    }

    /**
     * Mutable because of the placeholder inserting and later updating that we do; private so this mutability can't
     * leak out to users of the Circus.
     */
    private data class CircusNode(val name: String,
                                  var weight: Int,
                                  var parent: CircusNode?,
                                  var children: List<CircusNode>) {
        fun totalWeight(): Int = weight + children.map { it.totalWeight() }.sum()

        fun weightToBalance(weightIShouldBe: Int = 0): Int? {
            val unbalanced = this.unbalancedChildAndWeightItShouldBe()

            return if (unbalanced == null) {
                val childTotalWeight = children.map { it.totalWeight() }.sum()
                val newWeight = weightIShouldBe - childTotalWeight
                debug("I am $name, My children are balanced at weight $childTotalWeight, I should be weight $newWeight")
                newWeight
            } else {
                debug("I am $name, My child ${unbalanced.first.name} should be weight ${unbalanced.second}")
                unbalanced.first.weightToBalance(unbalanced.second)
            }
        }

        fun unbalancedChildAndWeightItShouldBe(): Pair<CircusNode, Int>? {
            val weights = children.map { Pair(it, it.totalWeight()) }
            debug(weights.map { Pair(it.first.name, it.second) })
            val mostCommonWeight = mode(weights.map { it.second })
            val unbalancedChild = weights.find { it.second != mostCommonWeight }?.first
            return unbalancedChild?.let { Pair(it, mostCommonWeight!!) }
        }

        private fun <T> mode(input: Collection<T>): T? {
            val counts = input.groupingBy { it }.eachCount()
            val maxCount = counts.values.maxOrNull()
            return counts.entries.find { it.value == maxCount }?.key
        }

        private fun debug(@Suppress("UNUSED_PARAMETER") line: Any) {
            // Uncomment (and remove annotation above) for debug information.
            // Used during development but didn't want to junk up output.
            // println(line)
        }
    }
}

fun main() {
    val circus = File("src/main/kotlin/advent/year2017/day7/input.txt")
            .useLines { RecursiveCircus(it) }

    println(circus.bottomProgramName())
    println(circus.newWeightToBalance())
}