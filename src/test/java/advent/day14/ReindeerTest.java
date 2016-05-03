package advent.day14;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ReindeerTest {

	@Test
	public void comet() {
		Reindeer comet = new Reindeer("Comet can fly 14 km/s for 10 seconds, but then must rest for 127 seconds.");
		assertEquals(1120, comet.distanceTraveled(1000));
	}

	@Test
	public void dancer() {
		Reindeer dancer = new Reindeer("Dancer can fly 16 km/s for 11 seconds, but then must rest for 162 seconds.");
		assertEquals(1056, dancer.distanceTraveled(1000));
	}

}
