package advent.year2015.day20;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableSet;

public class ElfDelivererTest {

	@Test
	public void reference() {
		assertEquals(10, ElfDeliverer.presentsDeliveredTo(1));
		assertEquals(30, ElfDeliverer.presentsDeliveredTo(2));
		assertEquals(40, ElfDeliverer.presentsDeliveredTo(3));
		assertEquals(70, ElfDeliverer.presentsDeliveredTo(4));
		assertEquals(60, ElfDeliverer.presentsDeliveredTo(5));
		assertEquals(120, ElfDeliverer.presentsDeliveredTo(6));
		assertEquals(80, ElfDeliverer.presentsDeliveredTo(7));
		assertEquals(150, ElfDeliverer.presentsDeliveredTo(8));
		assertEquals(130, ElfDeliverer.presentsDeliveredTo(9));

		assertEquals(6, ElfDeliverer.firstHouseToGet(100));
	}

	@Test
	public void divisors() {
		assertEquals(ImmutableSet.of(1, 2, 3, 6), ElfDeliverer.divisors(6));
	}

	public static void main(String[] args) {
		System.out.println(ElfDeliverer.presentsDeliveredTo(665280));
	}

}
