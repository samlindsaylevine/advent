package advent.year2016.day14;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.Test;

import advent.year2016.day14.OneTimePadKeyStream.Key;

public class OneTimePadKeyStreamTest {

	@Test
	public void reference() {
		OneTimePadKeyStream stream = new OneTimePadKeyStream("abc");
		List<Key> keys = stream.keys(64);
		assertEquals(39, keys.get(0).index);
		assertEquals(92, keys.get(1).index);
		assertEquals(22728, keys.get(keys.size() - 1).index);
	}

	@Test
	public void firstKey() {
		OneTimePadKeyStream stream = new OneTimePadKeyStream("abc");

		assertTrue(stream.possibleKey(18).contains("cc38887a5"));
		assertEquals(Optional.of('8'), OneTimePadKeyStream.firstRepeatedChar(stream.possibleKey(18), 3));

		assertTrue(stream.possibleKey(39).contains("eee"));
		assertEquals(Optional.of('e'), OneTimePadKeyStream.firstRepeatedChar(stream.possibleKey(39), 3));

		assertTrue(stream.possibleKey(816).contains("eeeee"));
		assertTrue(OneTimePadKeyStream.containsRepeatedChar(stream.possibleKey(816), 'e', 5));

		List<Key> keys = stream.keys(1);
		assertEquals(39, keys.get(0).index);
	}

	@Test
	public void repeats() {
		assertTrue(OneTimePadKeyStream.containsRepeatedChar("cc38887a5", '8', 3));
		assertFalse(OneTimePadKeyStream.containsRepeatedChar("cc38887a5", '8', 4));
		assertFalse(OneTimePadKeyStream.containsRepeatedChar("cc38887a5", 'c', 3));

		assertTrue(OneTimePadKeyStream.containsRepeatedChar("77777", '7', 4));
		assertTrue(OneTimePadKeyStream.containsRepeatedChar("77777", '7', 5));
		assertFalse(OneTimePadKeyStream.containsRepeatedChar("77777", '7', 6));
		assertFalse(OneTimePadKeyStream.containsRepeatedChar("77777", 'q', 2));
	}

}
