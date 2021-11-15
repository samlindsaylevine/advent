package advent.year2015.day22;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class WizardFightTest {

	@Test
	public void reference() {
		WizardFight state = new WizardFight(10, 0, 13, 8, 250, false);

		state = state.cast(Spell.POISON);

		assertEquals(2, state.getHeroHp());
		assertEquals(77, state.getHeroMana());
		assertEquals(7, state.getBossHp());

		state = state.cast(Spell.MAGIC_MISSILE);

		assertTrue(state.isOver());
		assertTrue(state.heroWon());

		assertEquals(2, state.getHeroHp());
		assertEquals(0, state.getBossHp());
	}

	@Test
	public void referenceTwo() {
		WizardFight state = new WizardFight(10, 0, 14, 8, 250, false);

		assertTrue(Spell.POISON.isCastable(state));

		System.out.println(WizardFight.allWinningFights(state));

		state = state.cast(Spell.RECHARGE);

		assertEquals(2, state.getHeroHp());
		assertEquals(122 + 101, state.getHeroMana());
		assertEquals(14, state.getBossHp());

		state = state.cast(Spell.SHIELD);

		assertEquals(1, state.getHeroHp());
		assertEquals(211 + 101, state.getHeroMana());
		assertEquals(14, state.getBossHp());
		assertTrue(state.isActive(Effect.SHIELD));
		assertFalse(Spell.SHIELD.isCastable(state));

		state = state.cast(Spell.DRAIN);

		assertEquals(2, state.getHeroHp());
		assertEquals(340, state.getHeroMana());
		assertEquals(12, state.getBossHp());
		assertTrue(state.isActive(Effect.SHIELD));
		assertFalse(Spell.SHIELD.isCastable(state));

		state = state.cast(Spell.POISON);

		assertEquals(1, state.getHeroHp());
		assertEquals(167, state.getHeroMana());
		assertEquals(6, state.getBossHp());
		assertTrue(state.isActive(Effect.POISON));
		assertFalse(Spell.POISON.isCastable(state));
		assertTrue(Spell.SHIELD.isCastable(state));

		state = state.cast(Spell.MAGIC_MISSILE);

		assertFalse(Spell.POISON.isCastable(state));

		assertTrue(state.isOver());
		assertTrue(state.heroWon());

		assertEquals(1, state.getHeroHp());
		assertEquals(-1, state.getBossHp());
	}

}
