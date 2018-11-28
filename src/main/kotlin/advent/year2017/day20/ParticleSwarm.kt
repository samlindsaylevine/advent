package advent.year2017.day20

import java.io.File
import java.lang.IllegalArgumentException

class ParticleSwarm(text: String) {

    private val particles: List<Particle> = text.trim().split("\n")
            .map { Particle.fromLine(it) }

    /**
     * @return the index in the array of the particle that remains closest to the origin (by Manhattan distance)
     * as time goes to infinity.
     */
    fun closestToOriginIndex(): Int? {

        // As time goes to infinity, the position of a particle (x0 + v0 * t + 1/2 * a * t^2) is dominated by the a
        // term. Therefore, its long run position is proportional (on each axis) simply to a. The Manhattan distance
        // is the sum of the absolute value of the position on each axis.
        return particles.indices.minBy { i ->
            Math.abs(particles[i].x.acceleration) +
                    Math.abs(particles[i].y.acceleration) +
                    Math.abs(particles[i].z.acceleration)
        }
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

}

data class AxisTrajectory(val initialPosition: Int,
                          val velocity: Int,
                          val acceleration: Int)

fun main(args: Array<String>) {
    val input = File("src/main/kotlin/advent/year2017/day20/input.txt")
            .readText()

    val swarm = ParticleSwarm(input)

    println(swarm.closestToOriginIndex())
}