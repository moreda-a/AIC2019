package code;

import client.model.*;

public class Action extends Util {
	// like void castAbility(Hero, Ability, Cell);
	Ahero ahero;
	Hero mhero;
	Ability ability;
	Point targetPoint;
	Cell targetCell;
	Boolean isNothing = false;

	public Action(Ahero ahero, Ability bb, Point targetPoint) {
		this.ahero = ahero;
		this.mhero = ahero.mhero;
		this.ability = bb;
		this.targetPoint = targetPoint;
		this.targetCell = pointToCell(targetPoint);
	}

	public Action() {
		isNothing = true;
	}

	public void doAction() {
		world.castAbility(mhero, ability, targetCell);
	}
}
