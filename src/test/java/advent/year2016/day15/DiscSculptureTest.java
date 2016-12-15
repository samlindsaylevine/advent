package advent.year2016.day15;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.stream.Stream;

import org.junit.Test;

public class DiscSculptureTest {

	@Test
	public void reference() {
		Stream<String> input = Stream.of("Disc #1 has 5 positions; at time=0, it is at position 4.", //
				"Disc #2 has 2 positions; at time=0, it is at position 1.");

		DiscSculpture sculpture = new DiscSculpture(input);

		assertFalse(sculpture.capsulePasses(0));
		assertTrue(sculpture.capsulePasses(5));
		assertEquals(5, sculpture.firstSuccessfulDropTime());
	}

}
