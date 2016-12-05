package advent.year2016.day5;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MD5PasswordTest {

	@Test
	public void reference() {
		MD5Password password = new MD5Password("abc");
		assertEquals("18f47a30", password.getOrderedPassword());
	}

	@Test
	public void cinematicReference() {
		MD5Password password = new MD5Password("abc");
		assertEquals("05ace8e3", password.getCinematicPassword());
	}

}
