package advent.year2015.day4;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.security.NoSuchAlgorithmException;

import org.junit.jupiter.api.Test;

import advent.year2015.day4.AdventCoin;

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
