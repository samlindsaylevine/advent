package advent.year2015.day4;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

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
