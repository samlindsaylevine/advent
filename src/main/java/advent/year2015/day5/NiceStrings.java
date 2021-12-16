package advent.year2015.day5;

import static java.util.stream.Collectors.toSet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

/**
 * --- Day 5: Doesn't He Have Intern-Elves For This? ---
 * Santa needs help figuring out which strings in his text file are naughty or nice.
 * A nice string is one with all of the following properties:
 * 
 * It contains at least three vowels (aeiou only), like aei, xazegov, or aeiouaeiouaeiou.
 * It contains at least one letter that appears twice in a row, like xx, abcdde (dd), or aabbccdd (aa, bb, cc, or dd).
 * It does not contain the strings ab, cd, pq, or xy, even if they are part of one of the other requirements.
 * 
 * For example:
 * 
 * ugknbfddgicrmopn is nice because it has at least three vowels (u...i...o...), a double letter (...dd...), and none
 * of the disallowed substrings.
 * aaa is nice because it has at least three vowels and a double letter, even though the letters used by different
 * rules overlap.
 * jchzalrnumimnmhp is naughty because it has no double letter.
 * haegwjzuvuyypxyu is naughty because it contains the string xy.
 * dvszwmarrgswjxmb is naughty because it contains only one vowel.
 * 
 * How many strings are nice?
 * 
 * --- Part Two ---
 * Realizing the error of his ways, Santa has switched to a better model of determining whether a string is naughty or
 * nice.  None of the old rules apply, as they are all clearly ridiculous.
 * Now, a nice string is one with all of the following properties:
 * 
 * It contains a pair of any two letters that appears at least twice in the string without overlapping, like xyxy (xy)
 * or aabcdefgaa (aa), but not like aaa (aa, but it overlaps).
 * It contains at least one letter which repeats with exactly one letter between them, like xyx, abcdefeghi (efe), or
 * even aaa.
 * 
 * For example:
 * 
 * qjhvhtzxzqqjkmpb is nice because is has a pair that appears twice (qj) and a letter that repeats with exactly one
 * letter between them (zxz).
 * xxyxx is nice because it has a pair that appears twice and a letter that repeats with one between, even though the
 * letters used by each rule overlap.
 * uurcxstgmygtbstg is naughty because it has a pair (tg) but no repeat with a single letter between them.
 * ieodomkazucvgmuy is naughty because it has a repeating letter with one between (odo), but no pair that appears twice.
 * 
 * How many strings are nice under these new rules?
 * 
 */
public class NiceStrings {

	public static class OriginalRule {

		public static boolean isNice(String input) {
			return hasThreeVowels(input) && //
					hasRepeatedLetter(input) && //
					!hasForbiddenStrings(input);
		}

		private static boolean hasThreeVowels(String input) {
			Set<Integer> vowels = ImmutableSet.of('a', 'e', 'i', 'o', 'u') //
					.stream() //
					.map(c -> (int) c) //
					.collect(toSet());

			return input.chars().filter(vowels::contains).count() >= 3;
		}

		private static boolean hasRepeatedLetter(String input) {
			for (int i = 0; i < input.length() - 1; i++) {
				if (input.charAt(i) == input.charAt(i + 1)) {
					return true;
				}
			}

			return false;
		}

		private static boolean hasForbiddenStrings(String input) {
			Set<String> forbiddenStrings = ImmutableSet.of("ab", "cd", "pq", "xy");
			return forbiddenStrings.stream().anyMatch(input::contains);
		}

	}

	public static class SecondRule {
		public static boolean isNice(String input) {
			return hasNonOverlappingPair(input) && hasRepeatWithOneBetween(input);
		}

		public static boolean hasNonOverlappingPair(String input) {
			for (int i = 0; i < input.length() - 3; i++) {
				String possiblePair = input.substring(i, i + 2);

				for (int j = i + 2; j < input.length() - 1; j++) {
					String otherPair = input.substring(j, j + 2);
					if (possiblePair.equals(otherPair)) {
						return true;
					}
				}
			}

			return false;
		}

		public static boolean hasRepeatWithOneBetween(String input) {
			for (int i = 0; i < input.length() - 2; i++) {
				if (input.charAt(i) == input.charAt(i + 2)) {
					return true;
				}
			}

			return false;
		}
	}

	public static long countOriginalNice() throws IOException {
		return Files.lines(Paths.get("src/main/java/advent/year2015/day5/input.txt")) //
				.filter(OriginalRule::isNice) //
				.count();
	}

	public static long countSecondNice() throws IOException {
		return Files.lines(Paths.get("src/main/java/advent/year2015/day5/input.txt")) //
				.filter(SecondRule::isNice) //
				.count();
	}

	public static void main(String[] args) throws IOException {
		System.out.println(countSecondNice());
	}

}