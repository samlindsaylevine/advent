package advent.day17;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class EggnogContainers {
	public static List<Integer> containerSizes() throws IOException {
		return Files.lines(Paths.get("src/main/java/advent/day17/input.txt")) //
				.map(Integer::valueOf) //
				.collect(toList());
	}

	public static int countStorageSolutions(int amount, List<Integer> containers) {
		if (amount == 0) {
			return 1;
		}

		if (containers.isEmpty()) {
			return 0;
		}

		int first = containers.get(0);
		List<Integer> rest = containers.subList(1, containers.size());

		int countUsingFirst = first > amount ? 0 : countStorageSolutions(amount - first, rest);
		int countNotUsingFirst = countStorageSolutions(amount, rest);

		return countUsingFirst + countNotUsingFirst;
	}

	public static int countStorageSolutions(int amount, List<Integer> containers, int containerCount) {
		if (amount == 0 && containerCount == 0) {
			return 1;
		}

		if (containerCount < 0) {
			return 0;
		}

		if (containers.isEmpty()) {
			return 0;
		}

		int first = containers.get(0);
		List<Integer> rest = containers.subList(1, containers.size());

		int countUsingFirst = first > amount ? 0 : countStorageSolutions(amount - first, rest, containerCount - 1);
		int countNotUsingFirst = countStorageSolutions(amount, rest, containerCount);

		return countUsingFirst + countNotUsingFirst;
	}

	public static void main(String[] args) throws IOException {
		for (int i = 0;; i++) {
			int countWithI = countStorageSolutions(150, containerSizes(), i);
			if (countWithI > 0) {
				System.out.println(countWithI);
				return;
			}
		}
	}
}
