package advent.year2017.day20

import java.io.File
import java.lang.IllegalArgumentException
import kotlin.math.sign

/**
 * --- Day 20: Particle Swarm ---
 * Suddenly, the GPU contacts you, asking for help. Someone has asked it to simulate too many particles, and it won't
 * be able to finish them all in time to render the next frame at this rate.
 * It transmits to you a buffer (your puzzle input) listing each particle in order (starting with particle 0, then
 * particle 1, particle 2, and so on). For each particle, it provides the X, Y, and Z coordinates for the particle's
 * position (p), velocity (v), and acceleration (a), each in the format <X,Y,Z>.
 * Each tick, all particles are updated simultaneously. A particle's properties are updated in the following order:
 * 
 * Increase the X velocity by the X acceleration.
 * Increase the Y velocity by the Y acceleration.
 * Increase the Z velocity by the Z acceleration.
 * Increase the X position by the X velocity.
 * Increase the Y position by the Y velocity.
 * Increase the Z position by the Z velocity.
 * 
 * Because of seemingly tenuous rationale involving z-buffering, the GPU would like to know which particle will stay
 * closest to position <0,0,0> in the long term. Measure this using the Manhattan distance, which in this situation is
 * simply the sum of the absolute values of a particle's X, Y, and Z position.
 * For example, suppose you are only given two particles, both of which stay entirely on the X-axis (for simplicity).
 * Drawing the current states of particles 0 and 1 (in that order) with an adjacent a number line and diagram of
 * current X positions (marked in parentheses), the following would take place:
 * p=< 3,0,0>, v=< 2,0,0>, a=<-1,0,0>    -4 -3 -2 -1  0  1  2  3  4
 * p=< 4,0,0>, v=< 0,0,0>, a=<-2,0,0>                         (0)(1)
 * 
 * p=< 4,0,0>, v=< 1,0,0>, a=<-1,0,0>    -4 -3 -2 -1  0  1  2  3  4
 * p=< 2,0,0>, v=<-2,0,0>, a=<-2,0,0>                      (1)   (0)
 * 
 * p=< 4,0,0>, v=< 0,0,0>, a=<-1,0,0>    -4 -3 -2 -1  0  1  2  3  4
 * p=<-2,0,0>, v=<-4,0,0>, a=<-2,0,0>          (1)               (0)
 * 
 * p=< 3,0,0>, v=<-1,0,0>, a=<-1,0,0>    -4 -3 -2 -1  0  1  2  3  4
 * p=<-8,0,0>, v=<-6,0,0>, a=<-2,0,0>                         (0)   
 * 
 * At this point, particle 1 will never be closer to <0,0,0> than particle 0, and so, in the long run, particle 0 will
 * stay closest.
 * Which particle will stay closest to position <0,0,0> in the long term?
 * 
 * --- Part Two ---
 * To simplify the problem further, the GPU would like to remove any particles that collide. Particles collide if their
 * positions ever exactly match. Because particles are updated simultaneously, more than two particles can collide at
 * the same time and place.  Once particles collide, they are removed and cannot collide with anything else after that
 * tick.
 * For example:
 * p=<-6,0,0>, v=< 3,0,0>, a=< 0,0,0>    
 * p=<-4,0,0>, v=< 2,0,0>, a=< 0,0,0>    -6 -5 -4 -3 -2 -1  0  1  2  3
 * p=<-2,0,0>, v=< 1,0,0>, a=< 0,0,0>    (0)   (1)   (2)            (3)
 * p=< 3,0,0>, v=<-1,0,0>, a=< 0,0,0>
 * 
 * p=<-3,0,0>, v=< 3,0,0>, a=< 0,0,0>    
 * p=<-2,0,0>, v=< 2,0,0>, a=< 0,0,0>    -6 -5 -4 -3 -2 -1  0  1  2  3
 * p=<-1,0,0>, v=< 1,0,0>, a=< 0,0,0>             (0)(1)(2)      (3)   
 * p=< 2,0,0>, v=<-1,0,0>, a=< 0,0,0>
 * 
 * p=< 0,0,0>, v=< 3,0,0>, a=< 0,0,0>    
 * p=< 0,0,0>, v=< 2,0,0>, a=< 0,0,0>    -6 -5 -4 -3 -2 -1  0  1  2  3
 * p=< 0,0,0>, v=< 1,0,0>, a=< 0,0,0>                       X (3)      
 * p=< 1,0,0>, v=<-1,0,0>, a=< 0,0,0>
 * 
 * ------destroyed by collision------    
 * ------destroyed by collision------    -6 -5 -4 -3 -2 -1  0  1  2  3
 * ------destroyed by collision------                      (3)         
 * p=< 0,0,0>, v=<-1,0,0>, a=< 0,0,0>
 * 
 * In this example, particles 0, 1, and 2 are simultaneously destroyed at the time and place marked X. On the next
 * tick, particle 3 passes through unharmed.
 * How many particles are left after all collisions are resolved?
 * 
 */
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