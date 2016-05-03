package advent.day8;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SantaStringTest {

	@Test
	public void replace() {
		assertEquals("aaa\"aaa", "aaa\\\"aaa".replace("\\\"", "\""));
	}

	@Test
	public void empty() {
		SantaString empty = new SantaString("\"\"");
		assertEquals(2, empty.rawLength());
		assertEquals(0, empty.decodedLength());
		assertEquals(6, empty.encodedLength());
	}

	@Test
	public void abc() {
		SantaString abc = new SantaString("\"abc\"");
		assertEquals(5, abc.rawLength());
		assertEquals(3, abc.decodedLength());
		assertEquals(9, abc.encodedLength());
	}

	@Test
	public void aaa() {
		SantaString aaa = new SantaString("\"aaa\\\"aaa\"");
		assertEquals(10, aaa.rawLength());
		assertEquals(7, aaa.decodedLength());
		assertEquals(16, aaa.encodedLength());
	}

	@Test
	public void x27() {
		SantaString x27 = new SantaString("\"\\x27\"");
		assertEquals(6, x27.rawLength());
		assertEquals(1, x27.decodedLength());
		assertEquals(11, x27.encodedLength());
	}

}
