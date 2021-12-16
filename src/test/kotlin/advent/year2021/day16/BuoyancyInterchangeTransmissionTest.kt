package advent.year2021.day16

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class BuoyancyInterchangeTransmissionTest {

  @Test
  fun `parse -- D2FE28 -- literal 2021`() {
    val packet = BuoyancyInterchangeTransmission.parse("D2FE28")

    val expected = LiteralPacket(version = 6, value = 2021)

    assertThat(packet).isEqualTo(expected)
  }

  @Test
  fun `parse -- 38006F45291200 -- operator with two subpackets`() {
    val packet = BuoyancyInterchangeTransmission.parse("38006F45291200")

    val expected = OperatorPacket(
      version = 1, packetTypeId = 6,
      subpackets = listOf(
        LiteralPacket(version = 6, value = 10),
        LiteralPacket(version = 2, value = 20)
      )
    )

    assertThat(packet).isEqualTo(expected)
  }

  @Test
  fun `parse -- EE00D40C823060 -- operator with three subpackets`() {
    val packet = BuoyancyInterchangeTransmission.parse("EE00D40C823060")

    val expected = OperatorPacket(
      version = 7, packetTypeId = 3,
      subpackets = listOf(
        LiteralPacket(version = 2, value = 1),
        LiteralPacket(version = 4, value = 2),
        LiteralPacket(version = 1, value = 3)
      )
    )

    assertThat(packet).isEqualTo(expected)
  }

  @ParameterizedTest
  @CsvSource(
    "8A004A801A8002F478, 16",
    "620080001611562C8802118E34, 12",
    "C0015000016115A2E0802F182340, 23",
    "A0016C880162017C3686B18A3D4780, 31"
  )
  fun `version sums -- reference examples -- reference values`(input: String, expected: Int) {
    val packet = BuoyancyInterchangeTransmission.parse(input)

    assertThat(packet.versionSum()).isEqualTo(expected)
  }

  @ParameterizedTest
  @CsvSource(
    "C200B40A82, 3",
    "04005AC33890, 54",
    "880086C3E88112, 7",
    "CE00C43D881120, 9",
    "D8005AC2A8F0, 1",
    "F600BC2D8F, 0",
    "9C005AC2F8F0, 0",
    "9C0141080250320F1802104A08, 1"
  )
  fun `evaluate -- reference examples -- reference values`(input: String, expected: Long) {
    val packet = BuoyancyInterchangeTransmission.parse(input)

    assertThat(packet.evaluate()).isEqualTo(expected)
  }
}