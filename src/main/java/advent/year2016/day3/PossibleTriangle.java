package advent.year2016.day3;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * --- Day 3: Squares With Three Sides ---
 * Now that you can think clearly, you move deeper into the labyrinth of hallways and office furniture that makes up
 * this part of Easter Bunny HQ. This must be a graphic design department; the walls are covered in specifications for
 * triangles.
 * Or are they?
 * The design document gives the side lengths of each triangle it describes, but... 5 10 25?  Some of these aren't
 * triangles. You can't help but mark the impossible ones.
 * In a valid triangle, the sum of any two sides must be larger than the remaining side.  For example, the "triangle"
 * given above is impossible, because 5 + 10 is not larger than 25.
 * In your puzzle input, how many of the listed triangles are possible?
 * 
 * --- Part Two ---
 * Now that you've helpfully marked up their design documents, it occurs to you that triangles are specified in groups
 * of three vertically.  Each set of three numbers in a column specifies a triangle.  Rows are unrelated.
 * For example, given the following specification, numbers with the same hundreds digit would be part of the same
 * triangle:
 * 101 301 501
 * 102 302 502
 * 103 303 503
 * 201 401 601
 * 202 402 602
 * 203 403 603
 * 
 * In your puzzle input, and instead reading by columns, how many of the listed triangles are possible?
 * 
 */
public class PossibleTriangle {

	final int smallest;
	final int middle;
	final int largest;

	public PossibleTriangle(String stringRep) {
		this(parseLine(stringRep));
	}

	private static List<Integer> parseLine(String line) {
		return Arrays.stream(line.trim().split("\\s+")) //
				.map(Integer::parseInt) //
				.collect(toList());
	}

	private PossibleTriangle(List<Integer> numbers) {
		if (numbers.size() != 3) {
			throw new IllegalArgumentException("Bad input " + numbers);
		}

		numbers = numbers.stream().sorted().collect(toList());

		smallest = numbers.get(0);
		middle = numbers.get(1);
		largest = numbers.get(2);
	}

	public boolean isPossible() {
		return smallest + middle > largest;
	}

	@Override
	public String toString() {
		return "[" + smallest + ", " + middle + ", " + largest + "]";
	}

	/**
	 * Not quite as elegant as reading rows since I require reading the whole
	 * input into memory. Oh well.
	 */
	public static Stream<PossibleTriangle> readingColumns(List<String> lines) {
		return Lists.partition(lines, 3) //
				.stream() //
				.flatMap(PossibleTriangle::readingColumnsFromThreeRows);
	}

	private static Stream<PossibleTriangle> readingColumnsFromThreeRows(List<String> lines) {
		List<List<Integer>> rows = lines.stream() //
				.map(PossibleTriangle::parseLine) //
				.collect(toList());

		// Not handling the case where rows are of different lengths, will fail
		// in some unpleasant way.
		return IntStream.range(0, rows.iterator().next().size()) //
				.mapToObj(i -> ImmutableList.of(rows.get(0).get(i), //
						rows.get(1).get(i), //
						rows.get(2).get(i))) //
				.map(PossibleTriangle::new);
	}

	public static void main(String[] args) throws IOException {
		Path inputFilePath = Paths.get("src/main/java/advent/year2016/day3/input.txt");

		// Reading rows.
		try (Stream<String> lines = Files.lines(inputFilePath)) {
			long possibleCount = lines //
					.map(PossibleTriangle::new) //
					.filter(PossibleTriangle::isPossible) //
					.count();
			System.out.println(possibleCount);
		}

		// Reading columns.
		List<String> lines = Files.readAllLines(inputFilePath);
		long possibleCount = readingColumns(lines) //
				.filter(PossibleTriangle::isPossible) //
				.count();
		System.out.println(possibleCount);
	}

}