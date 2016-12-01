package advent.year2015.day25;

import java.util.stream.Stream;

public class WeatherMachineCodes {

	/**
	 * Returns the index for the code at the provided row and column.
	 *
	 * Note that these are all 1-indexed: the row, column, and the return value.
	 */
	public static int index(int row, int column) {
		// Looking across the top row, we can see the difference between the
		// values looks like 2, then 3, then 4, etc.
		// Likewise in each other row or column. That indicates clearly we are
		// dealing with quadratics in the input value.

		// If we assume a form
		// a * row ^ 2 + b * row + c * column ^ 2 + d * column + e * row *
		// column + f = index
		// and stick in the datapoints

		// (row, column, index)
		// (1, 1, 1)
		// (2, 1, 2)
		// (1, 2, 3)
		// (3, 1, 4)
		// (2, 2, 5)
		// (1, 3, 6)

		// we get the system of linear equations

		// a + b + c + d + e + f = 1
		// 2a + b + c + d + 2e + f = 2
		// a + b + 2c + d + 2e + f = 3
		// 9a + 3b + c + d + 3e + f = 4
		// 4a + 2b + 4c + 2d + 4e + f = 5
		// a + b + 9c + 3c + 3e + f = 6

		// We can define this as a matrix, and then take its reduced row echelon
		// form (using Wolfram Alpha) to find

		// a = 1/2
		// b = -3/2
		// c = 1/2
		// d = -1/2
		// e = 1
		// f = 1

		// We return that here (making sure to save our integer division until
		// the end).
		return (row * row - 3 * row + column * column - column + 2 * row * column + 2) / 2;
	}

	public static Stream<Long> codes() {
		return Stream.iterate(20151125L, i -> i * 252533 % 33554393);
	}

	public static long code(int row, int column) {
		int index = index(row, column);
		return codes().skip(index - 1).findFirst().get();
	}

}
