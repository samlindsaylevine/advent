package advent.year2015.day23;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

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
