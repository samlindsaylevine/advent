package advent.year2016.day6;

import static java.util.Comparator.comparing;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collector.Characteristics;
import java.util.stream.Stream;

public class ErrorCorrection {

	public static Collector<String, ?, String> correctingErrorsByMostCommon() {
		return Collector.of(PartialErrorCorrectionResult::byMostCommon, //
				PartialErrorCorrectionResult::add, //
				PartialErrorCorrectionResult::merge, //
				PartialErrorCorrectionResult::result, //
				Characteristics.UNORDERED);
	}

	public static Collector<String, ?, String> correctingErrorsByLeastCommon() {
		return Collector.of(PartialErrorCorrectionResult::byLeastCommon, //
				PartialErrorCorrectionResult::add, //
				PartialErrorCorrectionResult::merge, //
				PartialErrorCorrectionResult::result, //
				Characteristics.UNORDERED);
	}

	private static class PartialErrorCorrectionResult {

		private List<Map<Character, Integer>> characterFrequencies;

		/**
		 * The order used to sort characters by frequency - the character that
		 * this comparator sorts to the front will be used as the 'correct'
		 * character.
		 */
		private final Comparator<Integer> ascendingFrequencyOrder;

		private PartialErrorCorrectionResult(Comparator<Integer> ascendingFrequencyOrder) {
			this.characterFrequencies = new ArrayList<>();
			this.ascendingFrequencyOrder = ascendingFrequencyOrder;
		}

		public static PartialErrorCorrectionResult byMostCommon() {
			return new PartialErrorCorrectionResult(Comparator.reverseOrder());
		}

		public static PartialErrorCorrectionResult byLeastCommon() {
			return new PartialErrorCorrectionResult(Comparator.naturalOrder());
		}

		public String result() {
			return this.characterFrequencies.stream() //
					.map(this::resultingCharacter) //
					.filter(Optional::isPresent) //
					.map(Optional::get) //
					.collect(StringBuilder::new, StringBuilder::append, StringBuilder::append) //
					.toString();
		}

		public void add(String sample) {
			while (this.characterFrequencies.size() < sample.length()) {
				this.characterFrequencies.add(new HashMap<>());
			}

			for (int i = 0; i < sample.length(); i++) {
				this.characterFrequencies.get(i).merge(sample.charAt(i), 1, Integer::sum);
			}
		}

		public PartialErrorCorrectionResult merge(PartialErrorCorrectionResult other) {
			if (this.characterFrequencies.size() != other.characterFrequencies.size()) {
				throw new IllegalArgumentException("Shouldn't be merging results with different string lengths");
			}

			for (int i = 0; i < this.characterFrequencies.size(); i++) {
				for (Map.Entry<Character, Integer> entry : other.characterFrequencies.get(i).entrySet()) {
					this.characterFrequencies.get(i).merge(entry.getKey(), entry.getValue(), Integer::sum);
				}
			}

			return this;
		}

		private Optional<Character> resultingCharacter(Map<Character, Integer> frequencies) {
			Comparator<Character> resultingCharacterFirst = comparing(frequencies::get, this.ascendingFrequencyOrder);
			return frequencies.keySet().stream() //
					.sorted(resultingCharacterFirst) //
					.findFirst();
		}

	}

	public static void main(String[] args) throws IOException {
		Path inputFilePath = Paths.get("src/main/java/advent/year2016/day6/input.txt");

		try (Stream<String> lines = Files.lines(inputFilePath)) {
			System.out.println(lines.collect(correctingErrorsByMostCommon()));
		}

		try (Stream<String> lines = Files.lines(inputFilePath)) {
			System.out.println(lines.collect(correctingErrorsByLeastCommon()));
		}
	}

}
