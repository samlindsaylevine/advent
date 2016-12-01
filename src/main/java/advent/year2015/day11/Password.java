package advent.year2015.day11;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;

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
