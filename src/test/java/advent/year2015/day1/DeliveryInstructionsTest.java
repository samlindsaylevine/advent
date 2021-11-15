package advent.year2015.day1;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import advent.year2015.day1.DeliveryInstructions;

public class DeliveryInstructionsTest {

	@Test
	public void finalFloor() {
		assertEquals(0, new DeliveryInstructions("(())").finalFloor());
		assertEquals(3, new DeliveryInstructions("(((").finalFloor());
		assertEquals(3, new DeliveryInstructions("(()(()(").finalFloor());
		assertEquals(3, new DeliveryInstructions("))(((((").finalFloor());
		assertEquals(-1, new DeliveryInstructions("())").finalFloor());
		assertEquals(-1, new DeliveryInstructions("))(").finalFloor());
		assertEquals(-3, new DeliveryInstructions(")))").finalFloor());
		assertEquals(-3, new DeliveryInstructions(")())())").finalFloor());
	}

	@Test
	public void enterTheBasementStep() {
		assertEquals(Optional.of(1), new DeliveryInstructions(")").enterTheBasementStep());
		assertEquals(Optional.of(5), new DeliveryInstructions("()())").enterTheBasementStep());
	}

}
