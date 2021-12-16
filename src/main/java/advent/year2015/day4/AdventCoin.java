package advent.year2015.day4;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

/**
 * --- Day 4: The Ideal Stocking Stuffer ---
 * Santa needs help mining some AdventCoins (very similar to bitcoins) to use as gifts for all the economically
 * forward-thinking little girls and boys.
 * To do this, he needs to find MD5 hashes which, in hexadecimal, start with at least five zeroes.  The input to the
 * MD5 hash is some secret key (your puzzle input, given below) followed by a number in decimal. To mine AdventCoins,
 * you must find Santa the lowest positive number (no leading zeroes: 1, 2, 3, ...) that produces such a hash.
 * For example:
 * 
 * If your secret key is abcdef, the answer is 609043, because the MD5 hash of abcdef609043 starts with five zeroes
 * (000001dbbfa...), and it is the lowest such number to do so.
 * If your secret key is pqrstuv, the lowest number it combines with to make an MD5 hash starting with five zeroes is
 * 1048970; that is, the MD5 hash of pqrstuv1048970 looks like 000006136ef....
 * 
 * 
 * --- Part Two ---
 * Now find one that starts with six zeroes.
 * 
 */
public class AdventCoin {

	private final long number;

	public AdventCoin(String secretKey) {
		this(secretKey, 5);
	}

	public AdventCoin(String secretKey, int numZeroes) {
		this.number = smallestValidNumber(secretKey, numZeroes);
	}

	public long getNumber() {
		return this.number;
	}

	private static long smallestValidNumber(String secretKey, int leadingZeroesRequired) {
		String leadingStringRequired = StringUtils.repeat("0", leadingZeroesRequired);

		MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			// Never actually happens.
			throw new RuntimeException(e);
		}

		for (long i = 0;; i++) {
			String input = secretKey + i;

			md5.reset();
			md5.update(input.getBytes());
			byte[] hash = md5.digest();

			String hexString = Hex.encodeHexString(hash);

			if (hexString.startsWith(leadingStringRequired)) {
				return i;
			}
		}
	}

	public static void main(String[] args) {
		AdventCoin coin = new AdventCoin("bgvyzdsv", 6);
		System.out.println(coin.getNumber());
	}

}