package advent.day3;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SantaTraversalTest {

	@Test
	public void oneMove() {
		SantaTraversal traversal = new SantaTraversal();
		traversal.traverse(">");
		assertEquals(2, traversal.visitedCount());
	}

	@Test
	public void square() {
		SantaTraversal traversal = new SantaTraversal();
		traversal.traverse("^>v<");
		assertEquals(4, traversal.visitedCount());
	}

	@Test
	public void backAndForth() {
		SantaTraversal traversal = new SantaTraversal();
		traversal.traverse("^v^v^v^v^v");
		assertEquals(2, traversal.visitedCount());
	}

}
