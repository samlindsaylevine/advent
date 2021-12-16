package advent.year2021.day16

import advent.utils.takeWhileInclusive
import java.io.File

object BuoyancyInterchangeTransmission {
  fun parse(hexString: String): BitPacket {
    // Can't just use the built-in toInt(radix) and toString(radix) for two reasons:
    // 1, number is too big; 2, need to ensure that we have the correct number of leading 0s.
    val binary = hexString.toCharArray()
      .joinToString("") {
        it.digitToInt(16)
          .toString(2)
          .padStart(4, '0')
      }

    return parseBinary(BinaryStream(binary))
  }

  private fun parseBinary(binary: BinaryStream): BitPacket {
    val version = binary.take(3).toInt(radix = 2)

    return when (val typeId = binary.take(3).toInt(radix = 2)) {
      4 -> parseLiteral(version, binary)
      else -> parseOperator(version, typeId, binary)
    }
  }

  private fun parseLiteral(version: Int, binary: BinaryStream): LiteralPacket {
    val number = generateSequence { binary.take(5) }
      .takeWhileInclusive { it.startsWith("1") }
      .joinToString("") { it.drop(1) }
      .toLong(radix = 2)

    return LiteralPacket(version, number)
  }

  private fun parseOperator(version: Int, typeId: Int, binary: BinaryStream): OperatorPacket {

    val subpackets: List<BitPacket> = when (binary.take(1)) {
      "0" -> {
        val subpacketBitCount = binary.take(15).toInt(radix = 2)
        val subpacketBits = binary.take(subpacketBitCount)
        parseAllPackets(BinaryStream(subpacketBits))
      }
      else -> {
        val subpacketCount = binary.take(11).toInt(radix = 2)
        parseSubpackets(subpacketCount, binary)
      }
    }

    return OperatorPacket(version, typeId, subpackets)
  }

  /**
   * Consumes off the binary stream until it has a total of [numPackets] in the resulting list.
   */
  private fun parseSubpackets(numPackets: Int, binary: BinaryStream): List<BitPacket> = when (numPackets) {
    0 -> emptyList()
    else -> listOf(parseBinary(binary)) + parseSubpackets(numPackets - 1, binary)
  }

  /**
   * Consumes the entire binary stream into a list of packets. The binary stream should consist of a series of sibling
   * packets.
   */
  private fun parseAllPackets(binary: BinaryStream): List<BitPacket> = when {
    binary.hasNext() -> listOf(parseBinary(binary)) + parseAllPackets(binary)
    else -> emptyList()
  }
}

sealed class BitPacket(open val version: Int) {
  fun versionSum(): Int = when (this) {
    is LiteralPacket -> this.version
    is OperatorPacket -> this.version + this.subpackets.sumOf { it.versionSum() }
  }
}

data class LiteralPacket(
  override val version: Int,
  val value: Long
) : BitPacket(version)

data class OperatorPacket(
  override val version: Int,
  val packetTypeId: Int,
  val subpackets: List<BitPacket>
) : BitPacket(version)

/**
 * Helper class for consuming our binary digits and keeping track of what has already been consumed.
 *
 * Mutable; each digit can only be consumed once.
 */
private class BinaryStream(private val input: String) {
  private var index = 0

  /**
   * Take the next [length] digits and return them as a string.
   */
  fun take(length: Int): String {
    val output = input.substring(index, index + length)
    index += length
    return output
  }

  fun hasNext() = !input.substring(index).all { it == '0' }
}

fun main() {
  val packet = File("src/main/kotlin/advent/year2021/day16/input.txt")
    .readText()
    .trim()
    .let(BuoyancyInterchangeTransmission::parse)

  println(packet.versionSum())
}