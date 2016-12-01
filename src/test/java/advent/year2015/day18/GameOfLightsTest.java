package advent.year2015.day18;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class GameOfLightsTest {

	@Test
	public void reference() {
		String initial = ".#.#.#\n" + //
				"...##.\n" + //
				"#....#\n" + //
				"..#...\n" + //
				"#.#..#\n" + //
				"####..";

		GameOfLights game = new GameOfLights(initial);
		assertEquals(initial, game.toString());

		game = game.step();

		String afterOne = "..##..\n" + //
				"..##.#\n" + //
				"...##.\n" + //
				"......\n" + //
				"#.....\n" + //
				"#.##..";

		assertEquals(afterOne, game.toString());

		game = game.step();

		String afterTwo = "..###.\n" + //
				"......\n" + //
				"..###.\n" + //
				"......\n" + //
				".#....\n" + //
				".#....";

		assertEquals(afterTwo, game.toString());

		game = game.step();

		String afterThree = "...#..\n" + //
				"......\n" + //
				"...#..\n" + //
				"..##..\n" + //
				"......\n" + //
				"......";

		assertEquals(afterThree, game.toString());

		game = game.step();

		String afterFour = "......\n" + //
				"......\n" + //
				"..##..\n" + //
				"..##..\n" + //
				"......\n" + //
				"......";

		assertEquals(afterFour, game.toString());

		assertEquals(4, game.count());
	}

	@Test
	public void stuck() {

	}

}
