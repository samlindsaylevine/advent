package advent.year2016.day4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RoomTest {

	@Test
	public void aaaaabzyx123() {
		Room room = new Room("aaaaa-bbb-z-y-x-123[abxyz]");
		assertTrue(room.isReal());
	}

	@Test
	public void abcdefgh987() {
		Room room = new Room("a-b-c-d-e-f-g-h-987[abcde]");
		assertTrue(room.isReal());
	}

	@Test
	public void notARealRoom() {
		Room room = new Room("not-a-real-room-404[oarel]");
		assertTrue(room.isReal());
	}

	@Test
	public void totallyRealRoom() {
		Room room = new Room("totally-real-room-200[decoy]");
		assertFalse(room.isReal());
	}

	@Test
	public void decryptedName() {
		Room room = new Room("qzmt-zixmtkozy-ivhz-343[abcde]");
		assertEquals("very encrypted name", room.decryptedName());
	}
}
