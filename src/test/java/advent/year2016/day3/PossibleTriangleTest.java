package advent.year2016.day3;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class PossibleTriangleTest {

	@Test
	public void possible() {
		assertTrue(new PossibleTriangle("3 4 5").isPossible());
		assertTrue(new PossibleTriangle("5 4 3").isPossible());
		assertTrue(new PossibleTriangle("4 3 5").isPossible());
	}

	@Test
	public void referenceImpossible() {
		assertFalse(new PossibleTriangle("5 10 25").isPossible());
		assertFalse(new PossibleTriangle("25 10 5").isPossible());
	}

	@Test
	public void referenceReadingColumns() {
		List<String> referenceInput = ImmutableList.of( //
				"101 301 501", //
				"102 302 502", //
				"103 303 503", //
				"201 401 601", //
				"202 402 602", //
				"203 403 603");

		List<PossibleTriangle> triangles = PossibleTriangle.readingColumns(referenceInput) //
				.collect(toList());

		assertEquals(6, triangles.size());
		PossibleTriangle first = triangles.iterator().next();
		assertEquals(101, first.smallest);
		assertEquals(102, first.middle);
		assertEquals(103, first.largest);

	}

}
