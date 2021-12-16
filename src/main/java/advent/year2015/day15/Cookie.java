package advent.year2015.day15;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.google.common.base.Preconditions;

/**
 * --- Day 15: Science for Hungry People ---
 * Today, you set out on the task of perfecting your milk-dunking cookie recipe.  All you have to do is find the right
 * balance of ingredients.
 * Your recipe leaves room for exactly 100 teaspoons of ingredients.  You make a list of the remaining ingredients you
 * could use to finish the recipe (your puzzle input) and their properties per teaspoon:
 * 
 * capacity (how well it helps the cookie absorb milk)
 * durability (how well it keeps the cookie intact when full of milk)
 * flavor (how tasty it makes the cookie)
 * texture (how it improves the feel of the cookie)
 * calories (how many calories it adds to the cookie)
 * 
 * You can only measure ingredients in whole-teaspoon amounts accurately, and you have to be accurate so you can
 * reproduce your results in the future.  The total score of a cookie can be found by adding up each of the properties
 * (negative totals become 0) and then multiplying together everything except calories.
 * For instance, suppose you have these two ingredients:
 * Butterscotch: capacity -1, durability -2, flavor 6, texture 3, calories 8
 * Cinnamon: capacity 2, durability 3, flavor -2, texture -1, calories 3
 * 
 * Then, choosing to use 44 teaspoons of butterscotch and 56 teaspoons of cinnamon (because the amounts of each
 * ingredient must add up to 100) would result in a cookie with the following properties:
 * 
 * A capacity of 44*-1 + 56*2 = 68
 * A durability of 44*-2 + 56*3 = 80
 * A flavor of 44*6 + 56*-2 = 152
 * A texture of 44*3 + 56*-1 = 76
 * 
 * Multiplying these together (68 * 80 * 152 * 76, ignoring calories for now) results in a total score of  62842880,
 * which happens to be the best score possible given these ingredients.  If any properties had produced a negative
 * total, it would have instead become zero, causing the whole score to multiply to zero.
 * Given the ingredients in your kitchen and their properties, what is the total score of the highest-scoring cookie
 * you can make?
 * 
 * --- Part Two ---
 * Your cookie recipe becomes wildly popular!  Someone asks if you can make another recipe that has exactly 500
 * calories per cookie (so they can use it as a meal replacement).  Keep the rest of your award-winning process the
 * same (100 teaspoons, same ingredients, same scoring system).
 * For example, given the ingredients above, if you had instead selected 40 teaspoons of butterscotch and 60 teaspoons
 * of cinnamon (which still adds to 100), the total calorie count would be 40*8 + 60*3 = 500.  The total score would go
 * down, though: only 57600000, the best you can do in such trying circumstances.
 * Given the ingredients in your kitchen and their properties, what is the total score of the highest-scoring cookie
 * you can make with a calorie total of 500?
 * 
 */
public class Cookie {

	private static final int VOLUME = 100;

	private Map<Ingredient, Integer> recipe;

	public Cookie(Map<Ingredient, Integer> recipe) {
		int totalVolume = recipe.values().stream().mapToInt(i -> i).sum();
		Preconditions.checkArgument(totalVolume == VOLUME);

		this.recipe = recipe;
	}

	public int capacity() {
		return this.total(Ingredient::getCapacity);
	}

	public int durability() {
		return this.total(Ingredient::getDurability);
	}

	public int flavor() {
		return this.total(Ingredient::getFlavor);
	}

	public int texture() {
		return this.total(Ingredient::getTexture);
	}

	public int calories() {
		return this.total(Ingredient::getCalories);
	}

	public int score() {
		return this.capacity() * this.durability() * this.flavor() * this.texture();
	}

	private int total(Function<Ingredient, Integer> attribute) {
		int total = this.recipe.entrySet() //
				.stream() //
				.mapToInt(entry -> entry.getValue() * attribute.apply(entry.getKey())) //
				.sum();

		return total > 0 ? total : 0;
	}

	private static Stream<Cookie> possibleCookies(List<Ingredient> ingredients) {
		return allListsThatSumTo(VOLUME, ingredients.size()) //
				.map(quantities -> byIngredientCount(ingredients, quantities));
	}

	private static Cookie byIngredientCount(List<Ingredient> ingredients, List<Integer> quantities) {
		Preconditions.checkArgument(ingredients.size() == quantities.size());

		Map<Ingredient, Integer> recipe = IntStream.range(0, quantities.size()) //
				.mapToObj(i -> i) //
				.collect(toMap(ingredients::get, quantities::get));

		return new Cookie(recipe);
	}

	public static Stream<List<Integer>> allListsThatSumTo(int total, int elements) {
		if (elements == 1) {
			List<Integer> list = new ArrayList<>();
			list.add(total);
			return Stream.of(list);
		}

		return IntStream.range(0, total + 1) //
				.mapToObj(i -> i) //
				.flatMap(firstElement -> allListsThatSumTo(total, elements, firstElement));
	}

	private static Stream<List<Integer>> allListsThatSumTo(int total, int elements, int firstElement) {
		return allListsThatSumTo(total - firstElement, elements - 1) //
				.map(list -> prepend(list, firstElement));

	}

	private static List<Integer> prepend(List<Integer> list, Integer element) {
		list.add(0, element);
		return list;
	}

	public static void main(String[] args) throws IOException {
		System.out.println(optimumScore(ingredientsFromFile(), 500));
	}

	private static List<Ingredient> ingredientsFromFile() throws IOException {
		return Files.lines(Paths.get("src/main/java/advent/year2015/day15/input.txt")) //
				.map(Ingredient::fromString) //
				.collect(toList());
	}

	public static int optimumScore(List<Ingredient> ingredients) {
		return possibleCookies(ingredients) //
				.mapToInt(Cookie::score) //
				.max().getAsInt();
	}

	public static int optimumScore(List<Ingredient> ingredients, int calorieCount) {
		return possibleCookies(ingredients) //
				.filter(c -> c.calories() == calorieCount) //
				.mapToInt(Cookie::score) //
				.max().getAsInt();
	}

}