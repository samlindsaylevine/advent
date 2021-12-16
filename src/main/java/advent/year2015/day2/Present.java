package advent.year2015.day2;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;

/**
 * --- Day 2: I Was Told There Would Be No Math ---
 * The elves are running low on wrapping paper, and so they need to submit an order for more.  They have a list of the
 * dimensions (length l, width w, and height h) of each present, and only want to order exactly as much as they need.
 * Fortunately, every present is a box (a perfect right rectangular prism), which makes calculating the required
 * wrapping paper for each gift a little easier: find the surface area of the box, which is 2*l*w + 2*w*h + 2*h*l.  The
 * elves also need a little extra paper for each present: the area of the smallest side.
 * For example:
 * 
 * A present with dimensions 2x3x4 requires 2*6 + 2*12 + 2*8 = 52 square feet of wrapping paper plus 6 square feet of
 * slack, for a total of 58 square feet.
 * A present with dimensions 1x1x10 requires 2*1 + 2*10 + 2*10 = 42 square feet of wrapping paper plus 1 square foot of
 * slack, for a total of 43 square feet.
 * 
 * All numbers in the elves' list are in feet.  How many total square feet of wrapping paper should they order?
 * 
 * --- Part Two ---
 * The elves are also running low on ribbon.  Ribbon is all the same width, so they only have to worry about the length
 * they need to order, which they would again like to be exact.
 * The ribbon required to wrap a present is the shortest distance around its sides, or the smallest perimeter of any
 * one face.  Each present also requires a bow made out of ribbon as well; the feet of ribbon required for the perfect
 * bow is equal to the cubic feet of volume of the present.  Don't ask how they tie the bow, though; they'll never tell.
 * For example:
 * 
 * A present with dimensions 2x3x4 requires 2+2+3+3 = 10 feet of ribbon to wrap the present plus 2*3*4 = 24 feet of
 * ribbon for the bow, for a total of 34 feet.
 * A present with dimensions 1x1x10 requires 1+1+1+1 = 4 feet of ribbon to wrap the present plus 1*1*10 = 10 feet of
 * ribbon for the bow, for a total of 14 feet.
 * 
 * How many total feet of ribbon should they order?
 * 
 */
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
		return Files.lines(Paths.get("src/main/java/advent/year2015/day2/presents.txt")) //
				.map(Present::new) //
				.mapToInt(presentFunction) //
				.sum();
	}

	public static void main(String[] args) throws IOException {
		System.out.println(total(Present::ribbonLength));
	}

}