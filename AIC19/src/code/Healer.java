package code;

import java.util.ArrayList;
import java.util.HashSet;

import client.model.*;

public class Healer extends Ahero {

	public Healer(Hero h) {
		super(h);
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

		stateCheck();

		Point bp = chooseBestMove();
		tryToMoveTo(bp);

	}

	private void initMoveTurn() {
		jumpTarget = null;
		w1 = false;
		w2 = false;
		w3 = false;
		mresAP = 0;
		// hello :D
		Point bp = isItGoodToJump();
		if (bp != null) {
			w2 = true;
			resAP += cost2;
			mresAP += cost2;
			jumpTarget = bp;
		}
	}

	private Point isItGoodToJump() {
		int minn = 100000;
		Point bp = null;
		if (isReady2 && realAP() >= cost2) {
			for (int dx = -range2; dx <= +range2; ++dx)
				for (int dy = -(range2 - Math.abs(dx)); dy <= range2 - Math.abs(dx); ++dy) {
					if (isInMap(myp.x + dx, myp.y + dy) && !p[myp.x + dx][myp.y + dy].isWall
							&& !p[myp.x + dx][myp.y + dy].ifull) {
						if (obdis[v(myp.x + dx, myp.y + dy)] != -1 && obdis[v(myp.x + dx, myp.y + dy)] < minn) {
							minn = obdis[v(myp.x + dx, myp.y + dy)];
							bp = p[myp.x + dx][myp.y + dy];
						}
					}
				}
			if (minn < obdis[v(myp)] - mAP / moveCost) {
				return bp;
				// btarget = bp;
				// w2 = true;
				// resAP += cost2;
				// mresAP += cost2;
			}
		}
		return null;
	}

	private void stateCheck() {
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
		double maxx = -100000;// set max to no move TODO
		boolean v = false, vt = false;
		for (Direction1 dir : Direction1.values()) {
			Point po = myp.dir1To(dir);
			if (po != null && !po.isWall && po.ifull)
				v = true;
			if (po.isWall)
				vt = true;
		}
		if (!v) {
			maxx = evaluate(myp);
			if (vt && !myp.isInObjectiveZone) {
				maxx -= 0.05;
			}
			// not lock by wall
			System.out.println(maxx);
		}
		if (w2)
			maxx += 200000;
		// for no lock
		// but can lock :( TODO
		// problem with moving idiots!!!

		for (Direction1 dir : Direction1.values()) {
			Point po = myp.dir1To(dir);
			if (po != null && !po.isWall && !po.ifull) {
				// do we really need ifull ?
				// can we calculate all together ?
				// what about planning ?
				double ev = evaluate(po);
				System.out.println(ev + " - " + dir);
				if (maxx < ev) {
					maxx = ev;
					bp = po;
				}
			}
		}
		return bp;
	}

	private double evaluate(Point po) {
		int mcc = 0;
		if (po != myp) {
			mcc = moveCost;
		}
		double ev = 0;
		double dd = 0;
		ArrayList<Ahero> sameNuke = DangerNuke(po);
		Point px = minDisFriend(po);
		// now it is bullshit just check 4 range

		dd += (double) mcc / 100;// .04 .06 .08

		if (seenO.size() == 0) {
			dd += obdis[v(po)]; // 0 1 2 3 ...// chand nafar nazdik manan
			if (sameNuke != null) {
				// if(obdis[v(po)] == 0)
				dd -= (double) po.distxy(px) * 0.15;// no dis
				// dd += (double) sameNuke.size() * 0.3;
			} else
				dd -= 0.75;

			dd += (double) ordis[v(po)] * 0.06;
		} else {
			dd += (double) obdis[v(po)] * 0.1;
			if (mrealAP() < cost1 + mcc && can1) {
				dd += 15;
			}
			// back line idea for not getting stalk at least one level stalk
			Point pp = minDisEnemy(po);
			int mde = dis[v(po)][v(pp)];
			int mdexy = po.distxy(pp);
			double guardianDanger = enemyGuardianDanger(po);
//			int cccop = disToGuardian(po);
//			int cedop = canEnemyDamageOnPoint(po);
//			int cedopx = canEnemyDamageOnPointXXX(po);
			dd += mde + guardianDanger;// 40 80 :|
//			if (cccop != maxInt && cccop <= 2)
//				dd -= 20 * cccop;
//			else if (cccop != maxInt && cccop > 2)
//				dd -= 40;
			if (sameNuke != null) {
				dd -= (double) po.distxy(px) * 1.1;
			} else
				dd -= 5.5; // dd += sameNuke.size() * 2.0;
			if (po.distxy(px) > range3 && mdexy > range1)
				dd += 50;
		}
		// System.out.println(dd);
		ev = 1000 - dd;
		return ev;
	}

	private double enemyGuardianDanger(Point po) {
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

	private ArrayList<Ahero> DangerNuke(Point po) {
		ArrayList<Ahero> dn = new ArrayList<Ahero>();
		int ff = 1;
		for (Ahero hero : osHeros.values()) {
			if (hero.type == HeroName.BLASTER) {
				// phase = 5
				ff = 5;
				if (po.distxy(hero.myp) <= 7 + (6 - phase))
					ff = 5;
			}
		}
		for (Ahero hero : mlHeros.values()) {
			if (hero == this)
				continue;
			if (hero.myp.distxy(po) < ff) {
				dn.add(hero);
			}
		}

		if (dn.size() != 0)
			return dn;
		return null;

	}

	// return pathDis not realDis
	private Point minDisFriend(Point po) {
		int minn = 100000;
		Point bp = null;

		for (Ahero hero : mHeros.values()) {
			if (hero == this)
				continue;
			if (hero.isDead)
				continue;
			if (dis[v(po)][v(hero.myp)] < minn) {
				minn = dis[v(po)][v(hero.myp)];
				bp = hero.myp;
			}
		}
		return bp;
	}

	// return pathDis not realDis
	private Point minDisEnemy(Point po) {
		int minn = 100000;
		Point bp = null;
		for (Ahero hero : oHeros.values()) {
			if (hero.isDead || !hero.isInVision)
				continue;
			if (dis[v(po)][v(hero.myp)] < minn) {
				minn = dis[v(po)][v(hero.myp)];
				bp = hero.myp;
			}
		}
		return bp;
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
				moveHero(this, bp);
				usedAP += moveCost;
				mresAP += moveCost;
			}
		}
	}

	@Override
	public void actionTurn() {
		actionList = new ArrayList<Action>();

		if (jumpTarget != null && realAP() >= cost2) {
			actionList.add(new Action(this, a2, jumpTarget));
			jumpTarget = null;
			return;
		}
		actionList.add(new Action());

		// mishe kamesh karda TODO
		if (rcd1 == 0 && cost1 <= realAP())
			for (Ahero eh : osHeros.values())
				if (myp.distxy(eh.myp) <= range1 + aoe1)
					actionList.add(new Action(this, a1, eh.myp));

		if (rcd3 == 0 && cost3 <= realAP())
			for (Ahero eh : mlHeros.values())
				if (myp.distxy(eh.myp) <= range3 + aoe3)
					actionList.add(new Action(this, a3, eh.myp));

//		if (mresAP == 0 && mAP >= cost2 && isReady2 && realAP() >= cost2 && actionList.size() == 0) {
//			oneJump();
//			if (jumpTarget != null)
//				actionList.add(new Action(this, a2, jumpTarget));
//			jumpTarget = null;
//		} else
//			actionList.add(new Action());
		// }

	}

	@Override
	public Point getNewDodge() {
		// TODO Auto-generated method stub
		return null;
	}

}
