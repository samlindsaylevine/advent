package advent.day15;

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
		return Files.lines(Paths.get("src/advent/day15/input.txt")) //
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
