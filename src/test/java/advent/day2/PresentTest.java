package advent.day2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PresentTest {

	@Test
	public void testRibbon() {
		assertEquals(34, new Present("2x3x4").ribbonLength());
		assertEquals(14, new Present("1x1x10").ribbonLength());
	}

}
