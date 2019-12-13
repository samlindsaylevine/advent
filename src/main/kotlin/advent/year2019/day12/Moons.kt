package advent.year2019.day12

import advent.year2015.day24.Ticker
import advent.year2019.day10.gcd

class Moons(private val moons: List<Moon>) {

    fun get() = moons

    fun advance() {
        moons.allPairs().forEach { it.first.attract(it.second) }
        moons.forEach { it.move() }
    }

    fun totalEnergy() = moons.sumBy { it.totalEnergy() }

    private fun copyMoons() = moons.map {
        Moon(position = it.position.copy(),
                velocity = it.velocity.copy())
    }

    fun stepsToRepeat() = listOf(periodOfAxis { it.x }, periodOfAxis { it.y }, periodOfAxis { it.z }).reduce(::lcm)

    // Left over from some previous experimentation.
    @Suppress("unused")
    fun stepsToPermute() = stepsUntil { initial, current -> initial.toSet() == current.toSet() }

    // Left over from some previous experimentation.
    @Suppress("unused")
    fun periodOfMoon(index: Int) = stepsUntil { initial, current -> initial[index] == current[index] }

    private fun periodOfAxis(selector: (ThreeDVector) -> Int) =
            stepsUntil { initial, current -> axisMatches(initial, current, selector) }

    private fun axisMatches(initial: List<Moon>,
                            current: List<Moon>,
                            selector: (ThreeDVector) -> Int): Boolean {
        val positionsMatch = (initial.map { selector(it.position) } == current.map { selector(it.position) })
        val velocitiesMatch = (initial.map { selector(it.velocity) } == current.map { selector(it.velocity) })
        return positionsMatch && velocitiesMatch
    }

    private fun stepsUntil(condition: (List<Moon>, List<Moon>) -> Boolean): Long {
        val initial = copyMoons()
        val current = Moons(copyMoons())
        val ticker = Ticker(10_000_000)
        var stepsTaken = 0L

        do {
            current.advance()
            stepsTaken++
            ticker.tick()
        } while (!condition(initial, current.moons))

        return stepsTaken
    }
}

data class Moon(val position: ThreeDVector,
                val velocity: ThreeDVector = ThreeDVector(0, 0, 0)) {
    constructor(x: Int, y: Int, z: Int) : this(ThreeDVector(x, y, z))

    private fun potentialEnergy() = Math.abs(position.x) + Math.abs(position.y) + Math.abs(position.z)
    private fun kineticEnergy() = Math.abs(velocity.x) + Math.abs(velocity.y) + Math.abs(velocity.z)
    fun totalEnergy() = potentialEnergy() * kineticEnergy()

    fun attract(other: Moon) {
        attractAxis(other, { it.x }, { v, i -> v.x += i })
        attractAxis(other, { it.y }, { v, i -> v.y += i })
        attractAxis(other, { it.z }, { v, i -> v.z += i })
    }

    private fun attractAxis(other: Moon,
                            positionGetter: (ThreeDVector) -> Int,
                            velocityIncrementor: (ThreeDVector, Int) -> Unit) {
        val positionDelta = positionGetter(other.position) - positionGetter(this.position)

        val velocityDelta = when {
            positionDelta < 0 -> -1
            positionDelta == 0 -> 0
            else -> 1
        }

        velocityIncrementor(this.velocity, velocityDelta)
        velocityIncrementor(other.velocity, -velocityDelta)
    }

    fun move() {
        this.position += this.velocity
    }
}

data class ThreeDVector(var x: Int, var y: Int, var z: Int) {
    operator fun plusAssign(other: ThreeDVector) {
        this.x += other.x
        this.y += other.y
        this.z += other.z
    }
}

fun <T> List<T>.allPairs(): List<Pair<T, T>> =
        (0 until this.size).flatMap { i ->
            (0 until i).map { j -> this[j] to this[i] }
        }

private fun lcm(x: Long, y: Long) = x * y / gcd(x, y)

private fun input() = Moons(listOf(
        Moon(-17, 9, -5),
        Moon(-1, 7, 13),
        Moon(-19, 12, 5),
        Moon(-6, -6, -4)
))

private fun partOne(): Int {
    val moons = input()

    repeat(1000) { moons.advance() }

    return (moons.totalEnergy())
}

// This is a little tricky. I tried the brute force, "CPU time is cheaper than developer time" approach, and could
// complete the reference "large value" in about 20 minutes, but I left it on overnight for the puzzle input and it
// got up to 40950000000 without completing.
//
// I considered whether each moon might be on its own independent loop and the total period would be the product
// (or product divided by GCD, i.e. LCM), but for the first reference example, several of the moons took the full 2772,
// and for the second, they had at least 80 million before I stopped.
//
// I thought maybe the moons might swap states - permute - but otherwise be identical, and then we could compose several
// of the permutations to get to the original state. That also did not appear to be the case.
//
// After some more consideration, I decided the LCM was on the right track, but not of the moons - of the *axes*.
// Since the axes do not actually interact with each other at all, if they cycle on independent periods, then the entire
// state should repeat when the axes' cycles line up!
private fun partTwo(): Long {
    val moons = input()

    return moons.stepsToRepeat()
}

fun main() {
    println(partOne())
    println(partTwo())
}