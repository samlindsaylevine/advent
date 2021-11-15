package advent.year2017.day24

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MagneticBridgeTest {

    @Test
    fun `strongestBridge -- reference input -- has reference max bridge`() {
        val pile = ComponentPile("""
            0/2
            2/2
            2/3
            3/4
            3/5
            0/1
            10/1
            9/10
        """.trimIndent())

        val strongestBridge = pile.strongestBridge()

        assertThat(strongestBridge).isEqualTo(MagneticBridge(listOf(
                BridgeComponent(0, 1),
                BridgeComponent(1, 10),
                BridgeComponent(10, 9))))
    }

    @Test
    fun `bridge strength -- reference bridge -- has reference strength`() {
        val bridge = MagneticBridge(listOf(
                BridgeComponent(0, 3),
                BridgeComponent(3, 7),
                BridgeComponent(7, 4)))

        val strength = bridge.strength()

        assertThat(strength).isEqualTo(24)
    }

}