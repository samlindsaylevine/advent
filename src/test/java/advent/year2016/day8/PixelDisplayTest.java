package advent.year2016.day8;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PixelDisplayTest {

	@Test
	public void initialize() {
		assertEquals(0, new PixelDisplay().litPixelCount());
	}

	@Test
	public void string() {
		String expected = ".......\n" + //
				".......\n" + //
				".......";

		assertEquals(expected, new PixelDisplay(7, 3).toString());
	}

	@Test
	public void reference() {
		PixelDisplay display = new PixelDisplay(7, 3);
		display.applyOperation("rect 3x2");
		display.applyOperation("rotate column x=1 by 1");
		display.applyOperation("rotate row y=0 by 4");
		display.applyOperation("rotate column x=1 by 1");

		String expected = ".#..#.#\n" + //
				"#.#....\n" + //
				".#.....";

		assertEquals(expected, display.toString());

		assertEquals(6, display.litPixelCount());
	}

}
