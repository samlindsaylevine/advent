package advent.year2017.day24

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.ListMultimap
import com.google.common.collect.MultimapBuilder
import java.io.File
import java.util.Comparator.comparing

/**
 * --- Day 24: Electromagnetic Moat ---
 * The CPU itself is a large, black building surrounded by a bottomless pit. Enormous metal tubes extend outward from
 * the side of the building at regular intervals and descend down into the void. There's no way to cross, but you need
 * to get inside.
 * No way, of course, other than building a bridge out of the magnetic components strewn about nearby.
 * Each component has two ports, one on each end.  The ports come in all different types, and only matching types can
 * be connected.  You take an inventory of the components by their port types (your puzzle input). Each port is
 * identified by the number of pins it uses; more pins mean a stronger connection for your bridge. A 3/7 component, for
 * example, has a type-3 port on one side, and a type-7 port on the other.
 * Your side of the pit is metallic; a perfect surface to connect a magnetic, zero-pin port. Because of this, the first
 * port you use must be of type 0. It doesn't matter what type of port you end with; your goal is just to make the
 * bridge as strong as possible.
 * The strength of a bridge is the sum of the port types in each component. For example, if your bridge is made of
 * components 0/3, 3/7, and 7/4, your bridge has a strength of 0+3 + 3+7 + 7+4 = 24.
 * For example, suppose you had the following components:
 * 0/2
 * 2/2
 * 2/3
 * 3/4
 * 3/5
 * 0/1
 * 10/1
 * 9/10
 * 
 * With them, you could make the following valid bridges:
 * 
 * 0/1
 * 0/1--10/1
 * 0/1--10/1--9/10
 * 0/2
 * 0/2--2/3
 * 0/2--2/3--3/4
 * 0/2--2/3--3/5
 * 0/2--2/2
 * 0/2--2/2--2/3
 * 0/2--2/2--2/3--3/4
 * 0/2--2/2--2/3--3/5
 * 
 * (Note how, as shown by 10/1, order of ports within a component doesn't matter. However, you may only use each port
 * on a component once.)
 * Of these bridges, the strongest one is 0/1--10/1--9/10; it has a strength of 0+1 + 1+10 + 10+9 = 31.
 * What is the strength of the strongest bridge you can make with the components you have available?
 * 
 * --- Part Two ---
 * The bridge you've built isn't long enough; you can't jump the rest of the way.
 * In the example above, there are two longest bridges:
 * 
 * 0/2--2/2--2/3--3/4
 * 0/2--2/2--2/3--3/5
 * 
 * Of them, the one which uses the 3/5 component is stronger; its strength is 0+2 + 2+2 + 2+3 + 3+5 = 19.
 * What is the strength of the longest bridge you can make? If you can make multiple bridges of the longest length,
 * pick the strongest one.
 * 
 */
data class MagneticBridge(private val components: List<BridgeComponent>) {
  fun strength() = components.sumBy { it.left + it.right }
  fun length() = components.size

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

  private fun allBridges() = bridgesStartingWith(0)

  fun strongestBridge(): MagneticBridge = allBridges()
          .maxByOrNull { it.strength() }
          ?: MagneticBridge(listOf())

  fun longestBridge(): MagneticBridge = allBridges()
          .maxWithOrNull(comparing(MagneticBridge::length).thenComparing(MagneticBridge::strength))
          ?: MagneticBridge(listOf())

}


fun main() {
  val input = File("src/main/kotlin/advent/year2017/day24/input.txt")
          .readText()
          .trim()

  val pile = ComponentPile(input)

  println(pile.strongestBridge().strength())

  println(pile.longestBridge().strength())
}