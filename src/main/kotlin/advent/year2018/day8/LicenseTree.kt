package advent.year2018.day8

import java.io.File

data class LicenseTree(val root: Node) {

    companion object {
        fun parse(input: String): LicenseTree = LicenseTree(
                parse(input.split(" ").map { it.toInt() }, 1)
                        .nodes.first())

        /**
         * Given a list of numbers, and wanting to parse off the first some number of nodes, return those nodes and
         * also any remaining unconsumed input.
         */
        private fun parse(input: List<Int>, numNodes: Int): ParseResult {
            val firstNodeNumChildren = input[0]
            val firstNodeNumMetadataEntries = input[1]

            val (firstNode, inputBeyondFirstNode) = if (firstNodeNumChildren == 0) {
                val node = Node(listOf(), input.subList(2, 2 + firstNodeNumMetadataEntries))
                val remaining = input.subList(2 + firstNodeNumMetadataEntries, input.size)
                Pair(node, remaining)
            } else {
                val (children, beyondChildren) = parse(input.subList(2, input.size), firstNodeNumChildren)
                val metadata = beyondChildren.subList(0, firstNodeNumMetadataEntries)
                val node = Node(children, metadata)
                val remaining = beyondChildren.subList(firstNodeNumMetadataEntries, beyondChildren.size)
                Pair(node, remaining)
            }

            return if (numNodes == 1) {
                ParseResult(listOf(firstNode), inputBeyondFirstNode)
            } else {
                firstNode + parse(inputBeyondFirstNode, numNodes - 1)
            }
        }

        private operator fun Node.plus(result: ParseResult): ParseResult = ParseResult(listOf(this) + result.nodes,
                result.remainingInput)
    }

    fun sum() = root.sum()
    fun value() = root.value()

    private data class ParseResult(val nodes: List<Node>, val remainingInput: List<Int>)

    data class Node(val children: List<Node>, val metadataEntries: List<Int>) {
        fun sum(): Int = metadataEntries.sum() + children.sumBy { it.sum() }

        fun value(): Int {
            return if (children.isEmpty()) {
                metadataEntries.sum()
            } else {
                metadataEntries.sumBy {
                    // Note that these are 1-indexed so we subtract 1 to get to our 0-indexed Lists.
                    if (1 <= it && it <= children.size) children[it - 1].value() else 0
                }
            }
        }
    }
}

fun main(args: Array<String>) {
    val input = File("src/main/kotlin/advent/year2018/day8/input.txt")
            .readText()
            .trim()

    val tree = LicenseTree.parse(input)

    println(tree.sum())
    println(tree.value())
}