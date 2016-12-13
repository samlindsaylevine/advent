package advent.year2016.day13;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CubicleMazeTest {

	private CubicleMaze reference = new CubicleMaze(10);

	@Test
	public void isOpen00() {
		assertTrue(this.reference.isOpen(0, 0));
	}

	@Test
	public void isOpen01() {
		assertTrue(this.reference.isOpen(0, 1));
	}

	@Test
	public void isOpen02() {
		assertFalse(this.reference.isOpen(0, 2));
	}

	@Test
	public void isOpen10() {
		assertFalse(this.reference.isOpen(1, 0));
	}

	@Test
	public void isOpen11() {
		assertTrue(this.reference.isOpen(1, 1));
	}

	@Test
	public void isOpen12() {
		assertTrue(this.reference.isOpen(1, 2));
	}

	@Test
	public void isOpen20() {
		assertTrue(this.reference.isOpen(2, 0));
	}

	@Test
	public void isOpen21() {
		assertFalse(this.reference.isOpen(2, 1));
	}

	@Test
	public void isOpen22() {
		assertTrue(this.reference.isOpen(2, 2));
	}

	@Test
	public void toStringArea() {
		String expected = ".#.####.##\n" + //
				"..#..#...#\n" + //
				"#....##...\n" + //
				"###.#.###.\n" + //
				".##..#..#.\n" + //
				"..##....#.\n" + //
				"#...##.###";

		assertEquals(expected, this.reference.toString(10, 7));
	}

	@Test
	public void pathLength() {
		assertEquals(11, this.reference.pathLength(1, 1, 7, 4));
	}

	@Test
	public void locationsReachable() {
		// Counts taken by inspection from the diagram above.
		assertEquals(1, this.reference.locationsReachable(1, 1, 0));
		assertEquals(3, this.reference.locationsReachable(1, 1, 1));
		assertEquals(5, this.reference.locationsReachable(1, 1, 2));
		assertEquals(6, this.reference.locationsReachable(1, 1, 3));
		assertEquals(9, this.reference.locationsReachable(1, 1, 4));
	}

}
