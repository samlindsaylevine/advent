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

/**
 * --- Day 21: RPG Simulator 20XX ---
 * Little Henry Case got a new video game for Christmas.  It's an RPG, and he's stuck on a boss.  He needs to know what
 * equipment to buy at the shop.  He hands you the controller.
 * In this game, the player (you) and the enemy (the boss) take turns attacking.  The player always goes first.  Each
 * attack reduces the opponent's hit points by at least 1.  The first character at or below 0 hit points loses.
 * Damage dealt by an attacker each turn is equal to the attacker's damage score minus the defender's armor score.  An
 * attacker always does at least 1 damage.  So, if the attacker has a damage score of 8, and the defender has an armor
 * score of 3, the defender loses 5 hit points.  If the defender had an armor score of 300, the defender would still
 * lose 1 hit point.
 * Your damage score and armor score both start at zero.  They can be increased by buying items in exchange for gold. 
 * You start with no items and have as much gold as you need.  Your total damage or armor is equal to the sum of those
 * stats from all of your items.  You have 100 hit points.
 * Here is what the item shop is selling:
 * Weapons:    Cost  Damage  Armor
 * Dagger        8     4       0
 * Shortsword   10     5       0
 * Warhammer    25     6       0
 * Longsword    40     7       0
 * Greataxe     74     8       0
 * 
 * Armor:      Cost  Damage  Armor
 * Leather      13     0       1
 * Chainmail    31     0       2
 * Splintmail   53     0       3
 * Bandedmail   75     0       4
 * Platemail   102     0       5
 * 
 * Rings:      Cost  Damage  Armor
 * Damage +1    25     1       0
 * Damage +2    50     2       0
 * Damage +3   100     3       0
 * Defense +1   20     0       1
 * Defense +2   40     0       2
 * Defense +3   80     0       3
 * 
 * You must buy exactly one weapon; no dual-wielding.  Armor is optional, but you can't use more than one.  You can buy
 * 0-2 rings (at most one for each hand).  You must use any items you buy.  The shop only has one of each item, so you
 * can't buy, for example, two rings of Damage +3.
 * For example, suppose you have 8 hit points, 5 damage, and 5 armor, and that the boss has 12 hit points, 7 damage,
 * and 2 armor:
 * 
 * The player deals 5-2 = 3 damage; the boss goes down to 9 hit points.
 * The boss deals 7-5 = 2 damage; the player goes down to 6 hit points.
 * The player deals 5-2 = 3 damage; the boss goes down to 6 hit points.
 * The boss deals 7-5 = 2 damage; the player goes down to 4 hit points.
 * The player deals 5-2 = 3 damage; the boss goes down to 3 hit points.
 * The boss deals 7-5 = 2 damage; the player goes down to 2 hit points.
 * The player deals 5-2 = 3 damage; the boss goes down to 0 hit points.
 * 
 * In this scenario, the player wins!  (Barely.)
 * You have 100 hit points.  The boss's actual stats are in your puzzle input.  What is the least amount of gold you
 * can spend and still win the fight?
 * 
 * --- Part Two ---
 * Turns out the shopkeeper is working with the boss, and can persuade you to buy whatever items he wants. The other
 * rules still apply, and he still only has one of each item.
 * What is the most amount of gold you can spend and still lose the fight?
 * 
 */
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