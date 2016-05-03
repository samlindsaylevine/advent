package advent.day22;

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
