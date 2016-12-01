package advent.year2015.day25;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class WeatherMachineCodesTest {

	@Test
	public void indexByRowAndColumn() {
		assertEquals(1, WeatherMachineCodes.index(1, 1));
		assertEquals(16, WeatherMachineCodes.index(6, 1));
		assertEquals(21, WeatherMachineCodes.index(1, 6));
		assertEquals(13, WeatherMachineCodes.index(3, 3));
	}

	@Test
	public void reference() {
		assertEquals(20151125, WeatherMachineCodes.code(1, 1));
		assertEquals(9380097, WeatherMachineCodes.code(4, 4));
		assertEquals(27995004, WeatherMachineCodes.code(6, 6));
	}

}
