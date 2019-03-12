package code;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import client.model.*;

public abstract class Ahero extends Util {// ahero = hero
	// TODO == double problem /double
	public boolean myTeam;
	public Hero mhero;
	// private Cell mcell;
	public boolean moved;

	public boolean w1;
	public boolean w2;
	public boolean w3;
	public boolean w22;
	public boolean wl;

	public Point jumpTarget = null;
	public Point beforeJumpTarget = null;

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
	public ArrayList<Movement> movementList = new ArrayList<Movement>();

	public int mAP;
	public int mresAP;
	public Point swap;

	public Ahero(Hero h, boolean myTeam) {
		this.myTeam = myTeam;
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

		maxcd1 = Math.max(1, a1.getCooldown());
		maxcd2 = a2.getCooldown();
		maxcd3 = a3.getCooldown();

		// System.out.println(maxcd1 + "-" + maxcd2 + "-" + maxcd3);
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

		if ((mcell == null || mcell.getColumn() == -1) && (phase != 0 && isInVision)) {
			// after move
			Point px = null;
			int k = 0;
			if (!isInVision(myp)) {
				px = myp;
				++k;
			}
			for (Direction1 dir : Direction1.values()) {
				Point po = myp.dir1To(dir);
				if (po != null && !po.isWall && !isInVision(po)) {
					px = po;
					++k;
				}
			}
			if (k == 1) {
				System.out.println("we change :D");
				mcell = pointToCell(px);
			}

		}

		if (phase == 0 || mhp == -1 || mhp == 0)
			mhp = mhero.getCurrentHP();
		// else doesnot change ->-1 0
		myp = cellToPoint(mcell);

		a1 = mhero.getAbilities()[0];
		a2 = mhero.getAbilities()[1];
		a3 = mhero.getAbilities()[2];

		rcd1 = a1.getRemCooldown();
		rcd2 = a2.getRemCooldown();
		rcd3 = a3.getRemCooldown();

//		if (!myTeam)
//			System.out.println("WG: " + rcd1 + " - " + rcd2 + " - " + rcd3);

		isReady1 = a1.isReady();
		isReady2 = a2.isReady();
		isReady3 = a3.isReady();

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

	public abstract double evaluate(Point target, Point from);

	public int mrealAP() {
		return mAP - mresAP;
	}

	protected double myHealFrom(Point po) {
		double heal = 0;
		if (type == HeroName.BLASTER) {
		} else if (type == HeroName.SENTRY) {
		} else if (type == HeroName.HEALER) {
			heal = mostHealFrom(po, a3).se / maxcd3;
		} else if (type == HeroName.GUARDIAN) {
		}
		return heal;
	}

	protected double myHealFromO(Point po) {
		double heal = 0;
		if (type == HeroName.BLASTER) {
		} else if (type == HeroName.SENTRY) {
		} else if (type == HeroName.HEALER) {
			heal = mostHealFrom(po, a3).se / maxcd3;
		} else if (type == HeroName.GUARDIAN) {
		}
		return heal;
	}

	protected double myTurnHealFrom(Point po) {
		double heal = 0;
		if (type == HeroName.BLASTER) {
		} else if (type == HeroName.SENTRY) {
		} else if (type == HeroName.HEALER) {
			if (isReady3)
				heal = mostHealFrom(po, a3).se;
		} else if (type == HeroName.GUARDIAN) {
		}
		return heal;
	}

	protected double myTurnHealFromO(Point po) {
		double heal = 0;
		if (type == HeroName.BLASTER) {
		} else if (type == HeroName.SENTRY) {
		} else if (type == HeroName.HEALER) {
			// if (isReady3)
			// is ready ?
			heal = mostHealFrom(po, a3).se;
		} else if (type == HeroName.GUARDIAN) {
		}
		return heal;
	}

	protected double myDamageFrom(Point po) {
		double damage = 0;
		if (type == HeroName.BLASTER) {
			double d1 = mostDamageFrom(po, a1).se;
			double d3 = mostDamageFrom(po, a3).se;
			damage = (double) (Math.max(d1, d3) + (maxcd3 - 1) * d1) / maxcd3;
		} else if (type == HeroName.SENTRY) {
			double d1 = mostDamageFrom(po, a1).se;
			double d3 = mostDamageFrom(po, a3).se;
			damage = (double) (Math.max(d1, d3) + (maxcd3 - 1) * d1) / maxcd3;
		} else if (type == HeroName.HEALER) {
			damage = mostDamageFrom(po, a1).se / maxcd1;
		} else if (type == HeroName.GUARDIAN) {
			damage = mostDamageFrom(po, a1).se / maxcd1;
		}
		return damage;
	}

	protected double myDamageFromO(Point po) {
		double damage = 0;
		if (type == HeroName.BLASTER) {
			double d1 = mostDamageFrom(po, a1).se;
			double d3 = mostDamageFrom(po, a3).se;
			damage = (double) (Math.max(d1, d3) + (maxcd3 - 1) * d1) / maxcd3;
		} else if (type == HeroName.SENTRY) {
			double d1 = mostDamageFrom(po, a1).se;
			double d3 = mostDamageFrom(po, a3).se;
			damage = (double) (Math.max(d1, d3) + (maxcd3 - 1) * d1) / maxcd3;
		} else if (type == HeroName.HEALER) {
			damage = mostDamageFrom(po, a1).se / maxcd1;
		} else if (type == HeroName.GUARDIAN) {
			damage = mostDamageFrom(po, a1).se / maxcd1;
		}
		return damage;
	}

	protected double myTurnDamageFrom(Point po) {
		double damage = 0;
		if (type == HeroName.BLASTER) {
			double d1 = 0, d3 = 0;
			if (isReady1)
				d1 = mostDamageFrom(po, a1).se;
			if (isReady3)
				d3 = mostDamageFrom(po, a3).se;
			damage = Math.max(d1, d3);
		} else if (type == HeroName.SENTRY) {
			double d1 = 0, d3 = 0;
			if (isReady1)
				d1 = mostDamageFrom(po, a1).se;
			if (isReady3)
				d3 = mostDamageFrom(po, a3).se;
			damage = Math.max(d1, d3);
		} else if (type == HeroName.HEALER) {
			if (isReady1)
				damage = mostDamageFrom(po, a1).se;
		} else if (type == HeroName.GUARDIAN) {
			if (isReady1)
				damage = mostDamageFrom(po, a1).se;
		}
		return damage;
	}

	protected double myTurnDamageFromO(Point po) {
		double damage = 0;
		if (type == HeroName.BLASTER) {
			double d1 = 0, d3 = 0;
			// if (isReady1)
			d1 = mostDamageFrom(po, a1).se;
			// if (isReady3)
			d3 = mostDamageFrom(po, a3).se;
			damage = Math.max(d1, d3);
		} else if (type == HeroName.SENTRY) {
			double d1 = 0, d3 = 0;
			// if (isReady1)
			d1 = mostDamageFrom(po, a1).se;
			// if (isReady3)
			d3 = mostDamageFrom(po, a3).se;
			damage = Math.max(d1, d3);
		} else if (type == HeroName.HEALER) {
			// if (isReady1)
			damage = mostDamageFrom(po, a1).se;
		} else if (type == HeroName.GUARDIAN) {
			// if (isReady1)
			damage = mostDamageFrom(po, a1).se;
		}
		return damage;
	}

	// and him self
	private Pair<Point, Double> mostHealFrom(Point po, Ability ab) {
		int mx = 0;
		Point be = null;
		// TODO not optimal
		// TODO not right
		// only right for bomb and guardian attack (lol what about unseen ? :D
		HashMap<Integer, Ahero> teamHeros;
		if (myTeam)
			teamHeros = mlHeros;
		else
			teamHeros = osHeros;

		for (Ahero hero : teamHeros.values())
			if (hero.myp.distxy(po) <= ab.getRange() + ab.getAreaOfEffect()) {
				int kx = Math.min(hero.maxHP - hero.mhp, ab.getPower());
				if (kx > mx) {
					mx = kx;
					be = hero.myp;
				}
			}
		return new Pair<Point, Double>(be, (double) mx);
	}

	private Pair<Point, Double> mostDamageFrom(Point po, Ability ab) {
		int mx = 0;
		Point be = null;
		// TODO not optimal
		// TODO not right
		// only right for bomb and guardian attack (lol what about unseen ? :D

		for (int dx = -ab.getRange(); dx <= ab.getRange(); ++dx) {
			for (int dy = -(ab.getRange() - Math.abs(dx)); dy <= (ab.getRange() - Math.abs(dx)); ++dy) {
				int xx = po.x + dx;
				int yy = po.y + dy;
				if (!isInMap(xx, yy) || (!ab.isLobbing()
						&& cellToPoint(world.getImpactCell(ab, pointToCell(po), pointToCell(p[xx][yy]))) != p[xx][yy]))
//				 !world.isInVision(mhero.getCurrentCell(), world.getMap().getCell(myp.y + dy, myp.x + dx))
					continue;
				int k = 0;
				for (int tx = -ab.getAreaOfEffect(); tx <= ab.getAreaOfEffect(); ++tx) {
					for (int ty = -(ab.getAreaOfEffect() - Math.abs(tx)); ty <= (ab.getAreaOfEffect()
							- Math.abs(tx)); ++ty) {
						int xxx = xx + tx;
						int yyy = yy + ty;
						if (!isInMap(xxx, yyy))
							continue;
						if (myTeam && p[xxx][yyy].ofull || !myTeam && p[xxx][yyy].ifull)
							++k;
					}
				}
				if (k > mx) {
					be = p[xx][yy];
					mx = k;
				}
			}
		}
		return new Pair<Point, Double>(be, (double) mx * ab.getPower());
	}

	protected Point isItGoodToJump(Point po) {
		int minn = 100000;
		Point bp = null;
		// seenO.size ? .|x - .|y
		if (isReady2 && realAP() >= cost2) {
			for (int dx = -range2; dx <= +range2; ++dx)
				for (int dy = -(range2 - Math.abs(dx)); dy <= range2 - Math.abs(dx); ++dy) {
					if (isInMap(po.x + dx, po.y + dy) && !p[po.x + dx][po.y + dy].isWall
							&& !p[po.x + dx][po.y + dy].ifull) {
						if (obdis[v(po.x + dx, po.y + dy)] != -1 && obdis[v(po.x + dx, po.y + dy)] < minn) {
							minn = obdis[v(po.x + dx, po.y + dy)];
							bp = p[po.x + dx][po.y + dy];
						}
					}
				}
			if (minn < obdis[v(po)] - mAP / moveCost) {
				return bp;
				// btarget = bp;
				// w2 = true;
				// resAP += cost2;
				// mresAP += cost2;
			}
		}
		return null;
	}

	protected Point isItGoodToJumpInNextTurn(Point po) {
		// Dijkstra to objective zone?
		int minn = 100000;
		Point bp = null;
		if (rcd2 <= 1) {
			for (int dx = -6; dx <= 6; ++dx)
				for (int dy = -(6 - Math.abs(dx)); dy <= 6 - Math.abs(dx); ++dy) {
					if (isInMap(po.x + dx, po.y + dy) && !p[po.x + dx][po.y + dy].isWall
							&& !p[po.x + dx][po.y + dy].ifull) {
						Point cp = isItGoodToJump(p[po.x + dx][po.y + dy]);// is ready?:/
						if (cp == null)
							continue;
						if (obdis[v(cp)] != -1 && obdis[v(cp)] < minn) {
							minn = obdis[v(cp)];
							bp = p[po.x + dx][po.y + dy];
						}
					}
				}
			if (minn < obdis[v(po)] - 2 * (mAP / moveCost)) {
				return bp;
				// btarget = bp;
				// w2 = true;
				// resAP += cost2;
				// mresAP += cost2;
			}
		}
		return null;
	}

	protected void addBestMoves() {
		movementList.add(new Movement(this, myp));
		for (Direction1 dir : Direction1.values()) {
			Point po = myp.dir1To(dir);
			if (po != null && !po.isWall && !po.ifull)
				movementList.add(new Movement(this, po));
		}
	}

	protected void initMoveTurn() {
		jumpTarget = null;
		beforeJumpTarget = null;
		w1 = false;
		w2 = false;
		w3 = false;
		w22 = false;
		mresAP = 0;
		// hello :D
		Point bp = isItGoodToJump(myp);
		if (bp != null) {
			w2 = true;
			resAP += cost2;
			mresAP += cost2;
			jumpTarget = bp;
		} else {
			bp = isItGoodToJumpInNextTurn(myp);
			if (bp != null) {
				w22 = true;
				beforeJumpTarget = bp;
			}
		}
	}

	protected void startMoveTurn() {
		movementList = new ArrayList<Movement>();
	}

	protected double enemyGuardianDanger(Point po) {
		int minn = maxInt;
		double val = 0;

		// just Guardian
		for (Ahero hero : seenOG) {
			int fdis = po.distxy(hero.myp);
			val += Math.max(4 - fdis, 0) * 20;
			// minn = Math.min(minn, po.distxy(hero.myp));

		}
		// dis -> - 0 20 40 40 40 ...
		// hajm damage i ke mikhore to onja hum :D
		// farz kon yeki
		// 40 40 40 20 0 0 0
		// 40 20 0 -20 -40 -40 -40
		// -> 80 60 40 20 0 0 0
//		if (minn == maxInt)
//			return 0;
//		else if (minn == 0)
//			return 100;// to much danger
//		else if (minn == 1)
//			return 100;
//		else if (minn == 2)
//			return 100;
//		else if (minn == 3)
//			return 100;
//		else if (minn > 3)
//			return 100;
		return val;
	}

	public static double damageOfEnemiesOn(Point target) {
		double damage = 0;
		for (Ahero hero : osHeros.values()) {
			if (hero.type == HeroName.BLASTER) {
				if (hero.myp.distxy(target) <= hero.range1 + hero.aoe1)// +1 ?
					damage += hero.pow1 / hero.maxcd1;
				if (hero.myp.distxy(target) <= hero.range3 + hero.aoe3)// +1 ?
					damage += hero.pow3 / hero.maxcd3;
			} else if (hero.type == HeroName.SENTRY) {
				if (hero.myp.distxy(target) <= hero.range1 + hero.aoe1)// +1 ?
					damage += hero.pow1 / hero.maxcd1;
				if (hero.myp.distxy(target) <= hero.range3 + hero.aoe3)// +1 ?
					damage += hero.pow3 / hero.maxcd3;
			} else if (hero.type == HeroName.HEALER) {
				if (hero.myp.distxy(target) <= hero.range1 + hero.aoe1)// +1 ?
					damage += hero.pow1 / hero.maxcd1;
			} else if (hero.type == HeroName.GUARDIAN) {
				if (hero.myp.distxy(target) <= hero.range1 + hero.aoe1)// +1 ?
					damage += hero.pow1 / hero.maxcd1;
			}
		}
		return damage;
	}

	protected double damageOfEnemiesThatCanShotMeAndMyAlies(Point po) {
		double damage = 0;
		for (Ahero hero : osHeros.values()) {
			if (hero.type == HeroName.BLASTER) {
				boolean v = false;
				if (hero.myp.distxy(po) <= hero.range1 + hero.aoe1)// +1 ?
				{
					for (Ahero hhero : mlHeros.values()) {
						// nice Manhattan style :D
						if (hhero == this)
							continue;
						if (hhero.myp.distxy(hero.myp) <= hero.range1 + hero.aoe1
								&& hhero.myp.distxy(po) <= hero.aoe1 * 2)
							v = true;
					}
				}
				if (v)
					damage += hero.pow1 / hero.maxcd1;
				v = false;
				if (hero.myp.distxy(po) <= hero.range3 + hero.aoe3)// +1 ?
				{
					for (Ahero hhero : mlHeros.values()) {
						// nice Manhattan style :D
						if (hhero == this)
							continue;
						if (hhero.myp.distxy(hero.myp) <= hero.range3 + hero.aoe3
								&& hhero.myp.distxy(po) <= hero.aoe3 * 2)
							v = true;
					}
				}
				if (v)
					damage += hero.pow3 / hero.maxcd3;
			} else if (hero.type == HeroName.GUARDIAN) {
				boolean v = false;
				if (hero.myp.distxy(po) <= hero.range1 + hero.aoe1)// +1 ?
				{
					for (Ahero hhero : mlHeros.values()) {
						// nice Manhattan style :D
						if (hhero == this)
							continue;
						if (hhero.myp.distxy(hero.myp) <= hero.range1 + hero.aoe1
								&& hhero.myp.distxy(po) <= hero.aoe1 * 2)
							v = true;
					}
				}
				if (v)
					damage += hero.pow1 / hero.maxcd1;
			}
		}
		return damage;
	}
}
