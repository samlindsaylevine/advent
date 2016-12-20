package advent.year2016.day20;

import static org.junit.Assert.assertEquals;

import java.util.Optional;
import java.util.stream.Stream;

import org.junit.Test;

public class IPRulesTest {

	@Test
	public void reference() {
		int maxValue = 9;
		Stream<String> blacklist = Stream.of( //
				"5-8", //
				"0-2", //
				"4-7");

		IPRules rules = new IPRules(maxValue, blacklist);
		assertEquals(Optional.of(3L), rules.lowestValid());
	}

}
