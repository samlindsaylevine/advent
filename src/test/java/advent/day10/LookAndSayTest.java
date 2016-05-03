package advent.day10;

import static advent.day10.LookAndSay.lookAndSay;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LookAndSayTest {

	@Test
	public void one() {
		assertEquals("11", lookAndSay("1"));
	}

	@Test
	public void oneOne() {
		assertEquals("21", lookAndSay("11"));
	}

	@Test
	public void twoOne() {
		assertEquals("1211", lookAndSay("21"));
	}

	@Test
	public void oneTwoOneOne() {
		assertEquals("111221", lookAndSay("1211"));
	}

	@Test
	public void oneoneoneTwoTwoOne() {
		assertEquals("312211", lookAndSay("111221"));
	}

}
