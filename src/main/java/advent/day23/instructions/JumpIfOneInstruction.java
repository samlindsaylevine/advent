package advent.day23.instructions;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import advent.day23.AssemblyComputer;
import advent.day23.Instruction;

public class JumpIfOneInstruction extends Instruction {
	private final String register;
	private final int offset;

	public JumpIfOneInstruction(String register, int offset) {
		this.register = register;
		this.offset = offset;
	}

	@Override
	public int nextInstructionIndexDelta(AssemblyComputer computer) {
		if (computer.getRegister(this.register).getValue() == 1) {
			return this.offset;
		} else {
			return super.nextInstructionIndexDelta(computer);
		}
	}

	private static Pattern REGEX = Pattern.compile("jio (\\w), ([+-]?\\d+)");

	public static Optional<JumpIfOneInstruction> tryParse(String input) {
		Matcher matcher = REGEX.matcher(input);

		if (matcher.matches()) {
			return Optional.of(new JumpIfOneInstruction(matcher.group(1), Integer.valueOf(matcher.group(2))));
		} else {
			return Optional.empty();
		}
	}
}
