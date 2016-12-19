package advent.year2016.day19;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import advent.year2016.day19.ElfPresentParty.PartyElf;

public class ElfPresentPartyTest {

	@Test
	public void reference() {
		ElfPresentParty party = ElfPresentParty.stealingFromTheLeft(5);
		assertEquals(3, party.elfThatGetsAllThePresents());
	}

	@Test
	public void referenceAcross() {
		ElfPresentParty party = ElfPresentParty.stealingAcross(5);
		assertEquals(2, party.elfThatGetsAllThePresents());
	}

	@Test
	public void linkedElves() {
		PartyElf first = ElfPresentParty.linkedElves(5);
		assertEquals(1, first.number);
		assertEquals(2, first.next().number);
		assertEquals(3, first.next().next().number);
		assertEquals(4, first.next().next().next().number);
		assertEquals(5, first.next().next().next().next().number);
		assertEquals(1, first.next().next().next().next().next().number);

		assertEquals(5, first.previous().number);
		assertEquals(4, first.previous().previous().number);

		assertEquals(1, first.next().previous().number);
	}
}
