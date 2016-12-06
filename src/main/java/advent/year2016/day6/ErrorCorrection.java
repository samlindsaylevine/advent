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

	public static Collector<String, ?, String> correctingErrors() {
		return Collector.of(PartialErrorCorrectionResult::new, //
				PartialErrorCorrectionResult::add, //
				PartialErrorCorrectionResult::merge, PartialErrorCorrectionResult::result, //
				Characteristics.UNORDERED);
	}

	private static class PartialErrorCorrectionResult {

		private List<Map<Character, Integer>> characterFrequencies;

		public PartialErrorCorrectionResult() {
			this.characterFrequencies = new ArrayList<>();
		}

		public String result() {
			return this.characterFrequencies.stream() //
					.map(PartialErrorCorrectionResult::mostFrequentCharacter) //
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

		private static Optional<Character> mostFrequentCharacter(Map<Character, Integer> frequencies) {
			Comparator<Character> mostFrequentLast = comparing(frequencies::get);
			return frequencies.keySet().stream() //
					.sorted(mostFrequentLast.reversed()) //
					.findFirst();
		}

	}

	public static void main(String[] args) throws IOException {
		Path inputFilePath = Paths.get("src/main/java/advent/year2016/day6/input.txt");

		try (Stream<String> lines = Files.lines(inputFilePath)) {
			System.out.println(lines.collect(correctingErrors()));
		}
	}

}
