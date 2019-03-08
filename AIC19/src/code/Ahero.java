package code;

import java.util.ArrayList;
import java.util.Stack;

import client.model.*;

public abstract class Ahero extends Util {// ahero = hero
	public Hero mhero;
	// private Cell mcell;
	public boolean moved;

	public boolean w1;
	public boolean w2;
	public boolean w3;
	public Point jumpTarget = null;
	public boolean isInDanger;
	public boolean canFight;
	public boolean can1;
	public boolean can3;

	public Point myp;
	public int mid;
	public HeroName type;
	public boolean isDead;
	public boolean isInVision;
	public int mhp;
	public int maxHP;
	public int mrrt;
	public int moveCost;
	public Stack<Point> mainPath;

	public int potDamage = 0;
	public int potHeal = 0;

	public ArrayList<Ahero> seenO = new ArrayList<Ahero>();
	public ArrayList<Ahero> seenA1 = new ArrayList<Ahero>();
	public ArrayList<Ahero> seenO2 = new ArrayList<Ahero>();
	public ArrayList<Ahero> seenA3 = new ArrayList<Ahero>();

	public Ability a1;
	public Ability a2;
	public Ability a3;

	public int cost1;
	public int cost2;
	public int cost3;

	public int range1;
	public int range2;
	public int range3;

	public int aoe1;
	public int aoe2;
	public int aoe3;

	public int maxcd1;
	public int maxcd2;
	public int maxcd3;

	public int pow1;
	public int pow2;
	public int pow3;

	public int rcd1;
	public int rcd2;
	public int rcd3;

	public boolean isReady1;
	public boolean isReady2;
	public boolean isReady3;

	public boolean isLobbing1;
	public boolean isLobbing2;
	public boolean isLobbing3;
	public ArrayList<Action> actionList = new ArrayList<Action>();

	public int mAP;
	public int mresAP;

	public Ahero(Hero h) {
		this.mhero = h;
		Cell mcell = mhero.getCurrentCell();
		myp = cellToPoint(mcell);

		a1 = mhero.getAbilities()[0];
		a2 = mhero.getAbilities()[1];
		a3 = mhero.getAbilities()[2];

		cost1 = a1.getAPCost();
		cost2 = a2.getAPCost();
		cost3 = a3.getAPCost();

		range1 = a1.getRange();
		range2 = a2.getRange();
		range3 = a3.getRange();

		aoe1 = a1.getAreaOfEffect();
		aoe2 = a2.getAreaOfEffect();
		aoe3 = a3.getAreaOfEffect();

		maxcd1 = a1.getCooldown();
		maxcd2 = a2.getCooldown();
		maxcd3 = a3.getCooldown();

		pow1 = a1.getPower();
		pow2 = a2.getPower();
		pow3 = a3.getPower();

		isLobbing1 = a1.isLobbing();
		isLobbing2 = a2.isLobbing();
		isLobbing3 = a3.isLobbing();

		maxHP = mhero.getMaxHP();

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
		moved = false;
		mhero = world.getHero(mid);
		Cell mcell = mhero.getCurrentCell();
		myp = cellToPoint(mcell);

		a1 = mhero.getAbilities()[0];
		a2 = mhero.getAbilities()[1];
		a3 = mhero.getAbilities()[2];

		rcd1 = a1.getRemCooldown();
		rcd2 = a2.getRemCooldown();
		rcd3 = a3.getRemCooldown();

		isReady1 = a1.isReady();
		isReady2 = a2.isReady();
		isReady3 = a3.isReady();

		mhp = mhero.getCurrentHP();
		mrrt = mhero.getRemRespawnTime();
		isDead = false;
		if (mrrt != 0)
			isDead = true;

		isInVision = true;
		if (mcell == null || mcell.getColumn() == -1)
			isInVision = false;
	}

	public abstract void update();

	public abstract void moveTurn();

	public abstract void actionTurn();

	public abstract Point getNewDodge();

	public int mrealAP() {
		return mAP - mresAP;
	}
}
