package advent.year2018.day8

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class LicenseTreeTest {

    private val referenceInput = "2 3 0 3 10 11 12 1 1 0 1 99 2 1 1 2"

    @Test
    fun `parse -- reference input -- reference tree`() {
        val tree = LicenseTree.parse(referenceInput)

        val nodeB = LicenseTree.Node(listOf(), listOf(10, 11, 12))
        val nodeD = LicenseTree.Node(listOf(), listOf(99))
        val nodeC = LicenseTree.Node(listOf(nodeD), listOf(2))
        val nodeA = LicenseTree.Node(listOf(nodeB, nodeC), listOf(1, 1, 2))
        val expected = LicenseTree(nodeA)

        assertThat(tree).isEqualTo(expected)
    }

    @Test
    fun `sum -- reference input -- 138`() {
        val tree = LicenseTree.parse(referenceInput)

        val sum = tree.sum()

        assertThat(sum).isEqualTo(138)
    }

    @Test
    fun `value -- reference input -- 66`() {
        val nodeB = LicenseTree.Node(listOf(), listOf(10, 11, 12))
        val nodeD = LicenseTree.Node(listOf(), listOf(99))
        val nodeC = LicenseTree.Node(listOf(nodeD), listOf(2))
        val nodeA = LicenseTree.Node(listOf(nodeB, nodeC), listOf(1, 1, 2))

        assertThat(nodeB.value()).isEqualTo(33)
        assertThat(nodeC.value()).isEqualTo(0)
        assertThat(nodeA.value()).isEqualTo(66)
    }
}