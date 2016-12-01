package advent.year2015.day19;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class ReplacementTest {

	@Test
	public void reference() {
		List<Replacement> replacements = ImmutableList.of(new Replacement("H => HO"), //
				new Replacement("H => OH"), //
				new Replacement("O => HH"));

		Molecule initial = new Molecule("HOH");

		Set<Molecule> expected = ImmutableSet.of(new Molecule("HOOH"), //
				new Molecule("HOHO"), //
				new Molecule("OHOH"), //
				new Molecule("HOOH"), //
				new Molecule("HHHH"));

		assertEquals(expected, Replacement.allGeneratable(initial, replacements));
	}

	@Test
	public void stepsToCreate() {
		List<Replacement> replacements = ImmutableList.of(new Replacement("e => H"), //
				new Replacement("e => O"), //
				new Replacement("H => HO"), //
				new Replacement("H => OH"), //
				new Replacement("O => HH"));

		assertEquals(3, Replacement.stepsToCreate(new Molecule("HOH"), replacements));
		assertEquals(6, Replacement.stepsToCreate(new Molecule("HOHOHO"), replacements));
	}

}
