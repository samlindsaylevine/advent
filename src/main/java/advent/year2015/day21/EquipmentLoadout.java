package advent.year2015.day21;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

public class EquipmentLoadout {

	private static class Item {
		private final String name;
		private final int cost;
		private final int damage;
		private final int armor;

		public Item(String name, int cost, int damage, int armor) {
			this.name = name;
			this.cost = cost;
			this.damage = damage;
			this.armor = armor;
		}

		public String getName() {
			return this.name;
		}

		public int getCost() {
			return this.cost;
		}

		public int getDamage() {
			return this.damage;
		}

		public int getArmor() {
			return this.armor;
		}

		@Override
		public String toString() {
			return this.getName();
		}
	}

	public static final List<Item> WEAPONS = ImmutableList.of( //
			new Item("Dagger", 8, 4, 0), //
			new Item("Shortsword", 10, 5, 0), //
			new Item("Warhammer", 25, 6, 0), //
			new Item("Longsword", 40, 7, 0), //
			new Item("Greataxe", 74, 8, 0));

	public static final List<Item> ARMOR = ImmutableList.of( //
			new Item("Leather", 13, 0, 1), //
			new Item("Chainmail", 31, 0, 2), //
			new Item("Splintmail", 53, 0, 3), //
			new Item("Bandedmail", 75, 0, 4), //
			new Item("Platemail", 102, 0, 5));

	public static final List<Item> RINGS = ImmutableList.of( //
			new Item("Damage +1", 25, 1, 0), //
			new Item("Damage +2", 50, 2, 0), //
			new Item("Damage +3", 100, 3, 0), //
			new Item("Defense +1", 20, 0, 1), //
			new Item("Defense +2", 40, 0, 2), //
			new Item("Defense +3", 80, 0, 3));

	private final Item weapon;
	private final Optional<Item> armor;
	private final List<Item> rings;

	public EquipmentLoadout(Item weapon, Optional<Item> armor, List<Item> rings) {
		Preconditions.checkArgument(rings.size() <= 2);
		this.weapon = weapon;
		this.armor = armor;
		this.rings = rings;
	}

	public Character asHero() {
		return new Character(Character.HERO_HP, this.total(Item::getArmor), this.total(Item::getDamage));
	}

	public int cost() {
		return this.total(Item::getCost);
	}

	private int total(Function<Item, Integer> itemValue) {
		return itemValue.apply(this.weapon) + //
				this.armor.map(itemValue).orElse(0) + //
				this.rings.stream().mapToInt(itemValue::apply).sum();
	}

	public static List<EquipmentLoadout> allAvailable() {
		List<EquipmentLoadout> output = new ArrayList<>();

		List<Optional<Item>> armorOptions = Stream
				.concat(Stream.of(Optional.<Item> empty()), ARMOR.stream().map(Optional::of)) //
				.collect(toList());

		for (Item weapon : WEAPONS) {
			for (Optional<Item> armor : armorOptions) {
				for (List<Item> rings : ringOptions()) {
					output.add(new EquipmentLoadout(weapon, armor, rings));
				}
			}
		}

		return output;
	}

	private static List<List<Item>> ringOptions() {
		List<List<Item>> output = new ArrayList<>();

		// No ring option
		output.add(new ArrayList<>());

		// One ring options
		RINGS.stream().map(ImmutableList::of).forEach(output::add);

		// Two ring options
		for (int i = 0; i < RINGS.size() - 1; i++) {
			for (int j = i + 1; j < RINGS.size(); j++) {
				output.add(ImmutableList.of(RINGS.get(i), RINGS.get(j)));
			}
		}

		return output;
	}

	public boolean defeatsTheBoss() {
		Character hero = this.asHero();
		Character boss = Character.boss();

		return new Fight(hero, boss).winner() == hero;
	}

	@Override
	public String toString() {
		return "EquipmentLoadout [weapon=" + this.weapon + ", armor=" + this.armor + ", rings=" + this.rings + ", cost="
				+ this.cost() + "]";
	}

	public static EquipmentLoadout cheapestThatDefeatsBoss() {
		return allAvailable().stream() //
				.sorted(Comparator.comparing(EquipmentLoadout::cost)) //
				.filter(EquipmentLoadout::defeatsTheBoss) //
				.findFirst().get();
	}

	public static EquipmentLoadout mostExpensiveThatLosesToBoss() {
		return allAvailable().stream() //
				.sorted(Comparator.comparing(EquipmentLoadout::cost).reversed()) //
				.filter(e -> !e.defeatsTheBoss()) //
				.findFirst().get();
	}

	public static void main(String[] args) {
		System.out.println(mostExpensiveThatLosesToBoss());
	}
}
