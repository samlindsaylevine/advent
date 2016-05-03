package advent.day15;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Preconditions;

public class Ingredient {

	private final int capacity;
	private final int durability;
	private final int flavor;
	private final int texture;
	private final int calories;

	public Ingredient(int capacity, int durability, int flavor, int texture, int calories) {
		this.capacity = capacity;
		this.durability = durability;
		this.flavor = flavor;
		this.texture = texture;
		this.calories = calories;
	}

	private static final Pattern REGEX = Pattern.compile(
			"\\w+: capacity (-?\\d+), durability (-?\\d+), flavor (-?\\d+), texture (-?\\d+), calories (-?\\d+)");

	public static Ingredient fromString(String representation) {
		Matcher matcher = REGEX.matcher(representation);
		Preconditions.checkArgument(matcher.matches(), representation);

		return new Ingredient(Integer.valueOf(matcher.group(1)), Integer.valueOf(matcher.group(2)),
				Integer.valueOf(matcher.group(3)), Integer.valueOf(matcher.group(4)),
				Integer.valueOf(matcher.group(5)));
	}

	public int getCapacity() {
		return this.capacity;
	}

	public int getDurability() {
		return this.durability;
	}

	public int getFlavor() {
		return this.flavor;
	}

	public int getTexture() {
		return this.texture;
	}

	public int getCalories() {
		return this.calories;
	}

}
