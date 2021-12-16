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

/**
 * --- Day 7: Internet Protocol Version 7 ---
 * While snooping around the local network of EBHQ, you compile a list of IP addresses (they're IPv7, of course; IPv6
 * is much too limited). You'd like to figure out which IPs support TLS (transport-layer snooping).
 * An IP supports TLS if it has an Autonomous Bridge Bypass Annotation, or ABBA.  An ABBA is any four-character
 * sequence which consists of a pair of two different characters followed by the reverse of that pair, such as xyyx or
 * abba.  However, the IP also must not have an ABBA within any hypernet sequences, which are contained by square
 * brackets.
 * For example:
 * 
 * abba[mnop]qrst supports TLS (abba outside square brackets).
 * abcd[bddb]xyyx does not support TLS (bddb is within square brackets, even though xyyx is outside square brackets).
 * aaaa[qwer]tyui does not support TLS (aaaa is invalid; the interior characters must be different).
 * ioxxoj[asdfgh]zxcvbn supports TLS (oxxo is outside square brackets, even though it's within a larger string).
 * 
 * How many IPs in your puzzle input support TLS?
 * 
 * --- Part Two ---
 * You would also like to know which IPs support SSL (super-secret listening).
 * An IP supports SSL if it has an Area-Broadcast Accessor, or ABA, anywhere in the supernet sequences (outside any
 * square bracketed sections), and a corresponding Byte Allocation Block, or BAB, anywhere in the hypernet sequences.
 * An ABA is any three-character sequence which consists of the same character twice with a different character between
 * them, such as xyx or aba. A corresponding BAB is the same characters but in reversed positions: yxy and bab,
 * respectively.
 * For example:
 * 
 * aba[bab]xyz supports SSL (aba outside square brackets with corresponding bab within square brackets).
 * xyx[xyx]xyx does not support SSL (xyx, but no corresponding yxy).
 * aaa[kek]eke supports SSL (eke in supernet with corresponding kek in hypernet; the aaa sequence is not related,
 * because the interior character must be different).
 * zazbz[bzb]cdb supports SSL (zaz has no corresponding aza, but zbz has a corresponding bzb, even though zaz and zbz
 * overlap).
 * 
 * How many IPs in your puzzle input support SSL?
 * 
 */
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