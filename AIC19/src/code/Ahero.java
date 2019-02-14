package code;

import client.model.*;

public abstract class Ahero extends Util {// ahero = hero
	public Hero h;
	public Point myp;

	public Ahero(Hero h) {
		this.h = h;
		myp = cellToPoint(h.getCurrentCell());
	}

	public void updateHero() {
		h = world.getHero(h.getId());
		myp = cellToPoint(h.getCurrentCell());
	}

	public abstract void doTurn();

	public abstract void moveTurn();
}
