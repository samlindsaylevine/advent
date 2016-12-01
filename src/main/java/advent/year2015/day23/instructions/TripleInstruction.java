package advent.year2015.day23.instructions;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import advent.year2015.day23.AssemblyComputer;
import advent.year2015.day23.AssemblyComputer.Register;
import advent.year2015.day23.Instruction;

public class TripleInstruction extends Instruction {
	private final String register;

	public TripleInstruction(String register) {
		this.register = register;
	}

	@Override
	public void updateComputer(AssemblyComputer computer) {
		Register r = computer.getRegister(this.register);
		r.setValue(r.getValue() * 3);
	}

	private static Pattern REGEX = Pattern.compile("tpl (\\w)");

	public static Optional<TripleInstruction> tryParse(String input) {
		Matcher matcher = REGEX.matcher(input);

		if (matcher.matches()) {
			return Optional.of(new TripleInstruction(matcher.group(1)));
		} else {
			return Optional.empty();
		}
	}
}
