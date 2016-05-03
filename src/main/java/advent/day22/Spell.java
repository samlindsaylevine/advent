package advent.day22;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;

public enum Spell {

	MAGIC_MISSILE(53, f -> f.bossLifeChanges(-4)), //
	DRAIN(73, f -> f.bossLifeChanges(-2).heroLifeChanges(2)), //
	SHIELD(113, Effect.SHIELD, 6), //
	POISON(173, Effect.POISON, 6), //
	RECHARGE(229, Effect.RECHARGE, 5);

	private final int cost;

	private final Function<WizardFight, WizardFight> effect;

	private final Predicate<WizardFight> isCastable;

	private Spell(int cost, Function<WizardFight, WizardFight> effect) {
		this.cost = cost;
		this.effect = effect;
		this.isCastable = f -> true;
	}

	private Spell(int cost, Effect effect, int duration) {
		this.cost = cost;
		this.effect = f -> f.withEffect(effect, duration);
		this.isCastable = f -> !f.isActive(effect);
	}

	public int cost() {
		return this.cost;
	}

	public boolean isCastable(WizardFight fightState) {
		return fightState.getHeroMana() >= this.cost && this.isCastable.test(fightState);
	}

	public WizardFight apply(WizardFight fightState) {
		WizardFight withManaSpent = fightState.heroManaChanges(-this.cost);
		return this.effect.apply(withManaSpent);
	}

	public static int minimumCost() {
		return Arrays.stream(values()).mapToInt(Spell::cost).min().getAsInt();
	}

}
