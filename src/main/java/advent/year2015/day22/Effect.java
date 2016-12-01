package advent.year2015.day22;

public enum Effect {

	SHIELD {
		@Override
		public WizardFight onCast(WizardFight fightState) {
			return fightState.heroArmorChanges(7);
		}

		@Override
		public WizardFight onBlur(WizardFight fightState) {
			return fightState.heroArmorChanges(-7);
		}
	},
	POISON {
		@Override
		public WizardFight onTick(WizardFight fightState) {
			return fightState.bossLifeChanges(-3);
		}
	},
	RECHARGE {
		@Override
		public WizardFight onTick(WizardFight fightState) {
			return fightState.heroManaChanges(101);
		}
	};

	public WizardFight onCast(WizardFight fightState) {
		return fightState;
	}

	public WizardFight onTick(WizardFight fightState) {
		return fightState;
	}

	public WizardFight onBlur(WizardFight fightState) {
		return fightState;
	}

}
