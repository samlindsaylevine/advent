package advent.year2016.day10;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.common.collect.ImmutableSet;

public class MicrochipFactoryTest {

	@Test
	public void reference() {
		MicrochipFactory factory = new MicrochipFactory();

		factory.addInstruction("value 5 goes to bot 2");
		factory.addInstruction("bot 2 gives low to bot 1 and high to bot 0");
		factory.addInstruction("value 3 goes to bot 1");
		factory.addInstruction("bot 1 gives low to output 1 and high to bot 0");
		factory.addInstruction("bot 0 gives low to output 2 and high to output 0");
		factory.addInstruction("value 2 goes to bot 2");

		assertEquals(ImmutableSet.of(5), factory.getBin(0).getChips());
		assertEquals(ImmutableSet.of(2), factory.getBin(1).getChips());
		assertEquals(ImmutableSet.of(3), factory.getBin(2).getChips());

		assertEquals(2, factory.findNumberOfBotWhoHandled(5, 2));
	}

}
