package advent.year2017.day20

import java.io.File
import java.lang.IllegalArgumentException
import kotlin.math.sign

class ParticleSwarm(private val particles: List<Particle>) {

  constructor(text: String) : this(text.trim().split("\n")
          .map { Particle.fromLine(it) })

  /**
   * @return the index in the array of the particle that remains closest to the origin (by Manhattan distance)
   * as time goes to infinity.
   */
  fun closestToOriginIndex(): Int? {

    // As time goes to infinity, the position of a particle (x0 + v0 * t + 1/2 * a * t^2) is dominated by the a
    // term. Therefore, its long run position is proportional (on each axis) simply to a. The Manhattan distance
    // is the sum of the absolute value of the position on each axis.
    return particles.indices.minByOrNull { i ->
      Math.abs(particles[i].x.acceleration) +
              Math.abs(particles[i].y.acceleration) +
              Math.abs(particles[i].z.acceleration)
    }
  }

  /**
   * How many particles are left after all collisions have taken place.
   */
  fun survivorCount(): Int = generateSequence(this, ParticleSwarm::next)
          .filter { it.willNeverHaveAnyMoreCollisions() }
          .first()
          .particles.size

  private fun withoutCollidingParticles(): ParticleSwarm {
    val collisions = collidingParticles()
    return ParticleSwarm(particles.filter { !collisions.contains(it) })
  }

  fun collidingParticles(): Set<Particle> = allPairs()
          .filter { it.first.collidesWith(it.second) }
          .flatMap { sequenceOf(it.first, it.second) }
          .toSet()

  private fun allPairs(): Sequence<Pair<Particle, Particle>> = particles.indices.asSequence()
          .flatMap { i -> particles.indices.asSequence().filter { j -> j != i }.map { j -> Pair(i, j) } }
          .map { Pair(particles[it.first], particles[it.second]) }

  fun next(): ParticleSwarm {
    return ParticleSwarm(this.withoutCollidingParticles()
            .particles.map { it.next() })
  }

  private fun willNeverHaveAnyMoreCollisions(): Boolean {
    val sortedByXAccel = particles.sortedBy { it.x.acceleration }
    val pairs = sortedByXAccel.zipWithNext()

    return pairs.all { it.first.willNotEverCollide(it.second) }
  }
}

data class Particle(val x: AxisTrajectory,
                    val y: AxisTrajectory,
                    val z: AxisTrajectory) {
  companion object {
    val REGEX = "p=<(-?\\d+),(-?\\d+),(-?\\d+)>, v=<(-?\\d+),(-?\\d+),(-?\\d+)>, a=<(-?\\d+),(-?\\d+),(-?\\d+)>".toRegex()

    private fun MatchResult.intAt(index: Int) = this.groupValues[index].toInt()

    fun fromLine(line: String): Particle {
      val match = REGEX.matchEntire(line.trim()) ?: throw IllegalArgumentException("Unrecognized particle $line")

      return Particle(
              AxisTrajectory(match.intAt(1),
                      match.intAt(4),
                      match.intAt(7)),
              AxisTrajectory(match.intAt(2),
                      match.intAt(5),
                      match.intAt(8)),
              AxisTrajectory(match.intAt(3),
                      match.intAt(6),
                      match.intAt(9)))
    }
  }

  fun willNotEverCollide(other: Particle) = this.x.willNotEverCollide(other.x) ||
          this.y.willNotEverCollide(other.y) ||
          this.z.willNotEverCollide(other.z)

  fun next() = Particle(x.next(), y.next(), z.next())

  fun collidesWith(other: Particle) = this.x.position == other.x.position &&
          this.y.position == other.y.position &&
          this.z.position == other.z.position
}

data class AxisTrajectory(val position: Int,
                          val velocity: Int,
                          val acceleration: Int) {
  // If the difference between acceleration, velocity, and position all have the same sign (or are
  // 0) then we know that one value will never catch up to the other.
  fun willNotEverCollide(other: AxisTrajectory): Boolean {
    if (this == other) return false

    val positionSign = (this.position - other.position).sign
    val velocitySign = (this.velocity - other.velocity).sign
    val accelerationSign = (this.acceleration - other.acceleration).sign

    return (positionSign <= 0 && velocitySign <= 0 && accelerationSign <= 0) ||
            (positionSign >= 0 && velocitySign >= 0 && accelerationSign >= 0)
  }

  fun next() = AxisTrajectory(this.position + this.velocity + this.acceleration,
          this.velocity + this.acceleration,
          this.acceleration)
}

fun main() {
  val input = File("src/main/kotlin/advent/year2017/day20/input.txt")
          .readText()

  val swarm = ParticleSwarm(input)

  println(swarm.closestToOriginIndex())
  println(swarm.survivorCount())
}