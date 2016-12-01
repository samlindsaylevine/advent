package advent.year2015.day15;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class CookieTest {

	@Test
	public void reference() {
		Ingredient butterscotch = Ingredient
				.fromString("Butterscotch: capacity -1, durability -2, flavor 6, texture 3, calories 8");
		Ingredient cinnamon = Ingredient
				.fromString("Cinnamon: capacity 2, durability 3, flavor -2, texture -1, calories 3");

		List<Ingredient> ingredients = ImmutableList.of(butterscotch, cinnamon);
		int optimumScore = Cookie.optimumScore(ingredients);
		assertEquals(62842880, optimumScore);

		int calorieConstrainedScore = Cookie.optimumScore(ingredients, 500);
		assertEquals(57600000, calorieConstrainedScore);
	}

}
