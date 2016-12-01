package advent.year2015.day23.instructions;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import advent.year2015.day23.AssemblyComputer;
import advent.year2015.day23.Instruction;

public class JumpIfEvenInstruction extends Instruction {
	private final String register;
	private final int offset;

	public JumpIfEvenInstruction(String register, int offset) {
		this.register = register;
		this.offset = offset;
	}

	@Override
	public int nextInstructionIndexDelta(AssemblyComputer computer) {
		if (computer.getRegister(this.register).getValue() % 2 == 0) {
			return this.offset;
		} else {
			return super.nextInstructionIndexDelta(computer);
		}
	}

	private static Pattern REGEX = Pattern.compile("jie (\\w), ([+-]?\\d+)");

	public static Optional<JumpIfEvenInstruction> tryParse(String input) {
		Matcher matcher = REGEX.matcher(input);

		if (matcher.matches()) {
			return Optional.of(new JumpIfEvenInstruction(matcher.group(1), Integer.valueOf(matcher.group(2))));
		} else {
			return Optional.empty();
		}
	}
}
