package advent.year2016.day12;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class AssembunnyComputerTest {

	@Test
	public void reference() {
		AssembunnyComputer computer = new AssembunnyComputer();

		List<String> instructions = ImmutableList.of("cpy 41 a", //
				"inc a", //
				"inc a", //
				"dec a", //
				"jnz a 2", //
				"dec a");

		computer.executeProgram(instructions);

		assertEquals(42, computer.getValue("a"));
	}

	@Test
	public void toggleReference() {
		AssembunnyComputer computer = new AssembunnyComputer();

		List<String> instructions = ImmutableList.of( //
				"cpy 2 a", //
				"tgl a", //
				"tgl a", //
				"tgl a", //
				"cpy 1 a", //
				"dec a", //
				"dec a");

		computer.executeProgram(instructions);

		assertEquals(3, computer.getValue("a"));
	}
}
