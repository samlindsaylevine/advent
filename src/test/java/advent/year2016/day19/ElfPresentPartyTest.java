package advent.year2016.day19;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class ElfPresentPartyTest {

	@Test
	public void reference() {
		ElfPresentParty party = new ElfPresentParty(5);
		assertEquals(3, party.elfThatGetsAllThePresents());
	}

	@Test
	public void nextTrue() {
		List<Boolean> values = ImmutableList.of(true, false, true, false, false);

		assertEquals(2, ElfPresentParty.nextTrueValue(values, 0));
		assertEquals(2, ElfPresentParty.nextTrueValue(values, 1));
		assertEquals(0, ElfPresentParty.nextTrueValue(values, 2));
		assertEquals(0, ElfPresentParty.nextTrueValue(values, 3));
		assertEquals(0, ElfPresentParty.nextTrueValue(values, 4));
	}
}
