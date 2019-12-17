package advent.year2019.day17

import advent.utils.Direction
import advent.utils.Point
import advent.utils.times
import advent.year2019.day13.parseIntcodeFromFile
import advent.year2019.day5.IntcodeComputer

class Scaffolding(private val scaffolds: Set<Point>,
                  private val robot: Point,
                  private val robotDirection: Direction) {
    companion object {
        fun parse(input: String): Scaffolding {
            val scaffolds = mutableSetOf<Point>()
            var robot: Point? = null
            var robotDirection: Direction? = null

            input.trim().lines().forEachIndexed { y, line ->
                line.forEachIndexed { x, c ->
                    if (c in setOf('#', '^', '<', '>', 'v')) scaffolds.add(Point(x, y))
                    if (c in setOf('^', '<', '>', 'v')) robot = Point(x, y)
                    when (c) {
                        // Note that north is *down* here becase that is the direction of increasing Y.
                        '^' -> robotDirection = Direction.S
                        '<' -> robotDirection = Direction.W
                        '>' -> robotDirection = Direction.E
                        'V' -> robotDirection = Direction.N
                    }
                }
            }

            return Scaffolding(scaffolds,
                    robot ?: throw IllegalStateException("Robot not found"),
                    robotDirection ?: throw IllegalStateException("Robot not found"))
        }
    }

    fun alignmentParameterSum(): Int = scaffolds.filter { scaffolds.containsAll(it.adjacentNeighbors) }
            .sumBy { it.x * it.y }

    fun directions() = directions(emptyList(),
            robot,
            robotDirection)

    private tailrec fun directions(soFar: List<String>,
                                   position: Point,
                                   direction: Direction): List<String> {
        if (scaffolds.contains(position + direction.toPoint())) {
            val steps = countSteps(position, direction)
            return directions(soFar + steps.toString(),
                    position + steps * direction.toPoint(),
                    direction)
        }

        val newNeighbors = position.adjacentNeighbors.filter {
            scaffolds.contains(it) &&
                    it != position - direction.toPoint()
        }

        if (newNeighbors.isEmpty()) return soFar

        // Because we reversed the definition of N & S (N points down) we also reverse the turn definitions.
        val turnOptions = mapOf("R" to Direction::left,
                "L" to Direction::right)

        val turn = turnOptions.map { (instruction, update) -> instruction to update(direction) }
                .first { newNeighbors.contains(position + it.second.toPoint()) }

        return directions(soFar + turn.first,
                position,
                turn.second)
    }

    private fun countSteps(position: Point,
                           direction: Direction) = generateSequence(1) { it + 1 }
            .takeWhile { scaffolds.contains(position + it * direction.toPoint()) }
            .last()
}

class AftScaffoldingControlAndInformationInterface(val program: List<Long>) {
    fun currentView(): String {
        val computer = IntcodeComputer()

        val result = StringBuilder()

        computer.execute(program,
                { throw IllegalStateException("No input available") },
                { result.append(it.toChar()) })

        return result.toString()
    }

    fun driveRobot(mainRoutine: List<String>,
                   functionA: List<String>,
                   functionB: List<String>,
                   functionC: List<String>): Long {
        val newProgram = program.toMutableList()
        newProgram[0] = 2

        var result: Long? = null
        val computer = IntcodeComputer()

        checkLength(mainRoutine)
        checkLength(functionA)
        checkLength(functionB)
        checkLength(functionC)

        val instructions: List<Long> = mainRoutine.toInstructions() + 10 +
                functionA.toInstructions() + 10 +
                functionB.toInstructions() + 10 +
                functionC.toInstructions() + 10 +
                'n'.toLong() + 10

        computer.execute(newProgram,
                instructions.asInput(),
                { result = it })

        return result ?: throw IllegalStateException("Never got any output")
    }

    private fun List<String>.toInstructions() = this.joinToString(",").toCharArray().map { it.toLong() }
    private fun checkLength(routine: List<String>) {
        require(routine.toInstructions().size <= 20) { "Routine $routine is too long"}
    }
}

fun <T> List<T>.asInput(): () -> T {
    var index = 0
    return {
        val result = this[index]
        index ++
        result
    }
}

fun main() {
    val input = parseIntcodeFromFile("src/main/kotlin/advent/year2019/day17/input.txt")

    val ascii = AftScaffoldingControlAndInformationInterface(input)

    val scaffoldingString = ascii.currentView()

    println(scaffoldingString)

    val scaffolding = Scaffolding.parse(scaffoldingString)

    println("Alignment parameter sum:")
    println(scaffolding.alignmentParameterSum())
    println()
    val directions = scaffolding.directions()
    println("Directions:")
    println(scaffolding.directions())

    // I'm not going to bother with writing code to parse the directions and come up with subroutines.
    // The problem could be a little tricky because we might want to throw in extra turns - e.g., if the fastest
    // path has an "L", we might in theory want to execute "L, R, L" or even "R, R, R" if that lets us repeat a
    // subroutine and save calls. We might even backtrack if that saves us characters!

    // Here's our set of directions:
    //[L, 10, R, 8, R, 8, L, 10, R, 8, R, 8, L, 10, L, 12, R, 8, R, 10, R, 10, L, 12, R, 10,
    // L, 10, L, 12, R, 8, R, 10, R, 10, L, 12, R, 10, L, 10, L, 12, R, 8, R, 10, R, 10,
    // L, 12, R, 10, R, 10, L, 12, R, 10, L, 10, R, 8, R, 8]
    //
    // Inspecting manually, we can see the repeating patterns:
    // A: L, 10, R, 8, R, 8
    // B: L, 10, L, 12, R, 8, R, 10
    // C: R, 10, L, 12, R, 10
    //
    // Giving an instruction list of
    // [A, A, B, C, B, C, B, C, C, A]
    //
    // I guess it turns out we didn't need to do any extra turns or backtracking after all.

    val a = listOf("L", "10", "R", "8", "R", "8")
    val b = listOf("L", "10", "L", "12", "R", "8", "R", "10")
    val c = listOf("R", "10", "L", "12", "R", "10")

    val instructions = listOf("A", "A", "B", "C", "B", "C", "B", "C", "C", "A")

    // Let's prove that this is right.
    val expandedDirections = instructions.flatMap {
        when (it) {
            "A" -> a
            "B" -> b
            "C" -> c
            else -> throw IllegalArgumentException("Bad instruction $it")
        }
    }

    if (expandedDirections == directions) {
        println("Error checking on instruction set passed.")
    } else {
        throw IllegalStateException("Error checking failed: actual $expandedDirections, expected $directions")
    }

    val dust = ascii.driveRobot(instructions, a, b, c)

    println()
    println("Dust total:")
    println(dust)
}