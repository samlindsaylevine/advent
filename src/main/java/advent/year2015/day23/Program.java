package advent.year2015.day23;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * --- Day 23: Opening the Turing Lock ---
 * Little Jane Marie just got her very first computer for Christmas from some unknown benefactor.  It comes with
 * instructions and an example program, but the computer itself seems to be malfunctioning.  She's curious what the
 * program does, and would like you to help her run it.
 * The manual explains that the computer supports two registers and six instructions (truly, it goes on to remind the
 * reader, a state-of-the-art technology). The registers are named a and b, can hold any non-negative integer, and
 * begin with a value of 0.  The instructions are as follows:
 * 
 * hlf r sets register r to half its current value, then continues with the next instruction.
 * tpl r sets register r to triple its current value, then continues with the next instruction.
 * inc r increments register r, adding 1 to it, then continues with the next instruction.
 * jmp offset is a jump; it continues with the instruction offset away relative to itself.
 * jie r, offset is like jmp, but only jumps if register r is even ("jump if even").
 * jio r, offset is like jmp, but only jumps if register r is 1 ("jump if one", not odd).
 * 
 * All three jump instructions work with an offset relative to that instruction.  The offset is always written with a
 * prefix + or - to indicate the direction of the jump (forward or backward, respectively).  For example, jmp +1 would
 * simply continue with the next instruction, while jmp +0 would continuously jump back to itself forever.
 * The program exits when it tries to run an instruction beyond the ones defined.
 * For example, this program sets a to 2, because the jio instruction causes it to skip the tpl instruction:
 * inc a
 * jio a, +2
 * tpl a
 * inc a
 * 
 * What is the value in register b when the program in your puzzle input is finished executing?
 * 
 * --- Part Two ---
 * The unknown benefactor is very thankful for releasi-- er, helping little Jane Marie with her computer.  Definitely
 * not to distract you, what is the value in register b after the program is finished executing if register a starts as
 * 1 instead?
 * 
 */
public class Program {

	private final List<Instruction> instructions;

	Program(List<Instruction> instructions) {
		this.instructions = instructions;
	}

	public AssemblyComputer execute() {
		return this.execute(new AssemblyComputer());
	}

	public AssemblyComputer execute(AssemblyComputer computer) {
		int index = 0;

		while (0 <= index && index < this.instructions.size()) {
			Instruction instruction = this.instructions.get(index);

			instruction.updateComputer(computer);

			index += instruction.nextInstructionIndexDelta(computer);
		}

		return computer;
	}

	public static Program fromFile() throws IOException {
		return new Program(Files.lines(Paths.get("src/main/java/advent/year2015/day23/input.txt")) //
				.map(Instruction::fromString) //
				.collect(toList()));
	}

	public static AssemblyComputer partOneFinalState() throws IOException {
		return fromFile().execute();
	}

	public static AssemblyComputer partTwoFinalState() throws IOException {
		AssemblyComputer computer = new AssemblyComputer();
		computer.getRegister("a").setValue(1);
		return fromFile().execute(computer);
	}

	public static void main(String[] args) throws IOException {
		System.out.println(partTwoFinalState());
	}

}