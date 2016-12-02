package advent.year2016.day2;

import static org.junit.Assert.assertEquals;

import java.util.stream.Stream;

import org.junit.Test;

public class BathroomCodeTest {

	@Test
	public void referenceSquare() {
		Stream<String> referenceLines = Stream.of( //
				"ULL", //
				"RRDDD", //
				"LURDL", //
				"UUUUD");

		BathroomCode code = BathroomCode.squareKeys();
		referenceLines.forEach(code::applyInstructionLine);

		assertEquals("1985", code.keyString());
	}

	@Test
	public void referenceDiamond() {
		Stream<String> referenceLines = Stream.of( //
				"ULL", //
				"RRDDD", //
				"LURDL", //
				"UUUUD");

		BathroomCode code = BathroomCode.diamondKeys();
		referenceLines.forEach(code::applyInstructionLine);

		assertEquals("5DB3", code.keyString());
	}

}
