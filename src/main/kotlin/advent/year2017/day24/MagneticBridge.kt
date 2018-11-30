package advent.year2017.day24

import com.google.common.collect.*
import java.io.File

data class MagneticBridge(private val components: List<BridgeComponent>) {
    fun strength() = components.sumBy { it.left + it.right }

    operator fun plus(other: MagneticBridge) = MagneticBridge(this.components + other.components)
}

data class BridgeComponent(val left: Int, val right: Int) {

    companion object {
        private val REGEX = "(\\d+)/(\\d+)".toRegex()
        fun parse(input: String): BridgeComponent {
            val match = REGEX.matchEntire(input.trim())
                    ?: throw IllegalArgumentException("Unparseable component $input")

            return BridgeComponent(match.groupValues[1].toInt(), match.groupValues[2].toInt())
        }
    }


    fun reversed() = BridgeComponent(right, left)
}

class ComponentPile private constructor(private val componentsByLeftPort: ListMultimap<Int, BridgeComponent>) {

    constructor(input: String) : this(input.trim().lines()
            .map { BridgeComponent.parse(it) })

    /**
     * This is a lookup optimization, so that we can see which components are available for a given port
     * number. Each component will appear twice in this map,
     */
    private constructor(components: List<BridgeComponent>) : this(components.asSequence()
            .flatMap { component -> sequenceOf(component, component.reversed()) }
            .fold(MultimapBuilder.hashKeys().arrayListValues().build<Int, BridgeComponent>())
            { map, component ->
                map.put(component.left, component)
                map
            })

    private fun without(component: BridgeComponent): ComponentPile {
        val newMap = ArrayListMultimap.create(componentsByLeftPort)
        newMap.remove(component.left, component)
        newMap.remove(component.right, component.reversed())
        return ComponentPile(newMap)
    }

    private fun bridgesStartingWith(port: Int): Sequence<MagneticBridge> {
        val possibleNextComponents = this.componentsByLeftPort[port]

        return possibleNextComponents.asSequence()
                .flatMap { component ->
                    val singleComponentBridge = MagneticBridge(listOf(component))

                    sequenceOf(singleComponentBridge) +
                            this.without(component).bridgesStartingWith(component.right).map { singleComponentBridge + it }
                }
    }

    fun strongestBridge(): MagneticBridge = bridgesStartingWith(0).maxBy { it.strength() }
            ?: MagneticBridge(listOf())

}


fun main(args: Array<String>) {
    val input = File("src/main/kotlin/advent/year2017/day24/input.txt")
            .readText()
            .trim()

    val pile = ComponentPile(input)

    println(pile.strongestBridge().strength())
}