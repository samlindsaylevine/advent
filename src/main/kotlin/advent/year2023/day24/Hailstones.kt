package advent.year2023.day24

import advent.utils.Rational
import advent.utils.allUnorderedPairs
import advent.utils.rrefRational
import java.io.File
import java.math.BigInteger

/**
 * --- Day 24: Never Tell Me The Odds ---
 * It seems like something is going wrong with the snow-making process. Instead of forming snow, the water that's been
 * absorbed into the air seems to be forming hail!
 * Maybe there's something you can do to break up the hailstones?
 * Due to strong, probably-magical winds, the hailstones are all flying through the air in perfectly linear
 * trajectories. You make a note of each hailstone's position and velocity (your puzzle input). For example:
 * 19, 13, 30 @ -2,  1, -2
 * 18, 19, 22 @ -1, -1, -2
 * 20, 25, 34 @ -2, -2, -4
 * 12, 31, 28 @ -1, -2, -1
 * 20, 19, 15 @  1, -5, -3
 *
 * Each line of text corresponds to the position and velocity of a single hailstone. The positions indicate where the
 * hailstones are right now (at time 0). The velocities are constant and indicate exactly how far each hailstone will
 * move in one nanosecond.
 * Each line of text uses the format px py pz @ vx vy vz. For instance, the hailstone specified by 20, 19, 15 @  1, -5,
 * -3 has initial X position 20, Y position 19, Z position 15, X velocity 1, Y velocity -5, and Z velocity -3. After
 * one nanosecond, the hailstone would be at 21, 14, 12.
 * Perhaps you won't have to do anything. How likely are the hailstones to collide with each other and smash into tiny
 * ice crystals?
 * To estimate this, consider only the X and Y axes; ignore the Z axis. Looking forward in time, how many of the
 * hailstones' paths will intersect within a test area? (The hailstones themselves don't have to collide, just test for
 * intersections between the paths they will trace.)
 * In this example, look for intersections that happen with an X and Y position each at least 7 and at most 27; in your
 * actual data, you'll need to check a much larger test area. Comparing all pairs of hailstones' future paths produces
 * the following results:
 * Hailstone A: 19, 13, 30 @ -2, 1, -2
 * Hailstone B: 18, 19, 22 @ -1, -1, -2
 * Hailstones' paths will cross inside the test area (at x=14.333, y=15.333).
 *
 * Hailstone A: 19, 13, 30 @ -2, 1, -2
 * Hailstone B: 20, 25, 34 @ -2, -2, -4
 * Hailstones' paths will cross inside the test area (at x=11.667, y=16.667).
 *
 * Hailstone A: 19, 13, 30 @ -2, 1, -2
 * Hailstone B: 12, 31, 28 @ -1, -2, -1
 * Hailstones' paths will cross outside the test area (at x=6.2, y=19.4).
 *
 * Hailstone A: 19, 13, 30 @ -2, 1, -2
 * Hailstone B: 20, 19, 15 @ 1, -5, -3
 * Hailstones' paths crossed in the past for hailstone A.
 *
 * Hailstone A: 18, 19, 22 @ -1, -1, -2
 * Hailstone B: 20, 25, 34 @ -2, -2, -4
 * Hailstones' paths are parallel; they never intersect.
 *
 * Hailstone A: 18, 19, 22 @ -1, -1, -2
 * Hailstone B: 12, 31, 28 @ -1, -2, -1
 * Hailstones' paths will cross outside the test area (at x=-6, y=-5).
 *
 * Hailstone A: 18, 19, 22 @ -1, -1, -2
 * Hailstone B: 20, 19, 15 @ 1, -5, -3
 * Hailstones' paths crossed in the past for both hailstones.
 *
 * Hailstone A: 20, 25, 34 @ -2, -2, -4
 * Hailstone B: 12, 31, 28 @ -1, -2, -1
 * Hailstones' paths will cross outside the test area (at x=-2, y=3).
 *
 * Hailstone A: 20, 25, 34 @ -2, -2, -4
 * Hailstone B: 20, 19, 15 @ 1, -5, -3
 * Hailstones' paths crossed in the past for hailstone B.
 *
 * Hailstone A: 12, 31, 28 @ -1, -2, -1
 * Hailstone B: 20, 19, 15 @ 1, -5, -3
 * Hailstones' paths crossed in the past for both hailstones.
 *
 * So, in this example, 2 hailstones' future paths cross inside the boundaries of the test area.
 * However, you'll need to search a much larger test area if you want to see if any hailstones might collide. Look for
 * intersections that happen with an X and Y position each at least 200000000000000 and at most 400000000000000.
 * Disregard the Z axis entirely.
 * Considering only the X and Y axes, check all pairs of hailstones' future paths for intersections. How many of these
 * intersections occur within the test area?
 *
 * --- Part Two ---
 * Upon further analysis, it doesn't seem like any hailstones will naturally collide. It's up to you to fix that!
 * You find a rock on the ground nearby. While it seems extremely unlikely, if you throw it just right, you should be
 * able to hit every hailstone in a single throw!
 * You can use the probably-magical winds to reach any integer position you like and to propel the rock at any integer
 * velocity. Now including the Z axis in your calculations, if you throw the rock at time 0, where do you need to be so
 * that the rock perfectly collides with every hailstone? Due to probably-magical inertia, the rock won't slow down or
 * change direction when it collides with a hailstone.
 * In the example above, you can achieve this by moving to position 24, 13, 10 and throwing the rock at velocity -3, 1,
 * 2. If you do this, you will hit every hailstone as follows:
 * Hailstone: 19, 13, 30 @ -2, 1, -2
 * Collision time: 5
 * Collision position: 9, 18, 20
 *
 * Hailstone: 18, 19, 22 @ -1, -1, -2
 * Collision time: 3
 * Collision position: 15, 16, 16
 *
 * Hailstone: 20, 25, 34 @ -2, -2, -4
 * Collision time: 4
 * Collision position: 12, 17, 18
 *
 * Hailstone: 12, 31, 28 @ -1, -2, -1
 * Collision time: 6
 * Collision position: 6, 19, 22
 *
 * Hailstone: 20, 19, 15 @ 1, -5, -3
 * Collision time: 1
 * Collision position: 21, 14, 12
 *
 * Above, each hailstone is identified by its initial position and its velocity. Then, the time and position of that
 * hailstone's collision with your rock are given.
 * After 1 nanosecond, the rock has exactly the same position as one of the hailstones, obliterating it into ice dust!
 * Another hailstone is smashed to bits two nanoseconds after that. After a total of 6 nanoseconds, all of the
 * hailstones have been destroyed.
 * So, at time 0, the rock needs to be at X position 24, Y position 13, and Z position 10. Adding these three
 * coordinates together produces 47. (Don't add any coordinates from the rock's velocity.)
 * Determine the exact position and velocity the rock needs to have at time 0 so that it perfectly collides with every
 * hailstone. What do you get if you add up the X, Y, and Z coordinates of that initial position?
 *
 */
class Hailstones(private val hailstones: List<Hailstone>) {
  constructor(input: String) : this(input.trim().lines().map(::Hailstone))

  fun xyIntersectionsWithinTestArea(numberRange: LongRange): Int {
    val pairs = hailstones.allUnorderedPairs()
    val inTestArea = { point: DoublePoint3D -> point.x in numberRange && point.y in numberRange }
    return pairs.count { (first, second) ->
      val intersection = first.intersectionPointXY(second)
      intersection != null && inTestArea(intersection)
    }
  }

  fun magicRockResult(): BigInteger {
    // We are again solving a system of equations. We have an unknown rx, ry, rz, rvx, rvy, and rvz.
    // Then, for each hailstone n, we have an intersection at time tn (adding one new unknown), so
    // xn + vxn * tn = rx + rvx * tn
    // yn + vyn * tn = ry + rvy * tn
    // zn + vzn * tn = rz + rvz * tn
    //
    // So each hailstone we consider adds one new unknown, but adds 3 constraints.
    // That means with X hailstones we have 3X constraints and 6 + X unknowns; so if we inspect 3 hailstones we have
    // the same number of constraints as we do unknowns - 9 variables and 9 equations.
    // Except, oops, rvx and tn are both variables, so it isn't a linear equation! So we can't use the straightforward
    // linear algebra rref solution.
    // The pesky annoying term is the rvx (and y and z) * tn one. Is there some way we can eliminate those terms?
    // Maybe we could try to get the rv variables out of our equations by bringing in more tns, using more hailstones?
    // Or we could use Newton's method on a system of multiple equations to numerically approximate a solution?
    // Or we could guess-and-check collision times for the first two hailstones, and see if the rock corresponding to
    // those times also hits the third hailstone.
    //
    // Let's try the algebraic solution first, since if we can get that, it will be both exact and very fast.
    // Suppose we try to find-and-replace out tn. If we do that with the x equation, we get
    // tn = (xn - rx) / (rvx - vxn)
    // Then
    // yn + vyn * (xn - rx) / (rvx - vxn) = ry + rvy * (xn - rx) / (rvx - vxn)
    // yn * (rvx - vxn) + vyn * (xn - rx) = ry * (rvx - vxn) + rvy * (xn - rx)
    // yn * rvx - yn * vxn + vyn * xn - vyn * rx = ry * rvx - ry * vxn + rvy * xn - rvy * rx
    // and likewise for z.
    // The terms in this equation that are nonlinear are ry * rvx and -rvy * rx.
    // But those are the same for every n! That means we can pick two different copies of this equation (n and m, say)
    // and subtract one from the other to cancel out the nonlinear terms. Let's subtract M:
    // (yn - ym) * rvx + (ym * vxm - yn * vxn) + (vyn * xn - vym * xm) + (vym - vyn) * rx =
    //    (vxm - vxn) * ry + (xn - xm) * rvy
    // Or
    // (vym - vyn) * rx + (vxn -vxm) * ry + (yn - ym) * rvx + (xm - xn) * rvy = vym * xm - vyn * xn + yn * vxn - ym * vxm
    // We can do the same with z, substituting z for y throughout. Then we can select 3 hailstones (the same number as
    // above!), take all three pairs for n and m, and both the z and y equations, to end up with 6 linear equations
    // in 6 unknowns.
    // Whew!
    // Now we just need to express that as a matrix and then rref it.
    // Unfortunately if we take all three pairs from the first 3 hailstones, we get a linear dependence - only
    // 4 rows survive in our rref. Let's use 4 hailstones instead -
    val pairs = listOf(
            0 to 1,
            0 to 2,
            0 to 3
    )

    // We need arbitrary precision! Without it our result has an E14 in it.
    val matrix: List<List<Rational>> = pairs.flatMap { (n, m) ->
      val hailstoneN = hailstones[n]
      val hailstoneM = hailstones[m]
      val (nPosition, nVelocity) = hailstoneN
      val (mPosition, mVelocity) = hailstoneM
      val (xn, yn, zn) = nPosition.exact()
      val (xm, ym, zm) = mPosition.exact()
      val (vxn, vyn, vzn) = nVelocity.exact()
      val (vxm, vym, vzm) = mVelocity.exact()

      val yRow = listOf(
              vym - vyn, vxn - vxm, 0.0, yn - ym, xm - xn, 0.0, vym * xm - vyn * xn + yn * vxn - ym * vxm
      ).map { Rational.of(it.toLong(), 1) }
      val zRow = listOf(
              vzm - vzn, 0.0, vxn - vxm, zn - zm, 0.0, xm - xn, vzm * xm - vzn * xn + zn * vxn - zm * vxm
      ).map { Rational.of(it.toLong(), 1) }
      listOf(yRow, zRow)
    }


    val rref = rrefRational(matrix)
    rref.forEach(::println)
    val resultColumn = (0 until 6).map { y -> rref[y].last() }

    return (resultColumn[0] + resultColumn[1] + resultColumn[2]).toBigInteger()
  }
}

private operator fun LongRange.contains(value: Double) = value >= this.first && value <= this.last

data class Hailstone(val position: DoublePoint3D, val velocity: DoublePoint3D) {
  constructor(input: String) : this(input.substringBefore(" @ ").toLongPoint3D(),
          input.substringAfter(" @ ").toLongPoint3D())

  private fun positionAt(time: Double) = position + time * velocity

  // Find the point at which the two paths of the hailstones intersect (although not necessarily at a time when
  // both hailstones are there). Returns null if the paths never intersect, or if the intersection was in the past
  // (i.e., either hailstone is at that intersection point at a negative time).
  // Only the X and Y axes are considered.
  fun intersectionPointXY(other: Hailstone): DoublePoint3D? {
    // Determine the times (if any) at which the two points have the same x and y values.
    val (ourTime, theirTime) = intersectionTimesXY(other) ?: return null
    return if (ourTime > 0 && theirTime > 0) this.positionAt(ourTime) else null
  }

  private fun intersectionTimesXY(other: Hailstone): Pair<Double, Double>? {
    // We are trying to solve the equations
    // x1 + vx1 * t1 = x2 + vx2 * t2
    // y1 + vy1 * t1 = y2 + vy2 * t2
    //
    // This is a system of two linear equations; we can use the classic reduced row echelon form solution to result
    // in
    // t1 = (vx2 * y2 - vx2 * y1 + vy2 * x1 - vy2 * x2) / (vx2 * vy1 - vx1 * vy2)
    // t2 = (vx1 * y2 - vx1 * y1 + vy1 * x1 - vy1 * x2) / (vx2 * vy1 - vx1 * vy2)
    // and of course if that determinant denominator is 0, there are no solutions.
    val (x1, y1, _) = this.position
    val (vx1, vy1, _) = this.velocity
    val (x2, y2, _) = other.position
    val (vx2, vy2, _) = other.velocity

    val denominator = vx2 * vy1 - vx1 * vy2
    if (denominator == 0.0) return null

    val t1 = (vx2 * y2 - vx2 * y1 + vy2 * x1 - vy2 * x2) / denominator
    val t2 = (vx1 * y2 - vx1 * y1 + vy1 * x1 - vy1 * x2) / denominator
    return (t1 to t2)
  }
}

data class DoublePoint3D(val x: Double, val y: Double, val z: Double) {
  operator fun plus(other: DoublePoint3D) = DoublePoint3D(this.x + other.x, this.y + other.y, this.z + other.z)
  fun sumOfCoordinates() = x + y + z
  fun exact() = Triple(x.toLong().toBigInteger(), y.toLong().toBigInteger(), z.toLong().toBigInteger())
}

operator fun Double.times(point: DoublePoint3D) = DoublePoint3D(this * point.x, this * point.y, this * point.z)

private fun String.toLongPoint3D(): DoublePoint3D {
  val (x, y, z) = this.split(", ")
  return DoublePoint3D(x.trim().toDouble(), y.trim().toDouble(), z.trim().toDouble())
}

fun main() {
  val input = File("src/main/kotlin/advent/year2023/day24/input.txt").readText().trim()
  val hailstones = Hailstones(input)

  println(hailstones.xyIntersectionsWithinTestArea(200000000000000..400000000000000))
  println(hailstones.magicRockResult())
}