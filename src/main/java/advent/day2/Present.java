package advent.day2;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;

class Present {
	private final int length;
	private final int width;
	private final int height;

	/**
	 * Expects "lxwxh"
	 */
	public Present(String representation) {
		List<Integer> dimensions = Arrays.stream(representation.split("x")) //
				.map(Integer::valueOf) //
				.collect(toList());

		if (dimensions.size() != 3) {
			throw new IllegalArgumentException("Wrong number of dimensions");
		}

		this.length = dimensions.get(0);
		this.width = dimensions.get(1);
		this.height = dimensions.get(2);
	}

	private int smallestSideArea() {
		List<Integer> sortedDimensions = Stream.of(this.length, this.width, this.height).sorted().collect(toList());
		return sortedDimensions.get(0) * sortedDimensions.get(1);
	}

	public int wrapperPaperArea() {
		return 2 * this.length * this.width + 2 * this.width * this.height + 2 * this.length * this.height
				+ this.smallestSideArea();
	}

	private int volume() {
		return this.length * this.width * this.height;
	}

	private int smallestSidePerimeter() {
		List<Integer> sortedDimensions = Stream.of(this.length, this.width, this.height).sorted().collect(toList());
		return 2 * (sortedDimensions.get(0) + sortedDimensions.get(1));
	}

	public int ribbonLength() {
		return this.smallestSidePerimeter() + this.volume();
	}

	private static int total(ToIntFunction<Present> presentFunction) throws IOException {
		return Files.lines(Paths.get("src/main/java/advent/day2/presents.txt")) //
				.map(Present::new) //
				.mapToInt(presentFunction) //
				.sum();
	}

	public static void main(String[] args) throws IOException {
		System.out.println(total(Present::ribbonLength));
	}

}