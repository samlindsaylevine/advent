package advent.year2016.day7;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class IPv7AddressTest {

	@Test
	public void isAbba() {
		assertTrue(IPv7Address.isAbba("xyyx"));
		assertTrue(IPv7Address.isAbba("abba"));
		assertFalse(IPv7Address.isAbba("aaaa"));

		assertFalse(IPv7Address.isAbba("ioxxoj"));
		assertFalse(IPv7Address.isAbba(""));
		assertFalse(IPv7Address.isAbba("abbaabba"));
	}

	@Test
	public void containsAbba() {
		assertTrue(IPv7Address.containsAbba("abba"));
		assertTrue(IPv7Address.containsAbba("ioxxoj"));
		assertTrue(IPv7Address.containsAbba("rightattheendwoow"));

		assertFalse(IPv7Address.containsAbba("zxcvbn"));
		assertFalse(IPv7Address.containsAbba(""));
	}

	@Test
	public void supportsTLS() {
		assertTrue(new IPv7Address("abba[mnop]qrst").supportsTLS());
		assertFalse(new IPv7Address("abcd[bddb]xyyx").supportsTLS());
		assertFalse(new IPv7Address("aaaa[qwer]tyui").supportsTLS());
		assertTrue(new IPv7Address("ioxxoj[asdfgh]zxcvbn").supportsTLS());
	}

}
