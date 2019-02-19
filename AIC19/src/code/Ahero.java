package code;

import client.model.*;

public abstract class Ahero extends Util {// ahero = hero
	public Hero mhero;
	public Point myp;
	public Cell mcell;
	public int mid;
	public HeroName type;
	public boolean isDead;
	public int mhp;
	public int mrrt;
	public int moveCost;

	public Ahero(Hero h) {
		this.mhero = h;
		mcell = mhero.getCurrentCell();
		myp = cellToPoint(mcell);

		mrrt = mhero.getRemRespawnTime();
		mhp = mhero.getCurrentHP();
		mid = mhero.getId();
		type = mhero.getName();
		moveCost = mhero.getMoveAPCost();

		isDead = false;
		if (mhero.getRemRespawnTime() != 0)
			isDead = true;
	}

	public void updateHero() {
		mhero = world.getHero(mid);
		mcell = mhero.getCurrentCell();
		myp = cellToPoint(mcell);
		mhp = mhero.getCurrentHP();
		mrrt = mhero.getRemRespawnTime();
		isDead = false;
		if (mrrt != 0)
			isDead = true;
	}

	public abstract void update();

	public abstract void moveTurn();

	public abstract void actionTurn();
}
