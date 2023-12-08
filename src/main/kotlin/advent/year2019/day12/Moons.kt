package advent.year2019.day12

import advent.year2015.day24.Ticker
import advent.year2019.day10.gcd

/**
 * --- Day 12: The N-Body Problem ---
 * The space near Jupiter is not a very safe place; you need to be careful of a big distracting red spot, extreme
 * radiation, and a whole lot of moons swirling around.  You decide to start by tracking the four largest moons: Io,
 * Europa, Ganymede, and Callisto.
 * After a brief scan, you calculate the position of each moon (your puzzle input). You just need to simulate their
 * motion so you can avoid them.
 * Each moon has a 3-dimensional position (x, y, and z) and a 3-dimensional velocity.  The position of each moon is
 * given in your scan; the x, y, and z velocity of each moon starts at 0.
 * Simulate the motion of the moons in time steps. Within each time step, first update the velocity of every moon by
 * applying gravity. Then, once all moons' velocities have been updated, update the position of every moon by applying
 * velocity. Time progresses by one step once all of the positions are updated.
 * To apply gravity, consider every pair of moons. On each axis (x, y, and z), the velocity of each moon changes by
 * exactly +1 or -1 to pull the moons together.  For example, if Ganymede has an x position of 3, and Callisto has a x
 * position of 5, then Ganymede's x velocity changes by +1 (because 5 > 3) and Callisto's x velocity changes by -1
 * (because 3 < 5). However, if the positions on a given axis are the same, the velocity on that axis does not change
 * for that pair of moons.
 * Once all gravity has been applied, apply velocity: simply add the velocity of each moon to its own position. For
 * example, if Europa has a position of x=1, y=2, z=3 and a velocity of x=-2, y=0,z=3, then its new position would be
 * x=-1, y=2, z=6. This process does not modify the velocity of any moon.
 * For example, suppose your scan reveals the following positions:
 * <x=-1, y=0, z=2>
 * <x=2, y=-10, z=-7>
 * <x=4, y=-8, z=8>
 * <x=3, y=5, z=-1>
 *
 * Simulating the motion of these moons would produce the following:
 * After 0 steps:
 * pos=<x=-1, y=  0, z= 2>, vel=<x= 0, y= 0, z= 0>
 * pos=<x= 2, y=-10, z=-7>, vel=<x= 0, y= 0, z= 0>
 * pos=<x= 4, y= -8, z= 8>, vel=<x= 0, y= 0, z= 0>
 * pos=<x= 3, y=  5, z=-1>, vel=<x= 0, y= 0, z= 0>
 *
 * After 1 step:
 * pos=<x= 2, y=-1, z= 1>, vel=<x= 3, y=-1, z=-1>
 * pos=<x= 3, y=-7, z=-4>, vel=<x= 1, y= 3, z= 3>
 * pos=<x= 1, y=-7, z= 5>, vel=<x=-3, y= 1, z=-3>
 * pos=<x= 2, y= 2, z= 0>, vel=<x=-1, y=-3, z= 1>
 *
 * After 2 steps:
 * pos=<x= 5, y=-3, z=-1>, vel=<x= 3, y=-2, z=-2>
 * pos=<x= 1, y=-2, z= 2>, vel=<x=-2, y= 5, z= 6>
 * pos=<x= 1, y=-4, z=-1>, vel=<x= 0, y= 3, z=-6>
 * pos=<x= 1, y=-4, z= 2>, vel=<x=-1, y=-6, z= 2>
 *
 * After 3 steps:
 * pos=<x= 5, y=-6, z=-1>, vel=<x= 0, y=-3, z= 0>
 * pos=<x= 0, y= 0, z= 6>, vel=<x=-1, y= 2, z= 4>
 * pos=<x= 2, y= 1, z=-5>, vel=<x= 1, y= 5, z=-4>
 * pos=<x= 1, y=-8, z= 2>, vel=<x= 0, y=-4, z= 0>
 *
 * After 4 steps:
 * pos=<x= 2, y=-8, z= 0>, vel=<x=-3, y=-2, z= 1>
 * pos=<x= 2, y= 1, z= 7>, vel=<x= 2, y= 1, z= 1>
 * pos=<x= 2, y= 3, z=-6>, vel=<x= 0, y= 2, z=-1>
 * pos=<x= 2, y=-9, z= 1>, vel=<x= 1, y=-1, z=-1>
 *
 * After 5 steps:
 * pos=<x=-1, y=-9, z= 2>, vel=<x=-3, y=-1, z= 2>
 * pos=<x= 4, y= 1, z= 5>, vel=<x= 2, y= 0, z=-2>
 * pos=<x= 2, y= 2, z=-4>, vel=<x= 0, y=-1, z= 2>
 * pos=<x= 3, y=-7, z=-1>, vel=<x= 1, y= 2, z=-2>
 *
 * After 6 steps:
 * pos=<x=-1, y=-7, z= 3>, vel=<x= 0, y= 2, z= 1>
 * pos=<x= 3, y= 0, z= 0>, vel=<x=-1, y=-1, z=-5>
 * pos=<x= 3, y=-2, z= 1>, vel=<x= 1, y=-4, z= 5>
 * pos=<x= 3, y=-4, z=-2>, vel=<x= 0, y= 3, z=-1>
 *
 * After 7 steps:
 * pos=<x= 2, y=-2, z= 1>, vel=<x= 3, y= 5, z=-2>
 * pos=<x= 1, y=-4, z=-4>, vel=<x=-2, y=-4, z=-4>
 * pos=<x= 3, y=-7, z= 5>, vel=<x= 0, y=-5, z= 4>
 * pos=<x= 2, y= 0, z= 0>, vel=<x=-1, y= 4, z= 2>
 *
 * After 8 steps:
 * pos=<x= 5, y= 2, z=-2>, vel=<x= 3, y= 4, z=-3>
 * pos=<x= 2, y=-7, z=-5>, vel=<x= 1, y=-3, z=-1>
 * pos=<x= 0, y=-9, z= 6>, vel=<x=-3, y=-2, z= 1>
 * pos=<x= 1, y= 1, z= 3>, vel=<x=-1, y= 1, z= 3>
 *
 * After 9 steps:
 * pos=<x= 5, y= 3, z=-4>, vel=<x= 0, y= 1, z=-2>
 * pos=<x= 2, y=-9, z=-3>, vel=<x= 0, y=-2, z= 2>
 * pos=<x= 0, y=-8, z= 4>, vel=<x= 0, y= 1, z=-2>
 * pos=<x= 1, y= 1, z= 5>, vel=<x= 0, y= 0, z= 2>
 *
 * After 10 steps:
 * pos=<x= 2, y= 1, z=-3>, vel=<x=-3, y=-2, z= 1>
 * pos=<x= 1, y=-8, z= 0>, vel=<x=-1, y= 1, z= 3>
 * pos=<x= 3, y=-6, z= 1>, vel=<x= 3, y= 2, z=-3>
 * pos=<x= 2, y= 0, z= 4>, vel=<x= 1, y=-1, z=-1>
 *
 * Then, it might help to calculate the total energy in the system. The total energy for a single moon is its potential
 * energy multiplied by its kinetic energy. A moon's potential energy is the sum of the absolute values of its x, y,
 * and z position coordinates. A moon's kinetic energy is the sum of the absolute values of its velocity coordinates.
 * Below, each line shows the calculations for a moon's potential energy (pot), kinetic energy (kin), and total energy:
 * Energy after 10 steps:
 * pot: 2 + 1 + 3 =  6;   kin: 3 + 2 + 1 = 6;   total:  6 * 6 = 36
 * pot: 1 + 8 + 0 =  9;   kin: 1 + 1 + 3 = 5;   total:  9 * 5 = 45
 * pot: 3 + 6 + 1 = 10;   kin: 3 + 2 + 3 = 8;   total: 10 * 8 = 80
 * pot: 2 + 0 + 4 =  6;   kin: 1 + 1 + 1 = 3;   total:  6 * 3 = 18
 * Sum of total energy: 36 + 45 + 80 + 18 = 179
 *
 * In the above example, adding together the total energy for all moons after 10 steps produces the total energy in the
 * system, 179.
 * Here's a second example:
 * <x=-8, y=-10, z=0>
 * <x=5, y=5, z=10>
 * <x=2, y=-7, z=3>
 * <x=9, y=-8, z=-3>
 *
 * Every ten steps of simulation for 100 steps produces:
 * After 0 steps:
 * pos=<x= -8, y=-10, z=  0>, vel=<x=  0, y=  0, z=  0>
 * pos=<x=  5, y=  5, z= 10>, vel=<x=  0, y=  0, z=  0>
 * pos=<x=  2, y= -7, z=  3>, vel=<x=  0, y=  0, z=  0>
 * pos=<x=  9, y= -8, z= -3>, vel=<x=  0, y=  0, z=  0>
 *
 * After 10 steps:
 * pos=<x= -9, y=-10, z=  1>, vel=<x= -2, y= -2, z= -1>
 * pos=<x=  4, y= 10, z=  9>, vel=<x= -3, y=  7, z= -2>
 * pos=<x=  8, y=-10, z= -3>, vel=<x=  5, y= -1, z= -2>
 * pos=<x=  5, y=-10, z=  3>, vel=<x=  0, y= -4, z=  5>
 *
 * After 20 steps:
 * pos=<x=-10, y=  3, z= -4>, vel=<x= -5, y=  2, z=  0>
 * pos=<x=  5, y=-25, z=  6>, vel=<x=  1, y=  1, z= -4>
 * pos=<x= 13, y=  1, z=  1>, vel=<x=  5, y= -2, z=  2>
 * pos=<x=  0, y=  1, z=  7>, vel=<x= -1, y= -1, z=  2>
 *
 * After 30 steps:
 * pos=<x= 15, y= -6, z= -9>, vel=<x= -5, y=  4, z=  0>
 * pos=<x= -4, y=-11, z=  3>, vel=<x= -3, y=-10, z=  0>
 * pos=<x=  0, y= -1, z= 11>, vel=<x=  7, y=  4, z=  3>
 * pos=<x= -3, y= -2, z=  5>, vel=<x=  1, y=  2, z= -3>
 *
 * After 40 steps:
 * pos=<x= 14, y=-12, z= -4>, vel=<x= 11, y=  3, z=  0>
 * pos=<x= -1, y= 18, z=  8>, vel=<x= -5, y=  2, z=  3>
 * pos=<x= -5, y=-14, z=  8>, vel=<x=  1, y= -2, z=  0>
 * pos=<x=  0, y=-12, z= -2>, vel=<x= -7, y= -3, z= -3>
 *
 * After 50 steps:
 * pos=<x=-23, y=  4, z=  1>, vel=<x= -7, y= -1, z=  2>
 * pos=<x= 20, y=-31, z= 13>, vel=<x=  5, y=  3, z=  4>
 * pos=<x= -4, y=  6, z=  1>, vel=<x= -1, y=  1, z= -3>
 * pos=<x= 15, y=  1, z= -5>, vel=<x=  3, y= -3, z= -3>
 *
 * After 60 steps:
 * pos=<x= 36, y=-10, z=  6>, vel=<x=  5, y=  0, z=  3>
 * pos=<x=-18, y= 10, z=  9>, vel=<x= -3, y= -7, z=  5>
 * pos=<x=  8, y=-12, z= -3>, vel=<x= -2, y=  1, z= -7>
 * pos=<x=-18, y= -8, z= -2>, vel=<x=  0, y=  6, z= -1>
 *
 * After 70 steps:
 * pos=<x=-33, y= -6, z=  5>, vel=<x= -5, y= -4, z=  7>
 * pos=<x= 13, y= -9, z=  2>, vel=<x= -2, y= 11, z=  3>
 * pos=<x= 11, y= -8, z=  2>, vel=<x=  8, y= -6, z= -7>
 * pos=<x= 17, y=  3, z=  1>, vel=<x= -1, y= -1, z= -3>
 *
 * After 80 steps:
 * pos=<x= 30, y= -8, z=  3>, vel=<x=  3, y=  3, z=  0>
 * pos=<x= -2, y= -4, z=  0>, vel=<x=  4, y=-13, z=  2>
 * pos=<x=-18, y= -7, z= 15>, vel=<x= -8, y=  2, z= -2>
 * pos=<x= -2, y= -1, z= -8>, vel=<x=  1, y=  8, z=  0>
 *
 * After 90 steps:
 * pos=<x=-25, y= -1, z=  4>, vel=<x=  1, y= -3, z=  4>
 * pos=<x=  2, y= -9, z=  0>, vel=<x= -3, y= 13, z= -1>
 * pos=<x= 32, y= -8, z= 14>, vel=<x=  5, y= -4, z=  6>
 * pos=<x= -1, y= -2, z= -8>, vel=<x= -3, y= -6, z= -9>
 *
 * After 100 steps:
 * pos=<x=  8, y=-12, z= -9>, vel=<x= -7, y=  3, z=  0>
 * pos=<x= 13, y= 16, z= -3>, vel=<x=  3, y=-11, z= -5>
 * pos=<x=-29, y=-11, z= -1>, vel=<x= -3, y=  7, z=  4>
 * pos=<x= 16, y=-13, z= 23>, vel=<x=  7, y=  1, z=  1>
 *
 * Energy after 100 steps:
 * pot:  8 + 12 +  9 = 29;   kin: 7 +  3 + 0 = 10;   total: 29 * 10 = 290
 * pot: 13 + 16 +  3 = 32;   kin: 3 + 11 + 5 = 19;   total: 32 * 19 = 608
 * pot: 29 + 11 +  1 = 41;   kin: 3 +  7 + 4 = 14;   total: 41 * 14 = 574
 * pot: 16 + 13 + 23 = 52;   kin: 7 +  1 + 1 =  9;   total: 52 *  9 = 468
 * Sum of total energy: 290 + 608 + 574 + 468 = 1940
 *
 * What is the total energy in the system after simulating the moons given in your scan for 1000 steps?
 *
 * --- Part Two ---
 * All this drifting around in space makes you wonder about the nature of the universe.  Does history really repeat
 * itself?  You're curious whether the moons will ever return to a previous state.
 * Determine the number of steps that must occur before all of the moons' positions and velocities exactly match a
 * previous point in time.
 * For example, the first example above takes 2772 steps before they exactly match a previous point in time; it
 * eventually returns to the initial state:
 * After 0 steps:
 * pos=<x= -1, y=  0, z=  2>, vel=<x=  0, y=  0, z=  0>
 * pos=<x=  2, y=-10, z= -7>, vel=<x=  0, y=  0, z=  0>
 * pos=<x=  4, y= -8, z=  8>, vel=<x=  0, y=  0, z=  0>
 * pos=<x=  3, y=  5, z= -1>, vel=<x=  0, y=  0, z=  0>
 *
 * After 2770 steps:
 * pos=<x=  2, y= -1, z=  1>, vel=<x= -3, y=  2, z=  2>
 * pos=<x=  3, y= -7, z= -4>, vel=<x=  2, y= -5, z= -6>
 * pos=<x=  1, y= -7, z=  5>, vel=<x=  0, y= -3, z=  6>
 * pos=<x=  2, y=  2, z=  0>, vel=<x=  1, y=  6, z= -2>
 *
 * After 2771 steps:
 * pos=<x= -1, y=  0, z=  2>, vel=<x= -3, y=  1, z=  1>
 * pos=<x=  2, y=-10, z= -7>, vel=<x= -1, y= -3, z= -3>
 * pos=<x=  4, y= -8, z=  8>, vel=<x=  3, y= -1, z=  3>
 * pos=<x=  3, y=  5, z= -1>, vel=<x=  1, y=  3, z= -1>
 *
 * After 2772 steps:
 * pos=<x= -1, y=  0, z=  2>, vel=<x=  0, y=  0, z=  0>
 * pos=<x=  2, y=-10, z= -7>, vel=<x=  0, y=  0, z=  0>
 * pos=<x=  4, y= -8, z=  8>, vel=<x=  0, y=  0, z=  0>
 * pos=<x=  3, y=  5, z= -1>, vel=<x=  0, y=  0, z=  0>
 *
 * Of course, the universe might last for a very long time before repeating.  Here's a copy of the second example from
 * above:
 * <x=-8, y=-10, z=0>
 * <x=5, y=5, z=10>
 * <x=2, y=-7, z=3>
 * <x=9, y=-8, z=-3>
 *
 * This set of initial positions takes 4686774924 steps before it repeats a previous state! Clearly, you might need to
 * find a more efficient way to simulate the universe.
 * How many steps does it take to reach the first state that exactly matches a previous state?
 *
 */
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

fun lcm(x: Long, y: Long) = x * y / gcd(x, y)

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