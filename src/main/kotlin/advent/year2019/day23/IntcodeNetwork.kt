package advent.year2019.day23

import advent.year2019.day13.parseIntcodeFromFile
import advent.year2019.day5.IntcodeComputer
import java.util.concurrent.*

/**
 * --- Day 23: Category Six ---
 * The droids have finished repairing as much of the ship as they can.  Their report indicates that this was a Category
 * 6 disaster - not because it was that bad, but because it destroyed the stockpile of Category 6 network cables as
 * well as most of the ship's network infrastructure.
 * You'll need to rebuild the network from scratch.
 * The computers on the network are standard Intcode computers that communicate by sending packets to each other. 
 * There are 50 of them in total, each running a copy of the same Network Interface Controller (NIC) software (your
 * puzzle input). The computers have network addresses 0 through 49; when each computer boots up, it will request its
 * network address via a single input instruction. Be sure to give each computer a unique network address.
 * Once a computer has received its network address, it will begin doing work and communicating over the network by
 * sending and receiving packets. All packets contain two values named X and Y. Packets sent to a computer are queued
 * by the recipient and read in the order they are received.
 * To send a packet to another computer, the NIC will use three output instructions that provide the destination
 * address of the packet followed by its X and Y values.  For example, three output instructions that provide the
 * values 10, 20, 30 would send a packet with X=20 and Y=30 to the computer with address 10.
 * To receive a packet from another computer, the NIC will use an input instruction.  If the incoming packet queue is
 * empty, provide -1.  Otherwise, provide the X value of the next packet; the computer will then use a second input
 * instruction to receive the Y value for the same packet.  Once both values of the packet are read in this way, the
 * packet is removed from the queue.
 * Note that these input and output instructions never block. Specifically, output instructions do not wait for the
 * sent packet to be received - the computer might send multiple packets before receiving any. Similarly, input
 * instructions do not wait for a packet to arrive - if no packet is waiting, input instructions should receive -1.
 * Boot up all 50 computers and attach them to your network.  What is the Y value of the first packet sent to address
 * 255?
 * 
 * --- Part Two ---
 * Packets sent to address 255 are handled by a device called a NAT (Not Always Transmitting). The NAT is responsible
 * for managing power consumption of the network by blocking certain packets and watching for idle periods in the
 * computers.
 * If a packet would be sent to address 255, the NAT receives it instead.  The NAT remembers only the last packet it
 * receives; that is, the data in each packet it receives overwrites the NAT's packet memory with the new packet's X
 * and Y values.
 * The NAT also monitors all computers on the network.  If all computers have empty incoming packet queues and are
 * continuously trying to receive packets without sending packets, the network is considered idle.
 * Once the network is idle, the NAT sends only the last packet it received to address 0; this will cause the computers
 * on the network to resume activity.  In this way, the NAT can throttle power consumption of the network when the ship
 * needs power in other areas.
 * Monitor packets released to the computer at address 0 by the NAT.  What is the first Y value delivered by the NAT to
 * the computer at address 0 twice in a row?
 * 
 */
class IntcodeNetwork(val program: List<Long>) {
  private val computers = (0L..49L).associateWith { NetworkedComputer(it, program, this) }
  private val nat = NotAlwaysTransferring(this)

  fun send(address: Long, packet: Packet) {
    // println("Sending $packet to $address")
    if (address == 255L) {
      nat.send(packet)
    } else {
      val computer = computers[address]
      if (computer == null) {
        println("Dropping packet to invalid address $address")
      } else {
        computer.send(packet)
      }
    }

  }

  fun isIdle() = computers.values.all { it.isIdle() }

  fun run(until: NetworkMode): Long {
    val executor = Executors.newFixedThreadPool(computers.size + 1) { runnable ->
      Thread(runnable).apply { isDaemon = true }
    }

    executor.submit(nat::start)

    computers.values.forEach {
      executor.submit(it::start)
    }

    val resultFuture = when (until) {
      NetworkMode.FIRST_PACKET_TO_NAT -> nat.firstReceived
      NetworkMode.FIRST_REPEATED_PACKET_FROM_NAT -> nat.firstSentTwice
    }
    val result = resultFuture.get()

    executor.shutdownNow()

    return result.y
  }
}

class NetworkedComputer(private val networkAddress: Long,
                        private val program: List<Long>,
                        private val network: IntcodeNetwork) {
  val queue: BlockingQueue<Packet> = LinkedBlockingQueue()

  private var everGotNetworkAddress = false
  private var partialInput: Long? = null
  private val partialOutput = mutableListOf<Long>()

  @Volatile
  var seekingInput: Boolean = false

  fun isIdle() = queue.isEmpty() && seekingInput

  fun send(packet: Packet) = queue.put(packet)

  fun start() {
    val computer = IntcodeComputer()

    computer.execute(program, this::getInput, this::sendOutput)
  }

  private fun getInput(): Long {
    if (!everGotNetworkAddress) {
      everGotNetworkAddress = true
      return networkAddress
    }

    val existing = partialInput
    if (existing != null) {
      partialInput = null
      return existing
    }

    val fromQueue = queue.poll()

    if (fromQueue == null) {
      seekingInput = true
      return -1
    }

    seekingInput = false
    partialInput = fromQueue.y
    return fromQueue.x
  }

  private fun sendOutput(output: Long) {
    if (partialOutput.size == 2) {
      network.send(partialOutput[0], Packet(partialOutput[1], output))
      partialOutput.clear()
    } else {
      partialOutput.add(output)
    }
  }
}


data class Packet(val x: Long, val y: Long)

class NotAlwaysTransferring(val network: IntcodeNetwork) {
  val firstReceived = CompletableFuture<Packet>()

  private var lastReceived: Packet? = null
  private var lastSent: Packet? = null

  val firstSentTwice = CompletableFuture<Packet>()

  private val queue: BlockingQueue<Packet> = LinkedBlockingQueue()

  fun send(packet: Packet) = queue.put(packet)

  fun start() {
    // println("NAT starting")
    while (true) {
      val packet = queue.poll()
      if (packet != null) {
        // println("NAT received $packet")
        if (!firstReceived.isDone) firstReceived.complete(packet)
        lastReceived = packet
      } else if (network.isIdle() && queue.isEmpty()) {
        val toSend = lastReceived
        if (toSend == null) {
          // println("Network is idle but NAT never got a packet")
        } else {
          if (lastSent == toSend) firstSentTwice.complete(toSend)
          lastSent = toSend
          println("NAT sending $toSend")
          network.send(0, toSend)
        }
      }
    }
  }
}

enum class NetworkMode {
  FIRST_PACKET_TO_NAT,
  FIRST_REPEATED_PACKET_FROM_NAT
}

fun main() {
  val program = parseIntcodeFromFile("src/main/kotlin/advent/year2019/day23/input.txt")

  val part1 = IntcodeNetwork(program).run(NetworkMode.FIRST_PACKET_TO_NAT)
  println("Part 1: $part1")

  val part2 = IntcodeNetwork(program).run(NetworkMode.FIRST_REPEATED_PACKET_FROM_NAT)
  println("Part 2: $part2")
}