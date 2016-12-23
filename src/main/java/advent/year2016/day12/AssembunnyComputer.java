package advent.year2016.day12;

import static advent.utils.CollectorUtils.toArrayList;

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
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Preconditions;

/**
 * This class has been extended on day 23 to also handle the "toggle"
 * instruction.
 */
public class AssembunnyComputer {

	private Map<String, Long> registers = new HashMap<>();

	private List<AssembunnyInstruction> instructions;
	int instructionPointer = 0;

	/**
	 * Several instructions want to be able to reference either a register
	 * (alphabetic) or a fixed value (numeric).
	 * 
	 * @param name
	 * @return
	 */
	public long getValue(String name) {
		try {
			return Long.parseLong(name);
		} catch (NumberFormatException e) {
			return this.registers.computeIfAbsent(name, any -> 0L);
		}
	}

	public void setRegister(String name, long value) {
		this.registers.put(name, value);
	}

	public void executeProgram(List<String> program) {

		int stepsTaken = 0;

		this.instructions = program.stream() //
				.map(AssembunnyInstruction::of) //
				.collect(toArrayList());

		this.instructionPointer = 0;

		while (instructionPointer < instructions.size()) {
			// Debug output if interested.
			stepsTaken++;
			if (stepsTaken % 1000000 == 0) {
				System.out.println("State " + this.registers);
				System.out.println("Pointer " + instructionPointer);
				System.out.println("Executing " + program.get(instructionPointer));
				System.out.println();
			}

			instructionPointer += instructions.get(instructionPointer).execute(this);
		}
	}

	private static class AssembunnyInstruction {

		private final Consumer<AssembunnyComputer> mutateComputer;
		private final Function<AssembunnyComputer, Long> stepsToAdvance;
		private final Supplier<AssembunnyInstruction> toggledVersion;

		public static AssembunnyInstruction of(String representation) {
			return Arrays.stream(OpCodes.values()) //
					.filter(code -> code.matches(representation)) //
					.map(code -> code.create(representation)) //
					.findFirst() //
					.orElseThrow(() -> new IllegalArgumentException("Bad instruction " + representation));
		}

		private static AssembunnyInstruction acting(Consumer<AssembunnyComputer> mutateComputer,
				Supplier<AssembunnyInstruction> toggledVersion) {
			return new AssembunnyInstruction(mutateComputer, any -> 1L, toggledVersion);
		}

		private static AssembunnyInstruction stepping(Function<AssembunnyComputer, Long> stepsToAdvance,
				Supplier<AssembunnyInstruction> toggledVersion) {
			return new AssembunnyInstruction(computer -> {
			}, stepsToAdvance, toggledVersion);
		}

		private AssembunnyInstruction(Consumer<AssembunnyComputer> mutateComputer,
				Function<AssembunnyComputer, Long> stepsToAdvance, Supplier<AssembunnyInstruction> toggledVersion) {
			super();
			this.mutateComputer = mutateComputer;
			this.stepsToAdvance = stepsToAdvance;
			this.toggledVersion = toggledVersion;
		}

		public long execute(AssembunnyComputer computer) {
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
				return AssembunnyInstruction.acting(computer -> computer.setRegister(to, computer.getValue(from)),
						() -> JMP.create(matchResult));
			}
		}, //

		INC("inc (\\w+)") {
			@Override
			protected AssembunnyInstruction create(Matcher matchResult) {
				String register = matchResult.group(1);
				return AssembunnyInstruction.acting(
						computer -> computer.setRegister(register, computer.getValue(register) + 1),
						() -> DEC.create(matchResult));
			}
		}, //

		DEC("dec (\\w+)") {
			@Override
			protected AssembunnyInstruction create(Matcher matchResult) {

				String register = matchResult.group(1);
				return AssembunnyInstruction.acting(
						computer -> computer.setRegister(register, computer.getValue(register) - 1),
						() -> INC.create(matchResult));
			}
		}, //

		JMP("jnz (\\w+) (-?\\w+)") {
			@Override
			protected AssembunnyInstruction create(Matcher matchResult) {
				String value = matchResult.group(1);
				String jumpStr = matchResult.group(2);

				return AssembunnyInstruction.stepping(
						computer -> computer.getValue(value) == 0 ? 1 : computer.getValue(jumpStr),
						() -> OpCodes.CPY.create(matchResult));
			}
		},

		TGL("tgl (\\w+)") {

			@Override
			protected AssembunnyInstruction create(Matcher matchResult) {
				String stepsStr = matchResult.group(1);
				return AssembunnyInstruction.acting(computer -> {
					long steps = computer.getValue(stepsStr);
					int index = (int) (computer.instructionPointer + steps);
					if (index < 0 || index >= computer.instructions.size()) {
						return;
					}

					AssembunnyInstruction existing = computer.instructions.get(index);
					computer.instructions.set(index, existing.toggledVersion.get());
				}, () -> INC.create(matchResult));
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
