package advent.day4;

import static org.junit.Assert.assertEquals;

import java.security.NoSuchAlgorithmException;

import org.junit.Test;

public class AdventCoinTest {

	@Test
	public void abcdef() throws NoSuchAlgorithmException {
		AdventCoin coin = new AdventCoin("abcdef");
		assertEquals(609043L, coin.getNumber());
	}

	@Test
	public void pqrstuv() throws NoSuchAlgorithmException {
		AdventCoin coin = new AdventCoin("pqrstuv");
		assertEquals(1048970L, coin.getNumber());
	}

}
