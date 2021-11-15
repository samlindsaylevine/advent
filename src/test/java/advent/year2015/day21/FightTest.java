package advent.year2015.day21;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

public class FightTest {

	@Test
	public void roundUpDiv() {
		assertEquals(1, Fight.roundUpDivision(4, 5));
		assertEquals(1, Fight.roundUpDivision(5, 5));
		assertEquals(2, Fight.roundUpDivision(6, 5));
	}

	@Test
	public void fight() {
		Character hero = new Character(8, 5, 5);
		Character boss = new Character(12, 2, 7);
		Fight fight = new Fight(hero, boss);
		assertSame(hero, fight.winner());
	}

	@Test
	public void highHp() {
		Character hero = new Character(100, 2, 7);
		Character boss = new Character(100, 2, 8);
		Fight fight = new Fight(hero, boss);
		assertSame(boss, fight.winner());
	}

}
