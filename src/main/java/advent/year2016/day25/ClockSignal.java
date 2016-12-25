package advent.year2016.day25;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.IntStream;

import advent.year2016.day12.AssembunnyComputer;

/**
 * Mostly just extended the existing {@link AssembunnyComputer}.
 */
public class ClockSignal {

	private static final int SAMPLE_OUTPUT_SIZE = 10;

	private final List<String> instructions;

	public ClockSignal(List<String> instructions) {
		this.instructions = instructions;
	}

	private static boolean isDesiredOutput(List<Long> list) {
		// Debug information if desired.
		// System.out.println(list.stream().map(Object::toString).collect(joining("")));
		return IntStream.range(0, list.size()) //
				.allMatch(i -> list.get(i) == i % 2);
	}

	private List<Long> output(int initialValue) {
		AssembunnyComputer computer = new AssembunnyComputer();
		computer.setRegister("a", initialValue);
		computer.executeProgram(instructions, c -> c.getOutput().size() >= SAMPLE_OUTPUT_SIZE);
		return computer.getOutput();
	}

	public int desiredinitialValue() {
		return IntStream.iterate(0, i -> i + 1) //
				// Debug information if desired.
				// .peek(System.out::println) //
				.filter(i -> isDesiredOutput(this.output(i))) //
				.findFirst() //
				.getAsInt();
	}

	public static void main(String[] args) throws IOException {
		Path inputFilePath = Paths.get("src/main/java/advent/year2016/day25/input.txt");

		List<String> lines = Files.readAllLines(inputFilePath);
		ClockSignal signal = new ClockSignal(lines);
		System.out.println(signal.desiredinitialValue());
	}

}
