package advent.day20;

import static java.util.stream.Collectors.toSet;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

public class ElfDeliverer {

	private final int number;

	public ElfDeliverer(int number) {
		this.number = number;
	}

	public int presentsPerHouse() {
		// In part II this is 11 * this.number.
		// I was too lazy to make this code support both halves of the problem
		// at once.
		return 10 * this.number;
	}

	public static Set<ElfDeliverer> deliveringTo(int houseNumber) {
		return divisors(houseNumber).stream() //
				// In part II add this filter.
				// .filter(i -> 50 * i >= houseNumber)
				.map(ElfDeliverer::new) //
				.collect(toSet());
	}

	static Set<Integer> divisors(int input) {
		Set<Integer> output = new HashSet<>();

		for (int i = 1; i * 2 <= input; i++) {
			if (input % i == 0) {
				output.add(i);
			}
		}

		output.add(input);
		return output;
	}

	public static int presentsDeliveredTo(int houseNumber) {
		if (houseNumber % 1000 == 0) {
			System.out.println(houseNumber);
		}
		return deliveringTo(houseNumber).stream().mapToInt(ElfDeliverer::presentsPerHouse).sum();
	}

	public static int firstHouseToGet(int presents) {
		return firstHouseToGet(presents, 1);
	}

	public static int firstHouseToGet(int presents, int startingWith) {
		return IntStream.iterate(startingWith, i -> i + 1) //
				.parallel() //
				.filter(i -> presentsDeliveredTo(i) >= presents) //
				.findFirst() //
				.getAsInt();
	}

	public static void main(String[] args) {
		System.out.println(firstHouseToGet(29000000, 600000));
	}
}
