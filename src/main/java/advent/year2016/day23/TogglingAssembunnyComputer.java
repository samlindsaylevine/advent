package advent.year2016.day23;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import advent.year2016.day12.AssembunnyComputer;

public class TogglingAssembunnyComputer {

	/**
	 * Rather than add a new class, the existing one was updated to be able to
	 * do toggling behavior.
	 */
	public static void main(String[] args) throws IOException {
		Path inputFilePath = Paths.get("src/main/java/advent/year2016/day23/input.txt");

		AssembunnyComputer computer = new AssembunnyComputer();
		computer.setRegister("a", 7);

		List<String> lines = Files.readAllLines(inputFilePath);
		computer.executeProgram(lines);
		System.out.println(computer.getValue("a"));

		// This is unquestionably the stupid naive way of completing this
		// problem - clearly the desired solution is to come up with a lookahead
		// optimization that recognizes patterns in the assembly and translates
		// that into a higher order operation - e.g., multiplication.
		//
		// However, CPU time is cheaper than programmer time! So, I launched
		// this stupid naive way, went off to eat lunch, and came back in 2
		// hours and it had spit out the correct answer. Hooray??
		AssembunnyComputer partTwo = new AssembunnyComputer();
		partTwo.setRegister("a", 12);
		partTwo.executeProgram(lines);
		System.out.println(partTwo.getValue("a"));
	}

}
