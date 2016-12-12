package advent.year2016.day11;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.stream.Stream;

import org.junit.Test;

import com.google.common.collect.ImmutableSet;

import advent.year2016.day11.RadioisotopeTestingFacility.Floor;
import advent.year2016.day11.RadioisotopeTestingFacility.Generator;
import advent.year2016.day11.RadioisotopeTestingFacility.Microchip;

public class RadioisotopeTestingFacilityTest {

	private RadioisotopeTestingFacility reference() {
		Stream<String> input = Stream.of(
				"The first floor contains a hydrogen-compatible microchip and a lithium-compatible microchip.", //
				"The second floor contains a hydrogen generator.", //
				"The third floor contains a lithium generator.", //
				"The fourth floor contains nothing relevant.");

		RadioisotopeTestingFacility facility = new RadioisotopeTestingFacility(input);

		return facility;
	}

	@Test
	public void referenceTest() {
		RadioisotopeTestingFacility facility = this.reference();

		Floor expectedFirst = new Floor(ImmutableSet.of(new Microchip("hydrogen"), new Microchip("lithium")),
				ImmutableSet.of());
		Floor expectedSecond = new Floor(ImmutableSet.of(), ImmutableSet.of(new Generator("hydrogen")));
		Floor expectedThird = new Floor(ImmutableSet.of(), ImmutableSet.of(new Generator("lithium")));
		Floor expectedFourth = new Floor(ImmutableSet.of(), ImmutableSet.of());

		assertEquals(expectedFirst, facility.getFloor(0));
		assertEquals(expectedSecond, facility.getFloor(1));
		assertEquals(expectedThird, facility.getFloor(2));
		assertEquals(expectedFourth, facility.getFloor(3));

		System.out.println(facility.legalNextMoves().map(Object::toString).collect(joining("\n")));

		assertEquals(11, facility.minimumNumberOfStepsToGetEverythingOntoTheTopFloor());
	}

	@Test
	public void havingMoved() {
		RadioisotopeTestingFacility stageOne = this.reference().moving(ImmutableSet.of(new Microchip("hydrogen")), 1);
		assertTrue(stageOne.isLegalState());

		assertEquals(1, stageOne.getFloor(0).microchips.size());
		assertEquals(1, stageOne.getFloor(1).microchips.size());
	}

	@Test
	public void noGeneratorsLegal() {
		Floor noGenerators = new Floor(ImmutableSet.of(new Microchip("yttrium")), ImmutableSet.of());
		assertTrue(noGenerators.isLegalState());
	}

	@Test
	public void matchingGeneratorLegal() {
		Floor matchingGenerator = new Floor(ImmutableSet.of(new Microchip("yttrium")),
				ImmutableSet.of(new Generator("yttrium")));
		assertTrue(matchingGenerator.isLegalState());
	}

	@Test
	public void wrongGeneratorIllegal() {
		Floor nonMatchingGenerator = new Floor(ImmutableSet.of(new Microchip("yttrium")),
				ImmutableSet.of(new Generator("hafnium")));
		assertFalse(nonMatchingGenerator.isLegalState());
	}

	@Test
	public void allMatchingGeneratorsLegal() {
		Floor matchingGenerator = new Floor(ImmutableSet.of(new Microchip("yttrium"), new Microchip("helium")), //
				ImmutableSet.of(new Generator("yttrium"), new Generator("helium")));
		assertTrue(matchingGenerator.isLegalState());
	}

	@Test
	public void someMatchingGeneratorsIllegal() {
		Floor matchingGenerator = new Floor(ImmutableSet.of(new Microchip("yttrium"), new Microchip("helium")), //
				ImmutableSet.of(new Generator("yttrium")));
		assertFalse(matchingGenerator.isLegalState());
	}

	@Test
	public void commaSeparatedAnd() {
		Floor floor = new Floor(
				"The first floor contains a thulium generator, a thulium-compatible microchip, a plutonium generator, and a strontium generator.");

		Floor expected = new Floor( //
				ImmutableSet.of(new Microchip("thulium")), //
				Stream.of("thulium", "plutonium", "strontium").map(Generator::new).collect(toSet()));

		assertEquals(expected, floor);
	}

	@Test
	public void canonicalize() {
		// Same as the reference input but with the generators swapped.
		Stream<String> input = Stream.of(
				"The first floor contains a hydrogen-compatible microchip and a lithium-compatible microchip.", //
				"The second floor contains a lithium generator.", //
				"The third floor contains a hydrogen generator.", //
				"The fourth floor contains nothing relevant.");

		RadioisotopeTestingFacility facility = new RadioisotopeTestingFacility(input);

		assertEquals(this.reference(), facility.canonicalize());
	}

}
