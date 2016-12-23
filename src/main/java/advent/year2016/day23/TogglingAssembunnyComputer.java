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

		AssembunnyComputer partTwo = new AssembunnyComputer();
		partTwo.setRegister("a", 12);
		partTwo.executeProgram(lines);
		System.out.println(partTwo.getValue("a"));
	}

}
