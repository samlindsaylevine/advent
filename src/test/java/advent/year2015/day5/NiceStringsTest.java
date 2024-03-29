package advent.year2015.day5;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import advent.year2015.day5.NiceStrings.OriginalRule;
import advent.year2015.day5.NiceStrings.SecondRule;

public class NiceStringsTest {

	@Test
	public void ugknbfddgicrmopn() {
		assertTrue(OriginalRule.isNice("ugknbfddgicrmopn"));
	}

	@Test
	public void aaa() {
		assertTrue(OriginalRule.isNice("aaa"));
	}

	@Test
	public void jchzalrnumimnmhp() {
		assertFalse(OriginalRule.isNice("jchzalrnumimnmhp"));
	}

	@Test
	public void haegwjzuvuyypxyu() {
		assertFalse(OriginalRule.isNice("haegwjzuvuyypxyu"));
	}

	@Test
	public void dvszwmarrgswjxmb() {
		assertFalse(OriginalRule.isNice("dvszwmarrgswjxmb"));
	}

	@Test
	public void qjhvhtzxzqqjkmpb() {
		assertTrue(SecondRule.isNice("qjhvhtzxzqqjkmpb"));
	}

	@Test
	public void xxyxx() {
		assertTrue(SecondRule.isNice("xxyxx"));
	}

	@Test
	public void uurcxstgmygtbstg() {
		assertFalse(SecondRule.isNice("uurcxstgmygtbstg"));
	}

	@Test
	public void ieodomkazucvgmuy() {
		assertFalse(SecondRule.isNice("ieodomkazucvgmuy"));
	}

	@Test
	public void xyxyPair() {
		assertTrue(SecondRule.hasNonOverlappingPair("xyxy"));
	}

	@Test
	public void aabcdefgaaPair() {
		assertTrue(SecondRule.hasNonOverlappingPair("aabcdefgaa"));
	}

	@Test
	public void aaaPair() {
		assertFalse(SecondRule.hasNonOverlappingPair("aaa"));
	}

}
