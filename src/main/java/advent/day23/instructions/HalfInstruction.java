package advent.day23.instructions;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import advent.day23.AssemblyComputer;
import advent.day23.AssemblyComputer.Register;
import advent.day23.Instruction;

public class HalfInstruction extends Instruction {
	private final String register;

	public HalfInstruction(String register) {
		this.register = register;
	}

	@Override
	public void updateComputer(AssemblyComputer computer) {
		Register r = computer.getRegister(this.register);
		r.setValue(r.getValue() / 2);
	}

	private static Pattern REGEX = Pattern.compile("hlf (\\w)");

	public static Optional<HalfInstruction> tryParse(String input) {
		Matcher matcher = REGEX.matcher(input);

		if (matcher.matches()) {
			return Optional.of(new HalfInstruction(matcher.group(1)));
		} else {
			return Optional.empty();
		}
	}
}
