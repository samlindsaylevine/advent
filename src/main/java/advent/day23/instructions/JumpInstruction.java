package advent.day23.instructions;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import advent.day23.AssemblyComputer;
import advent.day23.Instruction;

public class JumpInstruction extends Instruction {
	private final int offset;

	public JumpInstruction(int offset) {
		this.offset = offset;
	}

	@Override
	public int nextInstructionIndexDelta(AssemblyComputer computer) {
		return this.offset;
	}

	private static Pattern REGEX = Pattern.compile("jmp ([+-]?\\d+)");

	public static Optional<JumpInstruction> tryParse(String input) {
		Matcher matcher = REGEX.matcher(input);

		if (matcher.matches()) {
			return Optional.of(new JumpInstruction(Integer.valueOf(matcher.group(1))));
		} else {
			return Optional.empty();
		}
	}
}
