package advent.year2015.day13;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class SeatingHappinessTest {

	@Test
	public void reference() {
		SeatingHappiness seating = new SeatingHappiness();

		seating.addPreference("Alice would gain 54 happiness units by sitting next to Bob.");
		seating.addPreference("Alice would lose 79 happiness units by sitting next to Carol.");
		seating.addPreference("Alice would lose 2 happiness units by sitting next to David.");
		seating.addPreference("Bob would gain 83 happiness units by sitting next to Alice.");
		seating.addPreference("Bob would lose 7 happiness units by sitting next to Carol.");
		seating.addPreference("Bob would lose 63 happiness units by sitting next to David.");
		seating.addPreference("Carol would lose 62 happiness units by sitting next to Alice.");
		seating.addPreference("Carol would gain 60 happiness units by sitting next to Bob.");
		seating.addPreference("Carol would gain 55 happiness units by sitting next to David.");
		seating.addPreference("David would gain 46 happiness units by sitting next to Alice.");
		seating.addPreference("David would lose 7 happiness units by sitting next to Bob.");
		seating.addPreference("David would gain 41 happiness units by sitting next to Carol.");

		assertEquals(330, seating.optimalHappiness());
	}
}
