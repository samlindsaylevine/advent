package advent.year2016.day2;

import static org.junit.Assert.assertEquals;

import java.util.stream.Stream;

import org.junit.Test;

public class BathroomCodeTest {

	@Test
	public void reference() {
		Stream<String> referenceLines = Stream.of( //
				"ULL", //
				"RRDDD", //
				"LURDL", //
				"UUUUD");

		BathroomCode code = BathroomCode.of(referenceLines);

		assertEquals("1985", code.keyString());
	}

}
