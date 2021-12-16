package advent.year2015.day11;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;

/**
 * --- Day 11: Corporate Policy ---
 * Santa's previous password expired, and he needs help choosing a new one.
 * To help him remember his new password after the old one expires, Santa has devised a method of coming up with a
 * password based on the previous one.  Corporate policy dictates that passwords must be exactly eight lowercase
 * letters (for security reasons), so he finds his new password by incrementing his old password string repeatedly
 * until it is valid.
 * Incrementing is just like counting with numbers: xx, xy, xz, ya, yb, and so on. Increase the rightmost letter one
 * step; if it was z, it wraps around to a, and repeat with the next letter to the left until one doesn't wrap around.
 * Unfortunately for Santa, a new Security-Elf recently started, and he has imposed some additional password
 * requirements:
 * 
 * Passwords must include one increasing straight of at least three letters, like abc, bcd, cde, and so on, up to xyz.
 * They cannot skip letters; abd doesn't count.
 * Passwords may not contain the letters i, o, or l, as these letters can be mistaken for other characters and are
 * therefore confusing.
 * Passwords must contain at least two different, non-overlapping pairs of letters, like aa, bb, or zz.
 * 
 * For example:
 * 
 * hijklmmn meets the first requirement (because it contains the straight hij) but fails the second requirement
 * requirement (because it contains i and l).
 * abbceffg meets the third requirement (because it repeats bb and ff) but fails the first requirement.
 * abbcegjk fails the third requirement, because it only has one double letter (bb).
 * The next password after abcdefgh is abcdffaa.
 * The next password after ghijklmn is ghjaabcc, because you eventually skip all the passwords that start with ghi...,
 * since i is not allowed.
 * 
 * Given Santa's current password (your puzzle input), what should his next password be?
 * 
 * --- Part Two ---
 * Santa's password expired again.  What's the next one?
 * 
 */
public class Password {

	private final String rawString;

	public Password(String rawString) {
		Preconditions.checkArgument(rawString.length() == 8);
		this.rawString = rawString;
	}

	public Password increment() {
		int index = this.rawString.length() - 1;

		while (index >= 0 && this.rawString.charAt(index) == 'z') {
			index--;
		}

		String prefix = this.rawString.substring(0, index);
		String updatedChar = String.valueOf((char) (this.rawString.charAt(index) + 1));
		String remainder = StringUtils.repeat("a", this.rawString.length() - 1 - index);

		return new Password(prefix + updatedChar + remainder);
	}

	public Password incrementToNextValid(Predicate<Password> test) {
		Password nextTry = this.increment();

		while (!test.test(nextTry)) {
			nextTry = nextTry.increment();
		}

		return nextTry;
	}

	@Override
	public String toString() {
		return this.rawString;
	}

	public static boolean securityElfApproves(Password input) {
		return hasIncreasingStraight(input) && !containsIOL(input) && containsTwoPairs(input);
	}

	public static boolean hasIncreasingStraight(Password input) {
		for (int i = 0; i < input.rawString.length() - 3; i++) {
			if (isIncreasingStraight(input.rawString.substring(i, i + 3))) {
				return true;
			}
		}

		return false;
	}

	private static boolean isIncreasingStraight(String input) {
		if (input.length() != 3) {
			return false;
		}

		char one = input.charAt(0);
		char two = input.charAt(1);
		char three = input.charAt(2);

		return (two == one + 1) && (three == two + 1);
	}

	public static boolean containsIOL(Password input) {
		return input.rawString.contains("i") || input.rawString.contains("o") || input.rawString.contains("l");
	}

	public static boolean containsTwoPairs(Password input) {
		Set<Character> pairedCharacters = new HashSet<>();
		for (int i = 0; i < input.rawString.length() - 1; i++) {
			if (input.rawString.charAt(i) == input.rawString.charAt(i + 1)) {
				pairedCharacters.add(input.rawString.charAt(i));
			}
		}

		return pairedCharacters.size() > 1;
	}

	public static void main(String[] args) {
		System.out.println(new Password("hxbxxyzz").incrementToNextValid(Password::securityElfApproves));
	}
}