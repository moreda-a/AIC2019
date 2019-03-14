package code;

import java.util.ArrayList;
import java.util.HashSet;

import client.model.*;

public class Healer extends Ahero {

	public Healer(Hero h, boolean myTeam) {
		super(h, myTeam);
	}

	@Override
	public void update() {
		updateHero();

	}

	@Override
	public void moveTurn() {
		System.out.println("Healer: " + mid + " - " + mhp + " - " + myp);

		if (phase == 0)
			initMoveTurn();
		startMoveTurn();

		stateCheck();

		addBestMoves();
		// Point bp = chooseBestMove();
		// tryToMoveTo(bp);

	}

	@Override
	protected void stateCheck() {
		seenO = new ArrayList<Ahero>();
		seenA1 = new ArrayList<Ahero>();
		seenA3 = new ArrayList<Ahero>();
		seenOB = new ArrayList<Ahero>();
		seenOS = new ArrayList<Ahero>();
		seenOH = new ArrayList<Ahero>();
		seenOG = new ArrayList<Ahero>();
		isInDanger = false;
		can1 = false;
		can3 = false;
		for (Ahero hero : osHeros.values()) {
			seenO.add(hero);
			if (hero.type == HeroName.BLASTER)
				seenOB.add(hero);
			else if (hero.type == HeroName.SENTRY)
				seenOS.add(hero);
			else if (hero.type == HeroName.HEALER)
				seenOH.add(hero);
			else if (hero.type == HeroName.GUARDIAN)
				seenOG.add(hero);

			// if (isInVision(myp, hero.myp)) {
			if (myp.distxy(hero.myp) <= range1 + aoe1) {
				// TODO khati ...
				seenA1.add(hero);
			}

			// }
			if (myp.distxy(hero.myp) <= 7)
				isInDanger = true;
		}
		for (Ahero hero : mlHeros.values()) {
			if (myp.distxy(hero.myp) <= range3 + aoe2) {
				seenA3.add(hero);
			}
		}
		// isReady1 == true
		if (seenA1.size() != 0 && isReady1 && (realAP() >= cost1))
			can1 = true;
		if (seenA3.size() != 0 && isReady3 && (realAP() >= cost3))
			can3 = true;
		// System.out.println(isReady3);
	}

	private Point chooseBestMove() {
		Point bp = null;
		double maxx = evaluate(myp, myp);// set max to no move TODO

		// for no lock
		// but can lock :( TODO
		// problem with moving idiots!!!

		for (Direction1 dir : Direction1.values()) {
			Point po = myp.dir1To(dir);
			if (po != null && !po.isWall && !po.ifull) {
				// do we really need ifull ?
				// can we calculate all together ?
				// what about planning ?
				double ev = evaluate(po, myp);
				System.out.println(ev + " - " + dir);
				if (maxx < ev) {
					maxx = ev;
					bp = po;
				}
			}
		}
		return bp;
	}

	@Override
	public double evaluate(Point target, Point from) {
		int mcc = 0;
		double ev = 0;
		double dd = 0;
		if (target != from) {
			mcc = moveCost;
		} else {
			double maxx = -100000;// set max to no move TODO
			boolean v = false, vt = false;
			for (Direction1 dir : Direction1.values()) {
				Point po = from.dir1To(dir);
				if (po != null && !po.isWall && po.ifull)
					v = true;
				if (po.isWall)
					vt = true;
			}
			if (!v) {
				// maxx = evaluate(myp, myp);
				maxx = 0;
				if (vt && !from.isInObjectiveZone) {
					maxx -= 0.05;
				}
				// not lock by wall
				// System.out.println(maxx);
			}
			dd -= maxx;
			if (w2)
				dd -= 200000;
		}
		ArrayList<Ahero> sameNuke = DangerNuke(target);
		Point px = minDisFriend(target);
		// now it is bullshit just check 4 range

		dd += (double) mcc / 100;// .04 .06 .08

		if (seenO.size() == 0) {
			dd += obdis[v(target)]; // 0 1 2 3 ...// chand nafar nazdik manan
			if (sameNuke != null) {
				// if(obdis[v(po)] == 0)
				dd -= (double) target.distxy(px) * 0.15;// no dis
				// dd += (double) sameNuke.size() * 0.3;
			} else
				dd -= 0.75;

			dd += (double) ordis[v(target)] * 0.06;
			if (beforeJumpTarget != null)
				dd += dis[v(target)][v(beforeJumpTarget)];
		} else {
			dd += (double) obdis[v(target)] * 0.1;
			if (mrealAP() < cost1 + mcc && can1) {
				dd += 15;
			}
			// back line idea for not getting stalk at least one level stalk
			Point pp = minDisEnemy(target);
			int mde = dis[v(target)][v(pp)];
			// ?
			mde = Math.max(1, mde);
			int mdexy = target.distxy(pp);

			dd += mde + enemyGuardianDanger(target);// 80 60 40 20 0 0 0

			if (mcc != 0 && wl) {
//				if (sysOn)
//					System.out.println("here");
				if (seenOB.size() != 0) {
//					if (sysOn)
//						System.out.println("here");
					for (Ahero hero : mlHeros.values()) {
//						if (sysOn)
//							System.out.println("here");
						if (!hero.wl && from.distxy(hero.myp) <= 5)
							dd += 1000;
					}
				}
			}
//			if (phase == 5) {
//			}
//			if (sysOn)
//				System.out.println(dd);
//			if (mcc != 0) {
//				// dd -= (double) mcc / 100;// .04 .06 .08
//				dd += (double) 1.1 * 3 / 4;
//			}
			double t = 1.1;
			if (sameNuke != null) {
				dd -= (double) target.distxy(px) * t; // and here shit :)
//				if (sysOn)
//					System.out
//							.println("f1 : " + ((double) target.distxy(px) * 1.1 / 4) + " - " + ((double) 1.1 * 3 / 4));
			} else
				dd -= (double) 5 * t; // dd += sameNuke.size() * 2.0;
			// dd += damageOfEnemiesThatCanShotMeAndMyAlies(target);// here shit
			// maslan baese aghab garde alki mishe
			// dd -= Math.max(myTurnDamageFrom(target), myTurnHealFrom(target));

			if (sysOn)
				System.out.println(dd);
			if (swap != null)
				dd += 30 * dis[v(target)][v(swap)];

			if (sysOn)
				System.out.println(
						"FUCK : " + myDamageFrom(target) + " - " + damageOfEnemiesThatCanShotMeAndMyAlies(target));
//			if (po.distxy(px) > range3 && mdexy > range1)
//				dd += 50;
		}
		// System.out.println(dd);
		ev = 1000 - dd;
		return ev;
	}

	// WRONG
	private double numberOfEnemiesThatCanShotMeAndMyAlies(Point po) {
		double num = 0;
		for (Ahero hero : osHeros.values()) {
			if (hero.type == HeroName.BLASTER) {
				boolean v = false;
				if (hero.myp.distxy(po) <= hero.range1 + hero.aoe1)// +1 ?
				{
					for (Ahero hhero : mlHeros.values())
						if (hero.myp.distxy(hhero.myp) <= hero.range1 + hero.aoe1)
							v = true;
				}
				if (hero.myp.distxy(po) <= hero.range3 + hero.aoe3)// +1 ?
				{
					for (Ahero hhero : mlHeros.values())
						if (hero.myp.distxy(hhero.myp) <= hero.range3 + hero.aoe3)
							v = true;
				}
				if (v)
					++num;
			}
			if (hero.type == HeroName.GUARDIAN) {
				boolean v = false;
				if (hero.myp.distxy(po) <= hero.range1 + hero.aoe1)// +1 ?
				{
					for (Ahero hhero : mlHeros.values())
						if (hero.myp.distxy(hhero.myp) <= hero.range1 + hero.aoe1)
							v = true;
				}
				if (v)
					++num;
			}
		}
		return num;
	}

	private int disToGuardian(Point po) {
		int minn = maxInt;
		// just Guardian
		for (Ahero hero : seenOG) {
			minn = Math.min(minn, po.distxy(hero.myp));
		}
		return minn;
	}

	private int canEnemyDamageOnPoint(Point po) {
		int damage = 0;
		// just Guardian
		for (Ahero hero : seenOG) {
			if (po.distxy(hero.myp) <= hero.range1 + hero.aoe1) // am i visible ? or same nuke ? TODO
				damage += hero.pow1;
		}
		return damage;
	}

	private int canEnemyDamageOnPointXXX(Point po) {
		int damage = 0;
		// just Guardian
		for (Ahero hero : seenOG) {
			if (po.distxy(hero.myp) <= hero.range1 + hero.aoe1 + 1) // am i visible ? or same nuke ? TODO
				// move in next turn
				damage += hero.pow1;
		}
		return damage;
	}

	private void tryToMoveTo(Point bp) {

		if (bp != null) {
			if (moveCost <= realAP()) {
				movementList.add(new Movement(this, bp));
			}
		}

		if (movementList.isEmpty())
			movementList.add(new Movement(this, myp));
	}

	@Override
	public void actionTurn() {
		actionList = new ArrayList<Action>();

		if (jumpTarget != null && realAP() >= cost2) {
			actionList.add(new Action(this, a2, jumpTarget));
			jumpTarget = null;
			return;// force ?:/
		}

		actionList.add(new Action());

		// mishe kamesh karda TODO
		if (rcd1 == 0 && cost1 <= realAP())
			for (Ahero eh : osHeros.values())
				if (myp.distxy(eh.myp) <= range1 + aoe1)
					actionList.add(new Action(this, a1, eh.myp));

		if (rcd3 == 0 && cost3 <= realAP())
			for (Ahero eh : mlHeros.values())
				if (myp.distxy(eh.myp) <= range3 + aoe3 && eh.mhp != eh.maxHP)
					actionList.add(new Action(this, a3, eh.myp));

		if (actionList.size() == 0 && isReady2 && realAP() >= cost2 && mrealAP() >= cost2) {
			oneJump();
			if (jumpTarget != null)
				actionList.add(new Action(this, a2, jumpTarget));
			jumpTarget = null;
		}

	}

	private void oneJump() {
		double maxx = minInt;
		Point bp = null;
		maxx = evaluate(myp, myp);
		if (isReady2 && realAP() >= cost2) {
			for (int dx = -4; dx <= +4; ++dx)
				for (int dy = -(4 - Math.abs(dx)); dy <= 4 - Math.abs(dx); ++dy) {
					if (isInMap(myp.x + dx, myp.y + dy) && !p[myp.x + dx][myp.y + dy].isWall
							&& !p[myp.x + dx][myp.y + dy].ifull) {
						double ev = evaluate(p[myp.x + dx][myp.y + dy], myp);
						if (ev > maxx) {
							ev = maxx;
							bp = p[myp.x + dx][myp.y + dy];
						}
					}
				}
		}
		jumpTarget = bp;

	}

	@Override
	public Point getNewDodge() {
		// TODO Auto-generated method stub
		return null;
	}

}
