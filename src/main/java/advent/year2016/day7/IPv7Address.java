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
import com.google.common.base.Preconditions;

public class IPv7Address {

	private final List<String> supernetSequences;
	private final List<String> hypernetSequences;

	public IPv7Address(String representation) {
		// We'll assume that the brackets are well-balanced and split around
		// them.
		OddsAndEvens<String> groups = Arrays.stream(representation.split("[\\[\\]]")) //
				.collect(toOddsAndEvens());

		this.supernetSequences = groups.getEvens();
		this.hypernetSequences = groups.getOdds();
	}

	public boolean supportsTLS() {
		return this.supernetSequences.stream().anyMatch(IPv7Address::containsAbba)
				&& !this.hypernetSequences.stream().anyMatch(IPv7Address::containsAbba);
	}

	public boolean supportsSSL() {
		return this.supernetSequences.stream() //
				.flatMap(IPv7Address::allAbas) //
				.map(IPv7Address::abaToBab) //
				.anyMatch(this::anyHypernetSequenceContains);
	}

	private boolean anyHypernetSequenceContains(String input) {
		return this.hypernetSequences.stream().anyMatch(seq -> seq.contains(input));
	}

	/**
	 * "An ABBA is any four-character sequence which consists of a pair of two different characters followed by the reverse of that pair, such as xyyx or abba."
	 */
	@VisibleForTesting
	static boolean isAbba(String input) {
		return input.length() == 4 && //
				input.charAt(0) != input.charAt(1) && //
				input.charAt(0) == input.charAt(3) && //
				input.charAt(1) == input.charAt(2);
	}

	@VisibleForTesting
	static boolean containsAbba(String input) {
		return IntStream.range(0, input.length() - 4 + 1) //
				.mapToObj(i -> input.substring(i, i + 4)) //
				.anyMatch(IPv7Address::isAbba);
	}

	private static boolean isAba(String input) {
		return input.length() == 3 && //
				input.charAt(0) != input.charAt(1) && //
				input.charAt(0) == input.charAt(2);
	}

	private static String abaToBab(String aba) {
		Preconditions.checkArgument(isAba(aba));

		return new StringBuilder() //
				.append(aba.charAt(1)) //
				.append(aba.charAt(0)) //
				.append(aba.charAt(1)) //
				.toString();
	}

	private static Stream<String> allAbas(String input) {
		return IntStream.range(0, input.length() - 3 + 1) //
				.mapToObj(i -> input.substring(i, i + 3)) //
				.filter(IPv7Address::isAba);
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

		try (Stream<String> lines = Files.lines(inputFilePath)) {
			long supportsSSLCount = lines //
					.map(IPv7Address::new) //
					.filter(IPv7Address::supportsSSL) //
					.count();
			System.out.println(supportsSSLCount);
		}
	}

}
