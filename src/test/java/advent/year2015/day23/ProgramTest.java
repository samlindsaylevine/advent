package advent.year2015.day23;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class ProgramTest {

	@Test
	public void reference() {
		List<Instruction> instructions = ImmutableList.of( //
				Instruction.fromString("inc a"), //
				Instruction.fromString("jio a, +2"), //
				Instruction.fromString("tpl a"), //
				Instruction.fromString("inc a"));

		AssemblyComputer output = new Program(instructions).execute();

		assertEquals(2, output.getRegister("a").getValue());
	}

}
