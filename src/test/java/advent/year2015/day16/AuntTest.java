package advent.year2015.day16;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableMap;

public class AuntTest {

	@Test
	public void aunt() {
		Aunt sue3 = new Aunt("Sue 3: trees: 6, cars: 6, children: 4");
		assertEquals("Sue 3", sue3.getName());

		assertTrue(sue3.consistentWithValues(ImmutableMap.of("trees", 6, "fish", 72)));
		assertFalse(sue3.consistentWithValues(ImmutableMap.of("cars", 6, "children", 72)));
	}

	@Test
	public void sue260() {
		Aunt sue260 = new Aunt("Sue 260: goldfish: 0, vizslas: 0, samoyeds: 2");
		assertFalse(sue260.consistentWithValues(ImmutableMap.of("goldfish", 5)));
		// assertFalse(sue260.consistentWith(Aunt.mfcsamResult()));
	}
}
