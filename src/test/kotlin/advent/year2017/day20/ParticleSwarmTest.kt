package advent.year2017.day20

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ParticleSwarmTest {

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
}