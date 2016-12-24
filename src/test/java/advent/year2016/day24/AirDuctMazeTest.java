package advent.year2016.day24;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class AirDuctMazeTest {

	private AirDuctMaze referenceMaze() {
		List<String> rows = ImmutableList.of( //
				"###########", //
				"#0.1.....2#", //
				"#.#######.#", //
				"#4.......3#", //
				"###########");

		return new AirDuctMaze(rows);
	}

	@Test
	public void isOpen() {
		AirDuctMaze maze = referenceMaze();

		assertTrue(maze.isOpen(1, 1));
		assertTrue(maze.isOpen(2, 1));
		assertTrue(maze.isOpen(3, 1));

		assertTrue(maze.isOpen(9, 1));
		assertTrue(maze.isOpen(9, 2));
		assertTrue(maze.isOpen(9, 3));
	}

	@Test
	public void shortestLength() {
		assertEquals(14, referenceMaze().shortestLengthToVisitAll());
	}

	@Test
	public void shortestLengthWithReturn() {
		// 20 taken by inspection - have to do a lap.
		assertEquals(20, referenceMaze().shortestLengthToVistAllAndReturn());
	}

}
