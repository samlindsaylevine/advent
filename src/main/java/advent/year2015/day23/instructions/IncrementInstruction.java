package advent.year2015.day23.instructions;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import advent.year2015.day23.AssemblyComputer;
import advent.year2015.day23.AssemblyComputer.Register;
import advent.year2015.day23.Instruction;

public class IncrementInstruction extends Instruction {
	private final String register;

	public IncrementInstruction(String register) {
		this.register = register;
	}

	@Override
	public void updateComputer(AssemblyComputer computer) {
		Register r = computer.getRegister(this.register);
		r.setValue(r.getValue() + 1);
	}

	private static Pattern REGEX = Pattern.compile("inc (\\w)");

	public static Optional<IncrementInstruction> tryParse(String input) {
		Matcher matcher = REGEX.matcher(input);

		if (matcher.matches()) {
			return Optional.of(new IncrementInstruction(matcher.group(1)));
		} else {
			return Optional.empty();
		}
	}
}
