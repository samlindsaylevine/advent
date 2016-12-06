package advent.year2016.day6;

import static advent.year2016.day6.ErrorCorrection.correctingErrorsByLeastCommon;
import static advent.year2016.day6.ErrorCorrection.correctingErrorsByMostCommon;
import static org.junit.Assert.assertEquals;

import java.util.stream.Stream;

import org.junit.Test;

public class ErrorCorrectionTest {

	@Test
	public void referenceMostCommon() {
		Stream<String> input = Stream.of( //
				"eedadn", //
				"drvtee", //
				"eandsr", //
				"raavrd", //
				"atevrs", //
				"tsrnev", //
				"sdttsa", //
				"rasrtv", //
				"nssdts", //
				"ntnada", //
				"svetve", //
				"tesnvt", //
				"vntsnd", //
				"vrdear", //
				"dvrsen", //
				"enarar");

		assertEquals("easter", input.collect(correctingErrorsByMostCommon()));
	}

	@Test
	public void referenceLeastCommon() {
		Stream<String> input = Stream.of( //
				"eedadn", //
				"drvtee", //
				"eandsr", //
				"raavrd", //
				"atevrs", //
				"tsrnev", //
				"sdttsa", //
				"rasrtv", //
				"nssdts", //
				"ntnada", //
				"svetve", //
				"tesnvt", //
				"vntsnd", //
				"vrdear", //
				"dvrsen", //
				"enarar");

		assertEquals("advent", input.collect(correctingErrorsByLeastCommon()));
	}

}
