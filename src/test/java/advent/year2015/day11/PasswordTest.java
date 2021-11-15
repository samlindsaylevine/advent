package advent.year2015.day11;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class PasswordTest {

	@Test
	public void hijklmmn() {
		Password hijklmmn = new Password("hijklmmn");
		assertTrue(Password.hasIncreasingStraight(hijklmmn));
		assertTrue(Password.containsIOL(hijklmmn));
	}

	@Test
	public void abbceffg() {
		Password abbceffg = new Password("abbceffg");
		assertFalse(Password.hasIncreasingStraight(abbceffg));
		assertTrue(Password.containsTwoPairs(abbceffg));
	}

	@Test
	public void abbcegjk() {
		Password abbcegjk = new Password("abbcegjk");
		assertFalse(Password.containsTwoPairs(abbcegjk));
	}

	@Test
	public void nextAfterAbcdefgh() {
		Password abcdefgh = new Password("abcdefgh");
		Password next = abcdefgh.incrementToNextValid(Password::securityElfApproves);
		assertEquals("abcdffaa", next.toString());
	}

	@Test
	public void nextAfterGhijklmn() {
		Password ghijklmn = new Password("ghijklmn");
		Password next = ghijklmn.incrementToNextValid(Password::securityElfApproves);
		assertEquals("ghjaabcc", next.toString());
	}

}
