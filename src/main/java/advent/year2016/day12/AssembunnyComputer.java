package advent.year2016.day12;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Preconditions;

public class AssembunnyComputer {

	private Map<String, Integer> registers = new HashMap<>();

	/**
	 * Several instructions want to be able to reference either a register
	 * (alphabetic) or a fixed value (numeric).
	 * 
	 * @param name
	 * @return
	 */
	int getValue(String name) {
		try {
			return Integer.parseInt(name);
		} catch (NumberFormatException e) {
			return this.registers.computeIfAbsent(name, any -> 0);
		}
	}

	private void setRegister(String name, int value) {
		this.registers.put(name, value);
	}

	public void executeProgram(List<String> program) {

		List<AssembunnyInstruction> instructions = program.stream() //
				.map(AssembunnyInstruction::of) //
				.collect(toList());

		int instructionPointer = 0;

		while (instructionPointer < instructions.size()) {
			// Debug output if interested.
			// System.out.println("State " + this.registers);
			// System.out.println("Pointer " + instructionPointer);
			// System.out.println("Executing " +
			// program.get(instructionPointer));
			// System.out.println();

			instructionPointer += instructions.get(instructionPointer).execute(this);
		}
	}

	private static class AssembunnyInstruction {

		private final Consumer<AssembunnyComputer> mutateComputer;
		private final Function<AssembunnyComputer, Integer> stepsToAdvance;

		public static AssembunnyInstruction of(String representation) {
			return Arrays.stream(OpCodes.values()) //
					.filter(code -> code.matches(representation)) //
					.map(code -> code.create(representation)) //
					.findFirst() //
					.orElseThrow(() -> new IllegalArgumentException("Bad instruction " + representation));
		}

		private AssembunnyInstruction(Consumer<AssembunnyComputer> mutateComputer) {
			this(mutateComputer, any -> 1);
		}

		public AssembunnyInstruction(Consumer<AssembunnyComputer> mutateComputer,
				Function<AssembunnyComputer, Integer> stepsToAdvance) {
			super();
			this.mutateComputer = mutateComputer;
			this.stepsToAdvance = stepsToAdvance;
		}

		public int execute(AssembunnyComputer computer) {
			this.mutateComputer.accept(computer);
			return this.stepsToAdvance.apply(computer);
		}
	}

	private static enum OpCodes {

		CPY("cpy (-?\\w+) (\\w+)") {
			@Override
			protected AssembunnyInstruction create(Matcher matchResult) {
				String from = matchResult.group(1);
				String to = matchResult.group(2);
				return new AssembunnyInstruction(computer -> computer.setRegister(to, computer.getValue(from)));

			}
		}, //

		INC("inc (\\w+)") {
			@Override
			protected AssembunnyInstruction create(Matcher matchResult) {
				String register = matchResult.group(1);
				return new AssembunnyInstruction(
						computer -> computer.setRegister(register, computer.getValue(register) + 1));
			}
		}, //

		DEC("dec (\\w+)") {
			@Override
			protected AssembunnyInstruction create(Matcher matchResult) {

				String register = matchResult.group(1);
				return new AssembunnyInstruction(
						computer -> computer.setRegister(register, computer.getValue(register) - 1));
			}
		}, //

		JMP("jnz (\\w+) (-?\\w+)") {
			@Override
			protected AssembunnyInstruction create(Matcher matchResult) {
				String value = matchResult.group(1);
				int jump = Integer.parseInt(matchResult.group(2));

				return new AssembunnyInstruction(computer -> {
				} , computer -> {
					if (computer.getValue(value) == 0) {
						return 1;
					} else {
						return jump;
					}
				});
			}
		};

		private OpCodes(String regex) {
			this.pattern = Pattern.compile(regex);
		}

		private final Pattern pattern;

		public boolean matches(String representation) {
			return this.pattern.matcher(representation).matches();
		}

		public AssembunnyInstruction create(String input) {
			Matcher matcher = this.pattern.matcher(input);
			Preconditions.checkArgument(matcher.matches());
			return this.create(matcher);
		}

		protected abstract AssembunnyInstruction create(Matcher matchResult);
	}

	public static void main(String[] args) throws IOException {
		Path inputFilePath = Paths.get("src/main/java/advent/year2016/day12/input.txt");

		AssembunnyComputer computer = new AssembunnyComputer();

		List<String> lines = Files.readAllLines(inputFilePath);
		computer.executeProgram(lines);
		System.out.println(computer.getValue("a"));

		AssembunnyComputer partTwo = new AssembunnyComputer();
		partTwo.setRegister("c", 1);
		partTwo.executeProgram(lines);
		System.out.println(partTwo.getValue("a"));
	}
}
