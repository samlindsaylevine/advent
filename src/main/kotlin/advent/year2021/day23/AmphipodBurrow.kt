package advent.year2021.day23

import advent.utils.*
import kotlin.Pair
import kotlin.to

/**
 * Note - the below code probably all works, but is way too slow. There are probably more optimizations I couldn't
 * think of explicitly. Also, my representation of the state might be too big; instead of tracking each amphipod and
 * space maybe I should just be tracking "stacks" and holding spaces, and consider moving off the top of a stack, or
 * onto the top of a stack?
 *
 * Instead, I just worked it out by hand! I'm not proud.
 *
 * Someone posted a helpful simulator at https://aochelper2021.blob.core.windows.net/day23/index.html
 *
 * --- Day 23: Amphipod ---
 * A group of amphipods notice your fancy submarine and flag you down. "With such an impressive shell," one amphipod
 * says, "surely you can help us with a question that has stumped our best scientists."
 * They go on to explain that a group of timid, stubborn amphipods live in a nearby burrow. Four types of amphipods
 * live there: Amber (A), Bronze (B), Copper (C), and Desert (D). They live in a burrow that consists of a hallway and
 * four side rooms. The side rooms are initially full of amphipods, and the hallway is initially empty.
 * They give you a diagram of the situation (your puzzle input), including locations of each amphipod (A, B, C, or D,
 * each of which is occupying an otherwise open space), walls (#), and open space (.).
 * For example:
 * #############
 * #...........#
 * ###B#C#B#D###
 *   #A#D#C#A#
 *   #########
 *
 * The amphipods would like a method to organize every amphipod into side rooms so that each side room contains one
 * type of amphipod and the types are sorted A-D going left to right, like this:
 * #############
 * #...........#
 * ###A#B#C#D###
 *   #A#B#C#D#
 *   #########
 *
 * Amphipods can move up, down, left, or right so long as they are moving into an unoccupied open space. Each type of
 * amphipod requires a different amount of energy to move one step: Amber amphipods require 1 energy per step, Bronze
 * amphipods require 10 energy, Copper amphipods require 100, and Desert ones require 1000. The amphipods would like
 * you to find a way to organize the amphipods that requires the least total energy.
 * However, because they are timid and stubborn, the amphipods have some extra rules:
 *
 * Amphipods will never stop on the space immediately outside any room. They can move into that space so long as they
 * immediately continue moving. (Specifically, this refers to the four open spaces in the hallway that are directly
 * above an amphipod starting position.)
 * Amphipods will never move from the hallway into a room unless that room is their destination room and that room
 * contains no amphipods which do not also have that room as their own destination. If an amphipod's starting room is
 * not its destination room, it can stay in that room until it leaves the room. (For example, an Amber amphipod will
 * not move from the hallway into the right three rooms, and will only move into the leftmost room if that room is
 * empty or if it only contains other Amber amphipods.)
 * Once an amphipod stops moving in the hallway, it will stay in that spot until it can move into a room. (That is,
 * once any amphipod starts moving, any other amphipods currently in the hallway are locked in place and will not move
 * again until they can move fully into a room.)
 *
 * In the above example, the amphipods can be organized using a minimum of 12521 energy. One way to do this is shown
 * below.
 * Starting configuration:
 * #############
 * #...........#
 * ###B#C#B#D###
 *   #A#D#C#A#
 *   #########
 *
 * One Bronze amphipod moves into the hallway, taking 4 steps and using 40 energy:
 * #############
 * #...B.......#
 * ###B#C#.#D###
 *   #A#D#C#A#
 *   #########
 *
 * The only Copper amphipod not in its side room moves there, taking 4 steps and using 400 energy:
 * #############
 * #...B.......#
 * ###B#.#C#D###
 *   #A#D#C#A#
 *   #########
 *
 * A Desert amphipod moves out of the way, taking 3 steps and using 3000 energy, and then the Bronze amphipod takes its
 * place, taking 3 steps and using 30 energy:
 * #############
 * #.....D.....#
 * ###B#.#C#D###
 *   #A#B#C#A#
 *   #########
 *
 * The leftmost Bronze amphipod moves to its room using 40 energy:
 * #############
 * #.....D.....#
 * ###.#B#C#D###
 *   #A#B#C#A#
 *   #########
 *
 * Both amphipods in the rightmost room move into the hallway, using 2003 energy in total:
 * #############
 * #.....D.D.A.#
 * ###.#B#C#.###
 *   #A#B#C#.#
 *   #########
 *
 * Both Desert amphipods move into the rightmost room using 7000 energy:
 * #############
 * #.........A.#
 * ###.#B#C#D###
 *   #A#B#C#D#
 *   #########
 *
 * Finally, the last Amber amphipod moves into its room, using 8 energy:
 * #############
 * #...........#
 * ###A#B#C#D###
 *   #A#B#C#D#
 *   #########
 *
 * What is the least energy required to organize the amphipods?
 *
 * --- Part Two ---
 * As you prepare to give the amphipods your solution, you notice that the diagram they handed you was actually folded
 * up. As you unfold it, you discover an extra part of the diagram.
 * Between the first and second lines of text that contain amphipod starting positions, insert the following lines:
 *   #D#C#B#A#
 *   #D#B#A#C#
 *
 * So, the above example now becomes:
 * #############
 * #...........#
 * ###B#C#B#D###
 *   #D#C#B#A#
 *   #D#B#A#C#
 *   #A#D#C#A#
 *   #########
 *
 * The amphipods still want to be organized into rooms similar to before:
 * #############
 * #...........#
 * ###A#B#C#D###
 *   #A#B#C#D#
 *   #A#B#C#D#
 *   #A#B#C#D#
 *   #########
 *
 * In this updated example, the least energy required to organize these amphipods is 44169:
 * #############
 * #...........#
 * ###B#C#B#D###
 *   #D#C#B#A#
 *   #D#B#A#C#
 *   #A#D#C#A#
 *   #########
 *
 * #############
 * #..........D#
 * ###B#C#B#.###
 *   #D#C#B#A#
 *   #D#B#A#C#
 *   #A#D#C#A#
 *   #########
 *
 * #############
 * #A.........D#
 * ###B#C#B#.###
 *   #D#C#B#.#
 *   #D#B#A#C#
 *   #A#D#C#A#
 *   #########
 *
 * #############
 * #A........BD#
 * ###B#C#.#.###
 *   #D#C#B#.#
 *   #D#B#A#C#
 *   #A#D#C#A#
 *   #########
 *
 * #############
 * #A......B.BD#
 * ###B#C#.#.###
 *   #D#C#.#.#
 *   #D#B#A#C#
 *   #A#D#C#A#
 *   #########
 *
 * #############
 * #AA.....B.BD#
 * ###B#C#.#.###
 *   #D#C#.#.#
 *   #D#B#.#C#
 *   #A#D#C#A#
 *   #########
 *
 * #############
 * #AA.....B.BD#
 * ###B#.#.#.###
 *   #D#C#.#.#
 *   #D#B#C#C#
 *   #A#D#C#A#
 *   #########
 *
 * #############
 * #AA.....B.BD#
 * ###B#.#.#.###
 *   #D#.#C#.#
 *   #D#B#C#C#
 *   #A#D#C#A#
 *   #########
 *
 * #############
 * #AA...B.B.BD#
 * ###B#.#.#.###
 *   #D#.#C#.#
 *   #D#.#C#C#
 *   #A#D#C#A#
 *   #########
 *
 * #############
 * #AA.D.B.B.BD#
 * ###B#.#.#.###
 *   #D#.#C#.#
 *   #D#.#C#C#
 *   #A#.#C#A#
 *   #########
 *
 * #############
 * #AA.D...B.BD#
 * ###B#.#.#.###
 *   #D#.#C#.#
 *   #D#.#C#C#
 *   #A#B#C#A#
 *   #########
 *
 * #############
 * #AA.D.....BD#
 * ###B#.#.#.###
 *   #D#.#C#.#
 *   #D#B#C#C#
 *   #A#B#C#A#
 *   #########
 *
 * #############
 * #AA.D......D#
 * ###B#.#.#.###
 *   #D#B#C#.#
 *   #D#B#C#C#
 *   #A#B#C#A#
 *   #########
 *
 * #############
 * #AA.D......D#
 * ###B#.#C#.###
 *   #D#B#C#.#
 *   #D#B#C#.#
 *   #A#B#C#A#
 *   #########
 *
 * #############
 * #AA.D.....AD#
 * ###B#.#C#.###
 *   #D#B#C#.#
 *   #D#B#C#.#
 *   #A#B#C#.#
 *   #########
 *
 * #############
 * #AA.......AD#
 * ###B#.#C#.###
 *   #D#B#C#.#
 *   #D#B#C#.#
 *   #A#B#C#D#
 *   #########
 *
 * #############
 * #AA.......AD#
 * ###.#B#C#.###
 *   #D#B#C#.#
 *   #D#B#C#.#
 *   #A#B#C#D#
 *   #########
 *
 * #############
 * #AA.......AD#
 * ###.#B#C#.###
 *   #.#B#C#.#
 *   #D#B#C#D#
 *   #A#B#C#D#
 *   #########
 *
 * #############
 * #AA.D.....AD#
 * ###.#B#C#.###
 *   #.#B#C#.#
 *   #.#B#C#D#
 *   #A#B#C#D#
 *   #########
 *
 * #############
 * #A..D.....AD#
 * ###.#B#C#.###
 *   #.#B#C#.#
 *   #A#B#C#D#
 *   #A#B#C#D#
 *   #########
 *
 * #############
 * #...D.....AD#
 * ###.#B#C#.###
 *   #A#B#C#.#
 *   #A#B#C#D#
 *   #A#B#C#D#
 *   #########
 *
 * #############
 * #.........AD#
 * ###.#B#C#.###
 *   #A#B#C#D#
 *   #A#B#C#D#
 *   #A#B#C#D#
 *   #########
 *
 * #############
 * #..........D#
 * ###A#B#C#.###
 *   #A#B#C#D#
 *   #A#B#C#D#
 *   #A#B#C#D#
 *   #########
 *
 * #############
 * #...........#
 * ###A#B#C#D###
 *   #A#B#C#D#
 *   #A#B#C#D#
 *   #A#B#C#D#
 *   #########
 *
 * Using the initial configuration from the full diagram, what is the least energy required to organize the amphipods?
 *
 */
class AmphipodBurrow(
  val amphipods: Set<Amphipod>,
  val layout: AmphipodLayout
) {
  constructor(input: String) : this(input.lines().flatMapIndexed { y, line ->
    line.mapIndexedNotNull { x, c -> if (c == '#' || c == ' ' || c == '.') null else Amphipod(c, Point(x, y)) }
  }.toSet(), AmphipodLayout(input))

  fun nextSteps(): Set<Step<AmphipodBurrow>> {
    val legalMoves: List<Pair<Amphipod, AmphipodMove>> = amphipods.flatMap { amphipod ->
      val moves: List<AmphipodMove> = (layout.moves[amphipod.position] ?: emptyList())
        .filter { amphipod.canMove(it) }

      moves.map { amphipod to it }
    }

    fun Pair<Amphipod, AmphipodMove>.isToHomeSpace() = first.type == layout.spacesByLocation[second.target]?.room

    // If we can send an amphipod home, always do it and ignore all other possibilities.
    val optimalMoves = if (legalMoves.any { it.isToHomeSpace() }) {
      legalMoves.filter { it.isToHomeSpace() }
    } else {
      legalMoves
    }

    return optimalMoves.map { (amphipod, move) ->
      Step(next = this.withMove(amphipod, move), cost = move.steps * amphipod.multiplier)
    }.toSet()
  }

  private fun Amphipod.canMove(move: AmphipodMove): Boolean {
    // An amphipod already in the bottom space of its room will never move again.
    if (layout.spacesByLocation[this.position]?.room == this.type && this.position == layout.rooms[this.type]?.last()) {
      return false
    }

    // If this amphipod is in its room, and all the spaces below it are filled by amphipods also already in their
    // correct room, it also will never move again.
    if (layout.spacesByLocation[this.position]?.room == this.type &&
      amphipodsByLocation[layout.rooms[this.type]?.last()]?.type == this.type
    ) {
      return false
    }

    // The amphipod will never move into a room unless it is their room, and the other spaces in the room are filled by
    // correct amphipods.
    val targetRoom = layout.spacesByLocation[move.target]?.room
    if (targetRoom != null && targetRoom != this.type) {
      return false
    }
    if (targetRoom != null) {
      val roomSpaces = layout.rooms[targetRoom] ?: throw IllegalStateException("Missing room")
      val amphipodsInRoom = roomSpaces.mapNotNull { amphipodsByLocation[it] }
      // We won't move into the top space in our room if it is empty, only the bottom one.
      if (amphipodsInRoom.isEmpty() && move.target == roomSpaces.first()) {
        return false
      }
      if (amphipodsInRoom.any { it.type != this.type }) {
        return false
      }
    }

    // An amphipod in a hallway can only move into its room, not another hallway space.
    val currentRoom = layout.spacesByLocation[this.position]
    if (targetRoom == null && currentRoom == null) {
      return false
    }

    // All the intermediate spaces in the move must be clear.
    return move.requiredClear.none { it in amphipodLocations }
  }

  private fun withMove(amphipod: Amphipod, move: AmphipodMove): AmphipodBurrow {
    val newAmphipods = amphipods - amphipod + amphipod.copy(position = move.target)
    return AmphipodBurrow(amphipods = newAmphipods, layout = this.layout)
  }

  private val amphipodLocations by lazy { amphipods.map { it.position } }
  private val amphipodsByLocation by lazy { amphipods.associateBy { it.position } }

  private fun isDone() = amphipods.all { layout.spacesByLocation[it.position]?.room == it.type }

  fun organizationCost(): Int {
    val organization = ShortestPathFinder().find(
      start = this,
      end = EndCondition { it.isDone() },
      nextSteps = StepsWithCost { it.nextSteps() }, reportEvery = 10
    )

    return organization.first().totalCost
  }
}

data class Amphipod(val type: Char, val position: Point) {
  val multiplier = when (type) {
    'A' -> 1
    'B' -> 10
    'C' -> 100
    'D' -> 1000
    else -> throw IllegalArgumentException("Bad type $type")
  }
}

class AmphipodLayout(val spaces: Set<AmphipodSpace>) {
  constructor(input: String) : this(input.let {
    val points = input.lines().flatMapIndexed { y, line ->
      line.mapIndexedNotNull { x, c -> if (c == '#' || c == ' ') null else Point(x, y) }
    }

    val roomXs = points.filter { it.y > 1 }.map { it.x }.distinct().sorted()
    val xToChar = roomXs.mapIndexed { index, x -> x to ('A' + index) }.toMap()

    points.map { AmphipodSpace(it, if (it.y > 1) xToChar[it.x] else null) }.toSet()
  })

  val spacesByLocation: Map<Point, AmphipodSpace> by lazy {
    spaces.associateBy { it.point }
  }

  val rooms: Map<Char, List<Point>> = spaces.filter { it.room != null }
    .groupBy { it.room }
    .map { (room, spaces) -> room!! to spaces.map { it.point }.sortedBy { it.y } }
    .toMap()

  private val targetSpaces by lazy {
    val roomXs = spaces.filter { it.room != null }.map { it.point.x }
    spaces.filter { it.point.y > 1 || it.point.x !in roomXs }.toSet()
  }

  // In doing our search, instead of going space-to-space, we will consider "moves", to deal with the rule that
  // amphipods don't go back into a room once they have stopped moving until they are done.
  // This is a map of moves from a starting point.
  val moves: Map<Point, List<AmphipodMove>> by lazy {
    spaces.associate { it.point to movesStartingFrom(it) }
      .also { println("Finished calculating move options") }
  }

  private fun movesStartingFrom(start: AmphipodSpace): List<AmphipodMove> {
    return targetSpaces.filter { it != start }
      .map { move(from = start, to = it) }
  }

  private fun move(from: AmphipodSpace, to: AmphipodSpace): AmphipodMove {
    val path = ShortestPathFinder().find(
      start = from,
      end = EndState(to),
      nextSteps = Steps { current -> current.point.adjacentNeighbors.mapNotNull { spacesByLocation[it] }.toSet() },
      reportEvery = 1
    )
      .first()

    return AmphipodMove(
      target = to.point,
      steps = path.totalCost,
      requiredClear = path.steps.map { it.point }.toSet()
    )
  }

}


data class AmphipodSpace(val point: Point, val room: Char?)

/**
 * Represents a move from a space to another space.
 */
data class AmphipodMove(
  val target: Point,
  val steps: Int,
  /**
   * This move can only be executed if the provided spaces are all empty.
   */
  val requiredClear: Set<Point>
)