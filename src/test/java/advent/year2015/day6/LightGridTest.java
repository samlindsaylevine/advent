package advent.year2015.day6;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import advent.year2015.day6.LightGrid.DigitalLight;
import advent.year2015.day6.LightGrid.Instruction;

public class LightGridTest {

	@Test
	public void allOn() {
		LightGrid grid = new LightGrid(DigitalLight::new);
		grid.execute(new Instruction("turn on 0,0 through 999,999"));
		assertEquals(1_000_000, grid.count());
	}

	@Test
	public void toggle() {
		LightGrid grid = new LightGrid(DigitalLight::new);
		grid.execute(new Instruction("turn on 400,0 through 401,0"));
		grid.execute(new Instruction("toggle 0,0 through 999,0"));
		assertEquals(998, grid.count());
	}

	@Test
	public void turnOff() {
		LightGrid grid = new LightGrid(DigitalLight::new);
		grid.execute(new Instruction("turn on 0,0 through 999,999"));
		grid.execute(new Instruction("turn off 499,499 through 500,500"));
		assertEquals(1_000_000 - 4, grid.count());
	}

}
