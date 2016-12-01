package advent.year2015.day5;

import static java.util.stream.Collectors.toSet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

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
		return Files.lines(Paths.get("src/main/java/advent/day5/input.txt")) //
				.filter(OriginalRule::isNice) //
				.count();
	}

	public static long countSecondNice() throws IOException {
		return Files.lines(Paths.get("src/main/java/advent/day5/input.txt")) //
				.filter(SecondRule::isNice) //
				.count();
	}

	public static void main(String[] args) throws IOException {
		System.out.println(countSecondNice());
	}

}
