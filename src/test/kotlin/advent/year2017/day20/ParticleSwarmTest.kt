package advent.year2017.day20

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ParticleSwarmTest {

    val referenceInput = """
            p=<-6,0,0>, v=<3,0,0>, a=<0,0,0>
            p=<-4,0,0>, v=<2,0,0>, a=<0,0,0>
            p=<-2,0,0>, v=<1,0,0>, a=<0,0,0>
            p=<3,0,0>, v=<-1,0,0>, a=<0,0,0>
        """.trimIndent()

    @Test
    fun `particle from line -- first line of input -- gives expected particle`() {
        val line = "p=<1609,-863,-779>, v=<-15,54,-69>, a=<-10,0,14>"

        val particle = Particle.fromLine(line)

        assertThat(particle).isEqualTo(Particle(
                AxisTrajectory(1609, -15, -10),
                AxisTrajectory(-863, 54, 0),
                AxisTrajectory(-779, -69, 14))
        )
    }

    @Test
    fun `survivor count -- reference input -- reference output`() {
        val swarm = ParticleSwarm(referenceInput)

        val survivors = swarm.survivorCount()

        assertThat(survivors).isEqualTo(1)
    }

    @Test
    fun `colliding particles -- initial state of reference input -- no collisions`() {
        val swarm = ParticleSwarm(referenceInput)

        val collisions = swarm.collidingParticles()

        assertThat(collisions).isEmpty()
    }
}