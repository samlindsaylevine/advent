package advent.year2016.day1;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import advent.year2016.day1.StreetGridWalk.Location;

public class StreetGridWalkTest {

	@Test
	public void r2l3() {
		assertEquals(5, StreetGridWalk.of("R2, L3").finalLocation().distanceFromStart());
	}

	@Test
	public void r2r2r2() {
		assertEquals(2, StreetGridWalk.of("R2, R2, R2").finalLocation().distanceFromStart());
	}

	@Test
	public void r5l5r5r3() {
		assertEquals(12, StreetGridWalk.of("R5, L5, R5, R3").finalLocation().distanceFromStart());
	}

	@Test
	public void firstVisitedTwice() {
		StreetGridWalk walk = StreetGridWalk.of("R8, R4, R4, R8");
		Location firstVisited = walk.firstLocationVisitedTwice().get();
		assertEquals(4, firstVisited.getX());
		assertEquals(0, firstVisited.getY());
		assertEquals(4, firstVisited.distanceFromStart());
	}

}
