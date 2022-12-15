package advent.year2022.day15

import advent.utils.Point
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DeployableSensorTest {

  private val input = """
    Sensor at x=2, y=18: closest beacon is at x=-2, y=15
    Sensor at x=9, y=16: closest beacon is at x=10, y=16
    Sensor at x=13, y=2: closest beacon is at x=15, y=3
    Sensor at x=12, y=14: closest beacon is at x=10, y=16
    Sensor at x=10, y=20: closest beacon is at x=10, y=16
    Sensor at x=14, y=17: closest beacon is at x=10, y=16
    Sensor at x=8, y=7: closest beacon is at x=2, y=10
    Sensor at x=2, y=0: closest beacon is at x=2, y=10
    Sensor at x=0, y=11: closest beacon is at x=2, y=10
    Sensor at x=20, y=14: closest beacon is at x=25, y=17
    Sensor at x=17, y=20: closest beacon is at x=21, y=22
    Sensor at x=16, y=7: closest beacon is at x=15, y=3
    Sensor at x=14, y=3: closest beacon is at x=15, y=3
    Sensor at x=20, y=1: closest beacon is at x=15, y=3
  """.trimIndent()

  @Test
  fun `cannot contain count -- reference input, 10 -- 26`() {
    val sensors = DeployableSensors(input)

    val count = sensors.cannotContainCount(10)

    assertThat(count).isEqualTo(26)
  }

  @Test
  fun `distress beacon -- reference input -- 14, 11`() {
    val sensors = DeployableSensors(input)

    val beacon = sensors.distressBeacon(max = 20)

    assertThat(beacon).isEqualTo(Point(14, 11))
  }

  @Test
  fun `tuning frequency -- reference input -- 56000011`() {
    val sensors = DeployableSensors(input)

    val beacon = sensors.distressBeacon(max = 20)

    assertThat(beacon.tuningFrequency()).isEqualTo(56000011)
  }

  @Test
  fun `sparse range -- remove from front -- eliminates removed range`() {
    val range = SparseRange(1..10)

    val removed = range - (1..4)

    assertThat(removed).isEqualTo(SparseRange(5..10))
  }

  @Test
  fun `sparse range -- remove from back -- eliminates removed range`() {
    val range = SparseRange(1..10)

    val removed = range - (5..100)

    assertThat(removed).isEqualTo(SparseRange(1..4))
  }

  @Test
  fun `sparse range -- remove entire -- eliminates removed range`() {
    val range = SparseRange(1..10)

    val removed = range - (-2..100)

    assertThat(removed).isEqualTo(SparseRange(emptyList()))
  }

  @Test
  fun `sparse range -- remove from middle -- leaves two ranges`() {
    val range = SparseRange(1..10)

    val removed = range - (4..7)

    assertThat(removed).isEqualTo(SparseRange(listOf(1..3, 8..10)))
  }

  @Test
  fun `sparse range -- remove non-overlapping -- leaves unchanged`() {
    val range = SparseRange(1..10)

    val removed = range - (12..20)

    assertThat(removed).isEqualTo(range)
  }

  @Test
  fun `eliminatedDistressPositionsOnRow -- sample sensor -- gives expected eliminated range`() {
    val sensor = DeployableSensor.parse("Sensor at x=8, y=7: closest beacon is at x=2, y=10")

    val eliminated = sensor.eliminatedDistressPositionsOnRow(0)

    assertThat(eliminated).isEqualTo(6..10)
  }
}