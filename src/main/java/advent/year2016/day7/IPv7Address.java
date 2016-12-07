package advent.year2016.day7;

import static advent.year2016.day7.OddsAndEvens.toOddsAndEvens;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.google.common.annotations.VisibleForTesting;

public class IPv7Address {

	private final String representation;

	public IPv7Address(String representation) {
		this.representation = representation;
	}

	public boolean supportsTLS() {
		// We'll assume that the brackets are well-balanced and split around
		// them.
		OddsAndEvens<String> groups = Arrays.stream(this.representation.split("[\\[\\]]")) //
				.collect(toOddsAndEvens());

		List<String> supernetSequences = groups.getEvens();
		List<String> hypernetSequences = groups.getOdds();

		return supernetSequences.stream().anyMatch(IPv7Address::containsAbba)
				&& !hypernetSequences.stream().anyMatch(IPv7Address::containsAbba);
	}

	/**
	 * "An ABBA is any four-character sequence which consists of a pair of two different characters followed by the reverse of that pair, such as xyyx or abba."
	 */
	@VisibleForTesting
	static boolean isAbba(String input) {
		if (input.length() != 4) {
			return false;
		}

		if (input.charAt(0) == input.charAt(1)) {
			return false;
		}

		return input.charAt(0) == input.charAt(3) && input.charAt(1) == input.charAt(2);
	}

	@VisibleForTesting
	static boolean containsAbba(String input) {
		return IntStream.range(0, input.length() - 4 + 1) //
				.mapToObj(i -> input.substring(i, i + 4)) //
				.anyMatch(IPv7Address::isAbba);
	}

	public static void main(String[] args) throws IOException {
		Path inputFilePath = Paths.get("src/main/java/advent/year2016/day7/input.txt");

		try (Stream<String> lines = Files.lines(inputFilePath)) {
			long supportsTLSCount = lines //
					.map(IPv7Address::new) //
					.filter(IPv7Address::supportsTLS) //
					.count();
			System.out.println(supportsTLSCount);
		}
	}

}
