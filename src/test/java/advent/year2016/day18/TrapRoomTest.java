package advent.year2016.day18;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TrapRoomTest {

	@Test
	public void smallReference() {
		TrapRoom room = new TrapRoom("..^^.");

		String expected = "..^^.\n" + //
				".^^^^\n" + //
				"^^..^";

		assertEquals(expected, room.toString(3));
	}

	@Test
	public void largerReference() {
		TrapRoom room = new TrapRoom(".^^.^.^^^^");

		String expected = ".^^.^.^^^^\n" + //
				"^^^...^..^\n" + //
				"^.^^.^.^^.\n" + //
				"..^^...^^^\n" + //
				".^^^^.^^.^\n" + //
				"^^..^.^^..\n" + //
				"^^^^..^^^.\n" + //
				"^..^^^^.^^\n" + //
				".^^^..^.^^\n" + //
				"^^.^^^..^^";

		assertEquals(expected, room.toString(10));

		assertEquals(38, room.safeTiles(10));
	}

}
