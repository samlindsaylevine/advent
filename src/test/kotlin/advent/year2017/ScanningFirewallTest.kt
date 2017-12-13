package advent.year2017

import advent.year2017.day13.ScanningFirewall
import advent.year2017.day13.ScanningFirewall.SecurityScanner
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource


class ScanningFirewallTest {

    private val REFERENCE_INPUT = """0: 3
1: 2
4: 4
6: 4"""

    @ParameterizedTest(name = "scannerPosition -- size 4 scanner at time {0} -- position {1}")
    @CsvSource("0, 0",
            "1, 1",
            "2, 2",
            "3, 3",
            "4, 2",
            "5, 1",
            "6, 0",
            "7, 1",
            "8, 2",
            "9, 3",
            "10, 2",
            "11, 1",
            "12, 0")
    fun `scannerPosition -- size 4 scanner -- 2 full cycles`(time: Int, expected: Int) {
        val scanner = SecurityScanner(range = 4, depth = 0)

        val position = scanner.positionOn(time)

        assertThat(position).isEqualTo(expected)
    }

    @ParameterizedTest(name = "scannerPosition -- size 1 scanner at time {0} -- position 0")
    @CsvSource("0", "1", "2", "3")
    fun `scannerPosition -- size 1 scanner -- always at top`(time: Int) {
        val scanner = SecurityScanner(range = 1, depth = 0)

        val position = scanner.positionOn(time)

        assertThat(position).isEqualTo(0)
    }

    @Test
    fun `severityOfTripStartingOnTime -- time zero, reference firewall -- reference severity`() {
        val referenceFirewall = ScanningFirewall(REFERENCE_INPUT)

        val severity = referenceFirewall.severityOfTripStartingOn(0)

        assertThat(severity).isEqualTo(24)
    }

    @Test
    fun `earliestSafeTrip -- reference firewall -- reference start time`() {
        val referenceFirewall = ScanningFirewall(REFERENCE_INPUT)

        println(referenceFirewall.severityOfTripStartingOn(4))

        val startTime = referenceFirewall.earliestSafeTrip()

        assertThat(startTime).isEqualTo(10)
    }

}