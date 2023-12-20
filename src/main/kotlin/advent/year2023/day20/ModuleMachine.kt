package advent.year2023.day20

import advent.year2019.day12.lcm
import java.io.File

/**
 * --- Day 20: Pulse Propagation ---
 * With your help, the Elves manage to find the right parts and fix all of the machines. Now, they just need to send
 * the command to boot up the machines and get the sand flowing again.
 * The machines are far apart and wired together with long cables. The cables don't connect to the machines directly,
 * but rather to communication modules attached to the machines that perform various initialization tasks and also act
 * as communication relays.
 * Modules communicate using pulses. Each pulse is either a high pulse or a low pulse. When a module sends a pulse, it
 * sends that type of pulse to each module in its list of destination modules.
 * There are several different types of modules:
 * Flip-flop modules (prefix %) are either on or off; they are initially off. If a flip-flop module receives a high
 * pulse, it is ignored and nothing happens. However, if a flip-flop module receives a low pulse, it flips between on
 * and off. If it was off, it turns on and sends a high pulse. If it was on, it turns off and sends a low pulse.
 * Conjunction modules (prefix &) remember the type of the most recent pulse received from each of their connected
 * input modules; they initially default to remembering a low pulse for each input. When a pulse is received, the
 * conjunction module first updates its memory for that input. Then, if it remembers high pulses for all inputs, it
 * sends a low pulse; otherwise, it sends a high pulse.
 * There is a single broadcast module (named broadcaster). When it receives a pulse, it sends the same pulse to all of
 * its destination modules.
 * Here at Desert Machine Headquarters, there is a module with a single button on it called, aptly, the button module.
 * When you push the button, a single low pulse is sent directly to the broadcaster module.
 * After pushing the button, you must wait until all pulses have been delivered and fully handled before pushing it
 * again. Never push the button if modules are still processing pulses.
 * Pulses are always processed in the order they are sent. So, if a pulse is sent to modules a, b, and c, and then
 * module a processes its pulse and sends more pulses, the pulses sent to modules b and c would have to be handled
 * first.
 * The module configuration (your puzzle input) lists each module. The name of the module is preceded by a symbol
 * identifying its type, if any. The name is then followed by an arrow and a list of its destination modules. For
 * example:
 * broadcaster -> a, b, c
 * %a -> b
 * %b -> c
 * %c -> inv
 * &inv -> a
 *
 * In this module configuration, the broadcaster has three destination modules named a, b, and c. Each of these modules
 * is a flip-flop module (as indicated by the % prefix). a outputs to b which outputs to c which outputs to another
 * module named inv. inv is a conjunction module (as indicated by the & prefix) which, because it has only one input,
 * acts like an inverter (it sends the opposite of the pulse type it receives); it outputs to a.
 * By pushing the button once, the following pulses are sent:
 * button -low-> broadcaster
 * broadcaster -low-> a
 * broadcaster -low-> b
 * broadcaster -low-> c
 * a -high-> b
 * b -high-> c
 * c -high-> inv
 * inv -low-> a
 * a -low-> b
 * b -low-> c
 * c -low-> inv
 * inv -high-> a
 *
 * After this sequence, the flip-flop modules all end up off, so pushing the button again repeats the same sequence.
 * Here's a more interesting example:
 * broadcaster -> a
 * %a -> inv, con
 * &inv -> b
 * %b -> con
 * &con -> output
 *
 * This module configuration includes the broadcaster, two flip-flops (named a and b), a single-input conjunction
 * module (inv), a multi-input conjunction module (con), and an untyped module named output (for testing purposes). The
 * multi-input conjunction module con watches the two flip-flop modules and, if they're both on, sends a low pulse to
 * the output module.
 * Here's what happens if you push the button once:
 * button -low-> broadcaster
 * broadcaster -low-> a
 * a -high-> inv
 * a -high-> con
 * inv -low-> b
 * con -high-> output
 * b -high-> con
 * con -low-> output
 *
 * Both flip-flops turn on and a low pulse is sent to output! However, now that both flip-flops are on and con
 * remembers a high pulse from each of its two inputs, pushing the button a second time does something different:
 * button -low-> broadcaster
 * broadcaster -low-> a
 * a -low-> inv
 * a -low-> con
 * inv -high-> b
 * con -high-> output
 *
 * Flip-flop a turns off! Now, con remembers a low pulse from module a, and so it sends only a high pulse to output.
 * Push the button a third time:
 * button -low-> broadcaster
 * broadcaster -low-> a
 * a -high-> inv
 * a -high-> con
 * inv -low-> b
 * con -low-> output
 * b -low-> con
 * con -high-> output
 *
 * This time, flip-flop a turns on, then flip-flop b turns off. However, before b can turn off, the pulse sent to con
 * is handled first, so it briefly remembers all high pulses for its inputs and sends a low pulse to output. After
 * that, flip-flop b turns off, which causes con to update its state and send a high pulse to output.
 * Finally, with a on and b off, push the button a fourth time:
 * button -low-> broadcaster
 * broadcaster -low-> a
 * a -low-> inv
 * a -low-> con
 * inv -high-> b
 * con -high-> output
 *
 * This completes the cycle: a turns off, causing con to remember only low pulses and restoring all modules to their
 * original states.
 * To get the cables warmed up, the Elves have pushed the button 1000 times. How many pulses got sent as a result
 * (including the pulses sent by the button itself)?
 * In the first example, the same thing happens every time the button is pushed: 8 low pulses and 4 high pulses are
 * sent. So, after pushing the button 1000 times, 8000 low pulses and 4000 high pulses are sent. Multiplying these
 * together gives 32000000.
 * In the second example, after pushing the button 1000 times, 4250 low pulses and 2750 high pulses are sent.
 * Multiplying these together gives 11687500.
 * Consult your module configuration; determine the number of low pulses and high pulses that would be sent after
 * pushing the button 1000 times, waiting for all pulses to be fully handled after each push of the button. What do you
 * get if you multiply the total number of low pulses sent by the total number of high pulses sent?
 *
 * --- Part Two ---
 * The final machine responsible for moving the sand down to Island Island has a module attached named rx. The machine
 * turns on when a single low pulse is sent to rx.
 * Reset all modules to their default states. Waiting for all pulses to be fully handled after each button press, what
 * is the fewest number of button presses required to deliver a single low pulse to the module named rx?
 *
 */
class ModuleMachine(var modules: List<Module> = emptyList()) {
  companion object {
    fun of(input: String): ModuleMachine {
      val machine = ModuleMachine()
      val modules = input.trim().lines().map { line -> Module.of(line, machine) }
      machine.modules = modules
      return machine
    }
  }

  private val modulesByName by lazy { modules.associateBy { it.name } }
  private val pulsesSent: MutableMap<PulseType, Long> = mutableMapOf(PulseType.LOW to 0, PulseType.HIGH to 0)

  fun pressButton(times: Int): Unit = repeat(times) { this.pressButton() }

  /**
   * Returns true if any pulse met the provided condition during this button press.
   */
  private fun pressButton(condition: (Pulse) -> Boolean = { false }): Boolean {
    val pendingPulses = ArrayDeque<Pulse>()
    pendingPulses.add(Pulse(from = "button", to = "broadcaster", type = PulseType.LOW))

    var metCondition = false

    while (pendingPulses.isNotEmpty()) {
      val pulse = pendingPulses.removeFirst()
      if (condition(pulse)) metCondition = true
      pulsesSent.merge(pulse.type, 1, Long::plus)
      val target = modulesByName[pulse.to]
      if (target != null) {
        val nextPulseType = target.receive(pulse)
        if (nextPulseType != null) {
          pendingPulses.addAll(target.destinationModuleNames.map { to ->
            Pulse(from = target.name, to = to, type = nextPulseType)
          })
        }
      }
    }

    return metCondition
  }

  fun pulseSentProduct() = pulsesSent.values.reduce(Long::times)

  fun buttonPressesUntilHigh(name: String): Sequence<Int> {
    val condition = { pulse: Pulse -> pulse.from == name && pulse.type == PulseType.HIGH }
    val numbers = generateSequence(1) { it + 1 }
    return numbers.filter { this.pressButton(condition) }
  }
}

enum class PulseType { LOW, HIGH }

data class Pulse(val from: String, val to: String, val type: PulseType)

sealed class Module(val name: String, val destinationModuleNames: List<String>) {
  companion object {
    fun of(input: String, machine: ModuleMachine): Module {
      val (nameWithPrefix, destinationString) = input.split(" -> ")
      val destinationModuleNames = destinationString.split(", ")
      return when {
        nameWithPrefix.startsWith("%") -> FlipFlopModule(nameWithPrefix.drop(1), destinationModuleNames)
        nameWithPrefix.startsWith("&") -> ConjunctionModule(nameWithPrefix.drop(1), destinationModuleNames, machine)
        else -> BroadcastModule(destinationModuleNames)
      }
    }
  }

  /**
   * Returns the pulse type, if any, send to each of its destinations. May also mutate state.
   */
  abstract fun receive(incoming: Pulse): PulseType?
}

class BroadcastModule(destinationModuleNames: List<String>) : Module("broadcaster", destinationModuleNames) {
  override fun receive(incoming: Pulse) = incoming.type
}

class FlipFlopModule(name: String,
                     destinationModuleNames: List<String>,
                     var state: Boolean = false) : Module(name, destinationModuleNames) {
  override fun receive(incoming: Pulse) = when (incoming.type) {
    PulseType.HIGH -> null
    PulseType.LOW -> {
      this.state = !state
      if (state) PulseType.HIGH else PulseType.LOW
    }
  }
}

class ConjunctionModule(
        name: String,
        destinationModuleNames: List<String>,
        machine: ModuleMachine) : Module(name, destinationModuleNames) {
  private val inputModules: List<String> by lazy {
    machine.modules.filter { this.name in it.destinationModuleNames }.map { it.name }
  }
  private val mostRecentReceived: MutableMap<String, PulseType> by lazy {
    inputModules
            .associateWith { PulseType.LOW }
            .toMutableMap()
  }

  override fun receive(incoming: Pulse): PulseType {
    mostRecentReceived[incoming.from] = incoming.type
    return if (mostRecentReceived.values.all { it == PulseType.HIGH }) PulseType.LOW else PulseType.HIGH
  }
}

fun main() {
  val input = File("src/main/kotlin/advent/year2023/day20/input.txt").readText().trim()
  val machine = ModuleMachine.of(input)

  machine.pressButton(1000)
  println(machine.pulseSentProduct())

  // This seems like a problem where we need to read and understand what the input is doing to solve it.
  // In the general case, there is no option other than to just run it, but we tried running it for a while
  // and never got the appropriate low pulse to rx in a reasonable amount of time.
  // So, we will craft a solution that is particular to our input file.
  // In our input, only dg (a conjunction) sends to rx. That means we get an rx low pulse only when dg gets a high
  // input pulse from each of its inputs.
  // The inputs to dg are all also conjunctions, lk, zv, sp, and xt which have no other outputs. Each of these
  // conjunctions in turn has only one other input (another conjunction).
  // We can see that the broadcaster splits out to four different modules. Possibly there are four different sections
  // of the graph of modules that each act independently. Then we will get an rx low pulse the first time that
  // all of lk, zv, sp, and xt output high. Maybe those do so periodically or something, or a linear recurrence?
  // Doing a little investigation with the buttonPressesUntilHigh:
  println(ModuleMachine.of(input).buttonPressesUntilHigh("lk").take(5).toList())
  // We see that, for example "lk" is high on presses
  // 3823, 7646, 11469, 15292, 19115
  // Hey, that's promising! That's the first 5 multiples of 3823.
  // Let's try getting the first high-signal of all those four inputs to dg and then take the lcm of them.
  val inputsToDg = listOf("lk", "zv", "sp", "xt")
  val lcm = inputsToDg.map { name ->
    ModuleMachine.of(input)
            .buttonPressesUntilHigh(name)
            .first()
            .toLong()
  }.reduce(::lcm)
  println(lcm)
}