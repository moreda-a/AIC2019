package code;

import client.model.*;

public class Action extends Util {
	// like void castAbility(Hero, Ability, Cell);
	Ahero ahero;
	Hero mhero;
	Ability ability;
	Point targetPoint;

	Boolean isNothing = false;

	public Action(Ahero ahero, Ability bb, Point targetPoint) {
		this.ahero = ahero;
		this.mhero = ahero.mhero;
		this.ability = bb;
		this.targetPoint = targetPoint;
	}

	public Action() {
		isNothing = true;
	}

	public void doAction() {
		System.out.println(targetPoint + " " + targetPoint.ifull);
		if (ability.getType() == AbilityType.DODGE) {
			if (targetPoint.ifull) {
				targetPoint = ahero.getNewDodge();
				if (targetPoint == null)
					return;
			}
			Point lastMyp = ahero.myp;
			ahero.myp = targetPoint;

			if (!mfulls.contains(lastMyp))
				System.err.println("WTF1");
			mfulls.remove(lastMyp);
			mfulls.add(targetPoint);
			if (!lastMyp.ifull)
				System.err.println("WTF1");
			lastMyp.ifull = false;
			targetPoint.ifull = true;
		}
		world.castAbility(mhero, ability, pointToCell(targetPoint));
	}
}
