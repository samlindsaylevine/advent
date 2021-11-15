package advent.year2016.day9;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class CompressedStringTest {

	@Test
	public void advent() {
		assertEquals("ADVENT", new CompressedString("ADVENT").singlyDecompress());
	}

	@Test
	public void a1x5bc() {
		assertEquals("ABBBBBC", new CompressedString("A(1x5)BC").singlyDecompress());
	}

	@Test
	public void xyz() {
		assertEquals("XYZXYZXYZ", new CompressedString("(3x3)XYZ").singlyDecompress());
	}

	@Test
	public void a2x2bcd2x2efg() {
		assertEquals("ABCBCDEFEFG", new CompressedString("A(2x2)BCD(2x2)EFG").singlyDecompress());
	}

	@Test
	public void nestedMarker() {
		assertEquals("(1x3)A", new CompressedString("(6x1)(1x3)A").singlyDecompress());
	}

	@Test
	public void nestedMarkerRepeats() {
		assertEquals("X(3x3)ABC(3x3)ABCY", new CompressedString("X(8x2)(3x3)ABCY").singlyDecompress());
	}

	@Test
	public void recursiveXyz() {
		assertEquals("XYZXYZXYZ".length(), //
				new CompressedString("(3x3)XYZ").recursivelyDecompressedLength());
	}

	@Test
	public void recursiveNested() {
		assertEquals("XABCABCABCABCABCABCY".length(), //
				new CompressedString("X(8x2)(3x3)ABCY").recursivelyDecompressedLength());
	}

	@Test
	public void lotsOfAs() {
		assertEquals(241920, //
				new CompressedString("(27x12)(20x12)(13x14)(7x10)(1x12)A").recursivelyDecompressedLength());
	}

	@Test
	public void recursiveMultipleNesting() {
		assertEquals(445, //
				new CompressedString("(25x3)(3x3)ABC(2x3)XY(5x2)PQRSTX(18x9)(3x2)TWO(5x7)SEVEN")
						.recursivelyDecompressedLength());
	}

}
