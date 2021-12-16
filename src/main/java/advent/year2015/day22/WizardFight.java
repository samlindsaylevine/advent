package advent.year2015.day22;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;

/**
 * --- Day 22: Wizard Simulator 20XX ---
 * Little Henry Case decides that defeating bosses with swords and stuff is boring.  Now he's playing the game with a
 * wizard.  Of course, he gets stuck on another boss and needs your help again.
 * In this version, combat still proceeds with the player and the boss taking alternating turns.  The player still goes
 * first.  Now, however, you don't get any equipment; instead, you must choose one of your spells to cast.  The first
 * character at or below 0 hit points loses.
 * Since you're a wizard, you don't get to wear armor, and you can't attack normally.  However, since you do magic
 * damage, your opponent's armor is ignored, and so the boss effectively has zero armor as well.  As before, if armor
 * (from a spell, in this case) would reduce damage below 1, it becomes 1 instead - that is, the boss' attacks always
 * deal at least 1 damage.
 * On each of your turns, you must select one of your spells to cast.  If you cannot afford to cast any spell, you
 * lose.  Spells cost mana; you start with 500 mana, but have no maximum limit.  You must have enough mana to cast a
 * spell, and its cost is immediately deducted when you cast it.  Your spells are Magic Missile, Drain, Shield, Poison,
 * and Recharge.
 * 
 * Magic Missile costs 53 mana.  It instantly does 4 damage.
 * Drain costs 73 mana.  It instantly does 2 damage and heals you for 2 hit points.
 * Shield costs 113 mana.  It starts an effect that lasts for 6 turns.  While it is active, your armor is increased by
 * 7.
 * Poison costs 173 mana.  It starts an effect that lasts for 6 turns.  At the start of each turn while it is active,
 * it deals the boss 3 damage.
 * Recharge costs 229 mana.  It starts an effect that lasts for 5 turns.  At the start of each turn while it is active,
 * it gives you 101 new mana.
 * 
 * Effects all work the same way.  Effects apply at the start of both the player's turns and the boss' turns.  Effects
 * are created with a timer (the number of turns they last); at the start of each turn, after they apply any effect
 * they have, their timer is decreased by one.  If this decreases the timer to zero, the effect ends.  You cannot cast
 * a spell that would start an effect which is already active.  However, effects can be started on the same turn they
 * end.
 * For example, suppose the player has 10 hit points and 250 mana, and that the boss has 13 hit points and 8 damage:
 * -- Player turn --
 * - Player has 10 hit points, 0 armor, 250 mana
 * - Boss has 13 hit points
 * Player casts Poison.
 * 
 * -- Boss turn --
 * - Player has 10 hit points, 0 armor, 77 mana
 * - Boss has 13 hit points
 * Poison deals 3 damage; its timer is now 5.
 * Boss attacks for 8 damage.
 * 
 * -- Player turn --
 * - Player has 2 hit points, 0 armor, 77 mana
 * - Boss has 10 hit points
 * Poison deals 3 damage; its timer is now 4.
 * Player casts Magic Missile, dealing 4 damage.
 * 
 * -- Boss turn --
 * - Player has 2 hit points, 0 armor, 24 mana
 * - Boss has 3 hit points
 * Poison deals 3 damage. This kills the boss, and the player wins.
 * 
 * Now, suppose the same initial conditions, except that the boss has 14 hit points instead:
 * -- Player turn --
 * - Player has 10 hit points, 0 armor, 250 mana
 * - Boss has 14 hit points
 * Player casts Recharge.
 * 
 * -- Boss turn --
 * - Player has 10 hit points, 0 armor, 21 mana
 * - Boss has 14 hit points
 * Recharge provides 101 mana; its timer is now 4.
 * Boss attacks for 8 damage!
 * 
 * -- Player turn --
 * - Player has 2 hit points, 0 armor, 122 mana
 * - Boss has 14 hit points
 * Recharge provides 101 mana; its timer is now 3.
 * Player casts Shield, increasing armor by 7.
 * 
 * -- Boss turn --
 * - Player has 2 hit points, 7 armor, 110 mana
 * - Boss has 14 hit points
 * Shield's timer is now 5.
 * Recharge provides 101 mana; its timer is now 2.
 * Boss attacks for 8 - 7 = 1 damage!
 * 
 * -- Player turn --
 * - Player has 1 hit point, 7 armor, 211 mana
 * - Boss has 14 hit points
 * Shield's timer is now 4.
 * Recharge provides 101 mana; its timer is now 1.
 * Player casts Drain, dealing 2 damage, and healing 2 hit points.
 * 
 * -- Boss turn --
 * - Player has 3 hit points, 7 armor, 239 mana
 * - Boss has 12 hit points
 * Shield's timer is now 3.
 * Recharge provides 101 mana; its timer is now 0.
 * Recharge wears off.
 * Boss attacks for 8 - 7 = 1 damage!
 * 
 * -- Player turn --
 * - Player has 2 hit points, 7 armor, 340 mana
 * - Boss has 12 hit points
 * Shield's timer is now 2.
 * Player casts Poison.
 * 
 * -- Boss turn --
 * - Player has 2 hit points, 7 armor, 167 mana
 * - Boss has 12 hit points
 * Shield's timer is now 1.
 * Poison deals 3 damage; its timer is now 5.
 * Boss attacks for 8 - 7 = 1 damage!
 * 
 * -- Player turn --
 * - Player has 1 hit point, 7 armor, 167 mana
 * - Boss has 9 hit points
 * Shield's timer is now 0.
 * Shield wears off, decreasing armor by 7.
 * Poison deals 3 damage; its timer is now 4.
 * Player casts Magic Missile, dealing 4 damage.
 * 
 * -- Boss turn --
 * - Player has 1 hit point, 0 armor, 114 mana
 * - Boss has 2 hit points
 * Poison deals 3 damage. This kills the boss, and the player wins.
 * 
 * You start with 50 hit points and 500 mana points. The boss's actual stats are in your puzzle input. What is the
 * least amount of mana you can spend and still win the fight?  (Do not include mana recharge effects as "spending"
 * negative mana.)
 * 
 * --- Part Two ---
 * On the next run through the game, you increase the difficulty to hard.
 * At the start of each player turn (before any other effects apply), you lose 1 hit point. If this brings you to or
 * below 0 hit points, you lose.
 * With the same starting stats for you and the boss, what is the least amount of mana you can spend and still win the
 * fight?
 * 
 */
public class WizardFight {

	private final List<Spell> spellsCast;
	private final int heroHp;
	private final int heroArmor;
	private final int bossHp;
	private final int bossDmg;
	private final int heroMana;

	private final boolean hardMode;

	private final Map<Effect, Integer> effectDurations;

	public WizardFight(boolean hardMode) {
		this(50, //
				0, //
				51, //
				9, //
				500, //
				hardMode);
	}

	WizardFight(int heroHp, int heroArmor, int bossHp, int bossDmg, int heroMana, boolean hardMode) {
		this(ImmutableList.of(), //
				heroHp - (hardMode ? 1 : 0), //
				heroArmor, //
				bossHp, //
				bossDmg, //
				heroMana, //
				Arrays.stream(Effect.values()) //
						.collect(toMap(i -> i, i -> 0)),
				hardMode);
	}

	WizardFight(List<Spell> spellsCast, int heroHp, int heroArmor, int bossHp, int bossDmg, int heroMana,
			Map<Effect, Integer> effectDurations, boolean hardMode) {
		this.spellsCast = spellsCast;
		this.heroHp = heroHp;
		this.heroArmor = heroArmor;
		this.bossHp = bossHp;
		this.bossDmg = bossDmg;
		this.heroMana = heroMana;
		this.effectDurations = effectDurations;
		this.hardMode = hardMode;
	}

	WizardFight heroLifeChanges(int heroHpDelta) {
		return new WizardFight(this.spellsCast, this.heroHp + heroHpDelta, this.heroArmor, this.bossHp, this.bossDmg,
				this.heroMana, this.effectDurations, this.hardMode);
	}

	WizardFight heroManaChanges(int heroManaDelta) {
		return new WizardFight(this.spellsCast, this.heroHp, this.heroArmor, this.bossHp, this.bossDmg,
				this.heroMana + heroManaDelta, this.effectDurations, this.hardMode);
	}

	WizardFight heroArmorChanges(int heroArmorDelta) {
		return new WizardFight(this.spellsCast, this.heroHp, this.heroArmor + heroArmorDelta, this.bossHp, this.bossDmg,
				this.heroMana, this.effectDurations, this.hardMode);
	}

	WizardFight bossLifeChanges(int bossHpDelta) {
		return new WizardFight(this.spellsCast, this.heroHp, this.heroArmor, this.bossHp + bossHpDelta, this.bossDmg,
				this.heroMana, this.effectDurations, this.hardMode);
	}

	private WizardFight withEffectOneLessDuration(Effect effect) {
		Map<Effect, Integer> newDurations = new HashMap<>(this.effectDurations);
		newDurations.put(effect, newDurations.get(effect) - 1);

		return new WizardFight(this.spellsCast, this.heroHp, this.heroArmor, this.bossHp, this.bossDmg, this.heroMana,
				newDurations, this.hardMode);
	}

	private WizardFight withSpellCast(Spell spell) {
		List<Spell> newSpellsCast = ImmutableList.<Spell> builder() //
				.addAll(this.spellsCast) //
				.add(spell) //
				.build();

		return new WizardFight(newSpellsCast, this.heroHp, this.heroArmor, this.bossHp, this.bossDmg, this.heroMana,
				this.effectDurations, this.hardMode);
	}

	public int getHeroHp() {
		return this.heroHp;
	}

	public int getBossHp() {
		return this.bossHp;
	}

	public int getHeroMana() {
		return this.heroMana;
	}

	public boolean isActive(Effect effect) {
		return this.effectDurations.get(effect) > 0;
	}

	public boolean isOver() {
		return this.heroDied() || this.heroLostOom() || this.heroWon();
	}

	private boolean isOverInMiddleOfTurn() {
		return this.heroDied() || this.heroWon();
	}

	public boolean heroDied() {
		return this.heroHp <= 0;
	}

	public boolean heroLostOom() {
		return this.heroMana < Spell.minimumCost() && this.bossHp > 0;
	}

	public boolean heroWon() {
		return this.bossHp <= 0;
	}

	public int totalManaSpent() {
		return this.spellsCast.stream().mapToInt(Spell::cost).sum();
	}

	public boolean isWorthContinuingToEvaluateChildren() {
		return this.totalManaSpent() < 1288;
	}

	public WizardFight cast(Spell spell) {

		if (!spell.isCastable(this)) {
			throw new IllegalArgumentException();
		}

		WizardFight output = this.withSpellCast(spell);

		output = spell.apply(output);

		// Boss turn effects
		output = output.applyEffects();

		if (!output.isOverInMiddleOfTurn()) {
			int dmgTaken = Math.max(output.bossDmg - output.heroArmor, 1);

			output = output.heroLifeChanges(-dmgTaken);
		}

		if (!output.isOverInMiddleOfTurn() && this.hardMode) {
			output = output.heroLifeChanges(-1);
		}

		if (!output.isOverInMiddleOfTurn()) {
			// Hero turn effects
			output = output.applyEffects();
		}

		return output;
	}

	public Stream<WizardFight> nextOptions() {
		if (this.isOver() || !this.isWorthContinuingToEvaluateChildren()) {
			return Stream.empty();
		}

		return Arrays.stream(Spell.values()) //
				.filter(s -> s.isCastable(this)) //
				.map(this::cast);
	}

	WizardFight withEffect(Effect effect, int duration) {
		WizardFight fight = effect.onCast(this);
		Map<Effect, Integer> newEffectDurations = new HashMap<>(fight.effectDurations);
		newEffectDurations.put(effect, duration);
		return new WizardFight(fight.spellsCast, fight.heroHp, fight.heroArmor, fight.bossHp, fight.bossDmg,
				fight.heroMana, newEffectDurations, this.hardMode);
	}

	private WizardFight applyEffects() {
		WizardFight output = this;

		for (Effect effect : Effect.values()) {
			if (output.isActive(effect)) {
				output = effect.onTick(output);
				output = output.withEffectOneLessDuration(effect);
				if (!output.isActive(effect)) {
					output = effect.onBlur(output);
				}
			}
		}

		return output;
	}

	static List<WizardFight> allWinningFights(WizardFight initial) {
		List<WizardFight> output = new ArrayList<>();

		List<WizardFight> working = ImmutableList.of(initial);

		while (!working.isEmpty()) {
			System.out.println(working.size() + ": " + output.size());

			working.stream().filter(WizardFight::heroWon).forEach(output::add);
			working = working.stream().flatMap(WizardFight::nextOptions).collect(toList());
		}

		return output;
	}

	@Override
	public String toString() {
		return this.totalManaSpent() + " (" + this.spellsCast.size() + "): " + this.spellsCast + " hero=" + this.heroHp
				+ " mana=" + this.heroMana + " boss=" + this.bossHp;
	}

	public static WizardFight minManaSpentToWin() {
		return allWinningFights(new WizardFight(false)).stream().min(Comparator.comparing(WizardFight::totalManaSpent))
				.get();
	}

	public static WizardFight minManaSpentToWinHard() {
		return allWinningFights(new WizardFight(true)).stream().min(Comparator.comparing(WizardFight::totalManaSpent))
				.get();
	}

	public static void main(String[] args) {
		WizardFight fight = minManaSpentToWinHard();
		System.out.println(fight);
	}

}