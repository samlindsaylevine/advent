package advent.year2016.day9;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CompressedStringTest {

	@Test
	public void advent() {
		assertEquals("ADVENT", new CompressedString("ADVENT").decompress());
	}

	@Test
	public void a1x5bc() {
		assertEquals("ABBBBBC", new CompressedString("A(1x5)BC").decompress());
	}

	@Test
	public void xyz() {
		assertEquals("XYZXYZXYZ", new CompressedString("(3x3)XYZ").decompress());
	}

	@Test
	public void a2x2bcd2x2efg() {
		assertEquals("ABCBCDEFEFG", new CompressedString("A(2x2)BCD(2x2)EFG").decompress());
	}

	@Test
	public void nestedMarker() {
		assertEquals("(1x3)A", new CompressedString("(6x1)(1x3)A").decompress());
	}

	@Test
	public void nestedMarkerRepeats() {
		assertEquals("X(3x3)ABC(3x3)ABCY", new CompressedString("X(8x2)(3x3)ABCY").decompress());
	}

}
