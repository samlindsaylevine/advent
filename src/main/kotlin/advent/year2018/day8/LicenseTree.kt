package advent.year2018.day8

import java.io.File

/**
 * --- Day 8: Memory Maneuver ---
 * The sleigh is much easier to pull than you'd expect for something its weight. Unfortunately, neither you nor the
 * Elves know which way the North Pole is from here.
 * You check your wrist device for anything that might help.  It seems to have some kind of navigation system! 
 * Activating the navigation system produces more bad news: "Failed to start navigation system. Could not read software
 * license file."
 * The navigation system's license file consists of a list of numbers (your puzzle input).  The numbers define a data
 * structure which, when processed, produces some kind of tree that can be used to calculate the license number.
 * The tree is made up of nodes; a single, outermost node forms the tree's root, and it contains all other nodes in the
 * tree (or contains nodes that contain nodes, and so on).
 * Specifically, a node consists of:
 * 
 * A header, which is always exactly two numbers:
 * 
 * The quantity of child nodes.
 * The quantity of metadata entries.
 * 
 * Zero or more child nodes (as specified in the header).
 * One or more metadata entries (as specified in the header).
 * 
 * Each child node is itself a node that has its own header, child nodes, and metadata. For example:
 * 2 3 0 3 10 11 12 1 1 0 1 99 2 1 1 2
 * A----------------------------------
 *     B----------- C-----------
 *                      D-----
 * 
 * In this example, each node of the tree is also marked with an underline starting with a letter for easier
 * identification. In it, there are four nodes:
 * 
 * A, which has 2 child nodes (B, C) and 3 metadata entries (1, 1, 2).
 * B, which has 0 child nodes and 3 metadata entries (10, 11, 12).
 * C, which has 1 child node (D) and 1 metadata entry (2).
 * D, which has 0 child nodes and 1 metadata entry (99).
 * 
 * The first check done on the license file is to simply add up all of the metadata entries.  In this example, that sum
 * is 1+1+2+10+11+12+2+99=138.
 * What is the sum of all metadata entries?
 * 
 * --- Part Two ---
 * The second check is slightly more complicated: you need to find the value of the root node (A in the example above).
 * The value of a node depends on whether it has child nodes.
 * If a node has no child nodes, its value is the sum of its metadata entries. So, the value of node B is 10+11+12=33,
 * and the value of node D is 99.
 * However, if a node does have child nodes, the metadata entries become indexes which refer to those child nodes. A
 * metadata entry of 1 refers to the first child node, 2 to the second, 3 to the third, and so on. The value of this
 * node is the sum of the values of the child nodes referenced by the metadata entries. If a referenced child node does
 * not exist, that reference is skipped. A child node can be referenced multiple time and counts each time it is
 * referenced. A metadata entry of 0 does not refer to any child node.
 * For example, again using the above nodes:
 * 
 * Node C has one metadata entry, 2. Because node C has only one child node, 2 references a child node which does not
 * exist, and so the value of node C is 0.
 * Node A has three metadata entries: 1, 1, and 2. The 1 references node A's first child node, B, and the 2 references
 * node A's second child node, C. Because node B has a value of 33 and node C has a value of 0, the value of node A is
 * 33+33+0=66.
 * 
 * So, in this example, the value of the root node is 66.
 * What is the value of the root node?
 * 
 */
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

fun main() {
    val input = File("src/main/kotlin/advent/year2018/day8/input.txt")
            .readText()
            .trim()

    val tree = LicenseTree.parse(input)

    println(tree.sum())
    println(tree.value())
}