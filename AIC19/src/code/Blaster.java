package code;

import java.util.*;

import client.model.*;

// what if i was dead ? hello ? :((( i am deadddddd :D
public class Blaster extends Ahero {
	public boolean isInDanger;
	public boolean canRunAway;
	public boolean canFight1;
	public boolean canFight3;
	public boolean w1;
	public boolean w2;
	public boolean w3;
	public Point otarget;
	public Point ntarget;
	public Point btarget = null;

	public Blaster(Hero h) {
		super(h);
		w1 = false;
		w2 = false;
		w3 = false;
	}

	@Override
	public void update() {
		updateHero();
	}

	@Override
	public void moveTurn() {
		System.out.println("Blaster: " + mid + " - " + mhp + " - " + myp);

		if (phase == 0) {
			btarget = null;
			w1 = false;
			w2 = false;
			w3 = false;
			mresAP = 0;
			mAP = maxAP / 4;
			// hello :D
			Point bp = isItGoodToJump();
			if (bp != null) {
				w2 = true;
				resAP += cost2;
				mresAP += cost2;
				btarget = bp;
			}
		}

		stateCheck();

		Point bp = chooseBestMove();
		if (bp != null) {
			if (moveCost <= realAP()) {
				moveHero(this, bp);
				usedAP += moveCost;
				mresAP += moveCost;
			}
		}
	}

	private Point isItGoodToJump() {
		int minn = 100000;
		Point bp = null;
		if (isReady2 && realAP() >= cost2) {
			for (int dx = -4; dx <= +4; ++dx)
				for (int dy = -(4 - Math.abs(dx)); dy <= 4 - Math.abs(dx); ++dy) {
					if (isInMap(myp.x + dx, myp.y + dy) && !p[myp.x + dx][myp.y + dy].isWall
							&& !p[myp.x + dx][myp.y + dy].ifull) {
						if (obdis[v(myp.x + dx, myp.y + dy)] != -1 && obdis[v(myp.x + dx, myp.y + dy)] < minn) {
							minn = obdis[v(myp.x + dx, myp.y + dy)];
							bp = p[myp.x + dx][myp.y + dy];
						}
					}
				}
			if (minn < obdis[v(myp)] - 6) {
				return bp;
				// btarget = bp;
				// w2 = true;
				// resAP += cost2;
				// mresAP += cost2;
			}
		}
		return null;
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

	// what if give all to one guradian ?
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
			if (mrealAP() < cost1 + mcc && canFight1) {
				dd += 15;
			}
			// back line idea for not getting stalk at least one level stalk
			Point pp = minDisEnemy(po);
			int mde = dis[v(po)][v(pp)];
			int mdexy = po.distxy(pp);
			int cccop = disToGuardian(po);
			int cedop = canEnemyDamageOnPoint(po);
			int cedopx = canEnemyDamageOnPointXXX(po);
			dd += mde + (double) cedop / 2 + (double) cedopx / 2;// 40 80 :|
			if (cccop != maxInt && cccop <= 2)
				dd -= 20 * cccop;
			else if (cccop != maxInt && cccop > 2)
				dd -= 40;
			if (sameNuke != null) {
				dd -= (double) po.distxy(px) * 1.1;
			} else
				dd -= 5.5; // dd += sameNuke.size() * 2.0;
		}
		// System.out.println(dd);
		ev = 1000 - dd;
		return ev;
	}

	private int disToGuardian(Point po) {
		int minn = maxInt;
		// just Guardian
		for (Ahero hero : seenOG) {
			minn = Math.min(minn, po.distxy(hero.myp));
		}
		return minn;
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

	private Point moveForward() {
		int mindis = 100000;
		Point qo = null;
		for (Ahero hh : oHeros.values()) {
			// str += myp + ":{" + hh.getCurrentCell().getColumn() + ", " +
			// hh.getCurrentCell().getRow() + "}, ";
			if (!hh.isDead && isInVision(hh.myp) && isInVision(myp, hh.myp)) {
				Point po = hh.myp;
				if (myp.distxy(po) < mindis) {
					mindis = myp.distxy(po);
					qo = po;
				}
			}
		}
		if (qo != null && myp.distxy(qo) > 4) {
			mainPath = Nav.simpleBFS2(myp, qo);
		}

		if (mainPath != null && mainPath.size() != 0)
			return mainPath.peek();

		return null;
	}

	@SuppressWarnings("unused")
	private void tryToFight() {
//		if (seenA3.size() != 0 && isReady3 && realAP() >= cost3) {
//			w3 = true;
//			resAP += cost3;
//			mresAP += cost3;
//		}
		if (!w3 && seenA1.size() != 0 && isReady1 && realAP() >= cost1) {
			w1 = true;
			resAP += cost1;
			mresAP += cost1;
		}
	}

	@SuppressWarnings("unused")
	private Point tryToRunAwayFromSameNuke() {
		int minn, maxx = -1;
		Point bp = null;

		minn = 100000;
		for (Ahero hero : mHeros.values()) {
			if (hero == this)
				continue;
			if (hero.isDead)
				continue;
			if (hero.myp.distxy(myp) < minn) {
				minn = hero.myp.distxy(myp);
			}
		}
		if (minn > maxx) {
			maxx = minn;
		}

		Point pp = moveForward();
		if (pp != null && !pp.isWall && !pp.ifull) {
			minn = 100000;
			for (Ahero hero : mHeros.values()) {
				if (hero == this)
					continue;
				if (hero.isDead)
					continue;
				if (hero.myp.distxy(pp) < minn) {
					minn = hero.myp.distxy(pp);
				}
			}
			// System.out.println("haha: " + minn);
			if (minn >= 5)
				return pp;
			if (minn > maxx) {
				maxx = minn;
				bp = pp;
			}
		}

		for (Direction1 dir : Direction1.values()) {
			Point po = myp.dir1To(dir);
			if (po != null && !po.isWall && !po.ifull) {
				minn = 100000;
				for (Ahero hero : mHeros.values()) {
					if (hero == this)
						continue;
					if (hero.isDead)
						continue;
					if (hero.myp.distxy(po) < minn) {
						minn = hero.myp.distxy(po);
					}
				}
				// System.out.println("haha: " + minn);
				if (minn >= 5)
					return po;
				if (minn > maxx) {
					maxx = minn;
					bp = po;
				}
			}
		}
		return bp;
	}

	private ArrayList<Ahero> DangerNuke(Point po) {
		ArrayList<Ahero> dn = new ArrayList<Ahero>();

		for (Ahero hero : mHeros.values()) {
			if (hero == this)
				continue;
			if (hero.isDead)
				continue;
			if (hero.myp.distxy(po) < 5) {
				dn.add(hero);
			}
		}

		if (dn.size() != 0)
			return dn;
		return null;

	}

	@SuppressWarnings("unused")
	private int enemyDamageOnPoint(Point po) {
		int damage = 0;
		for (Ahero hero : seenOB) {
			if (po.distxy(hero.myp) <= hero.range3 + hero.aoe3) // am i visible ? or same nuke ? TODO
				damage += hero.pow3;
			else if (po.distxy(hero.myp) <= hero.range1 + hero.aoe1) // am i visible ? or same nuke ? TODO
				damage += hero.pow1;
		}
		for (Ahero hero : seenOS) {
			if (isInVision(hero.myp, po)) // am i visible ? or same nuke ? or either khati ? TODO
				damage += hero.pow3;
			else if (po.distxy(hero.myp) <= hero.range1 + hero.aoe1) // am i visible ? or same nuke ? or either khati ?
				damage += hero.pow1;
		}
		for (Ahero hero : seenOH) {
			if (po.distxy(hero.myp) <= hero.range1 + hero.aoe1) // am i visible ? or same nuke ? or either khati ? TODO
				damage += hero.pow1;
		}
		for (Ahero hero : seenOG) {
			if (po.distxy(hero.myp) <= hero.range1 + hero.aoe1) // am i visible ? or same nuke ? TODO
				damage += hero.pow1;
		}
		return damage;
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

	@SuppressWarnings("unused")
	private int canDamgeFromPoint(Point po) {
		int candamage = 0;
//		Point v1 = bestTarget(a1);
//		Point v3 = bestTarget(a1);
//		if(v3!=null && isReady3)
//			candamage = 1;
		for (Ahero hero : seenO) {
			if (po.distxy(hero.myp) <= range3 + aoe3) // am i visible ? or same nuke ? TODO
				candamage = 30;
			if (po.distxy(hero.myp) <= range1 + aoe1) // am i visible ? or same nuke ? TODO
				candamage = 30;
		}
		return candamage;
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
		canFight1 = false;
		canFight3 = false;
		for (Ahero hero : oHeros.values()) {
			if (!isInVision(hero.myp))
				continue;
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
			if (myp.distxy(hero.myp) <= range3 + aoe2) {
				seenA3.add(hero);
			}
			// }
			if (myp.distxy(hero.myp) <= 7)
				isInDanger = true;
		}

		// isReady1 == true
		if (seenA1.size() != 0 && isReady1 && (realAP() >= cost1))
			canFight1 = true;
		if (seenA3.size() != 0 && isReady3 && (realAP() >= cost3))
			canFight3 = true;
		// System.out.println(isReady3);

	}

	@Override
	public void actionTurn() {
		actionList = new ArrayList<Action>();
		if (btarget != null && realAP() >= cost2) {
			actionList.add(new Action(this, a2, btarget));
			btarget = null;
			return;
		}
		Point v = null;
		// System.out.println("hi " + AP + " - " + bb.getRemCooldown());
		if (a3.getRemCooldown() == 0 && a3.getAPCost() <= realAP())
			for (Ahero eh : oHeros.values()) {
				// System.out.println(eh.getCurrentCell());
				if (eh.isInVision)
					if (myp.distxy(eh.myp) <= range3 + aoe3) {
						v = bestTarget(a3);
						actionList.add(new Action(this, a3, v));
						break;
					}
			}
		// if (v == null) {
		if (a1.getRemCooldown() == 0 && a1.getAPCost() <= realAP())
			for (Ahero eh : oHeros.values()) {
				if (eh.isInVision)
					if (myp.distxy(eh.myp) <= range1 + aoe1) {
						v = bestTarget(a1);
						actionList.add(new Action(this, a1, v));
						break;
					}
			}
		if (mresAP == 0 && mAP >= cost2 && isReady2 && realAP() >= cost2 && actionList.size() == 0) {
			oneJump();
			if (btarget != null)
				actionList.add(new Action(this, a2, btarget));
			btarget = null;
		} else
			actionList.add(new Action());
		// }

//		if (v != null)
//			usedAP += bb.getAPCost();
//
//		if (AP != world.getAP())
//			System.out.println("-------------JESUS------------");// sad no jesus here
	}

	private void oneJump() {
		double maxx = minInt;
		Point bp = null;
		maxx = evaluate(myp);
		if (isReady2 && realAP() >= cost2) {
			for (int dx = -4; dx <= +4; ++dx)
				for (int dy = -(4 - Math.abs(dx)); dy <= 4 - Math.abs(dx); ++dy) {
					if (isInMap(myp.x + dx, myp.y + dy) && !p[myp.x + dx][myp.y + dy].isWall
							&& !p[myp.x + dx][myp.y + dy].ifull) {
						double ev = evaluate(p[myp.x + dx][myp.y + dy]);
						if (ev > maxx) {
							ev = maxx;
							bp = p[myp.x + dx][myp.y + dy];
						}
					}
				}
		}
		btarget = bp;
	}

	// a1 blaster issue khati :D
	// TODO LOL?!?!
	public Point bestTarget(Ability ab) {
		int mx = -1;
		Point be = null;
		for (int dx = -ab.getRange(); dx <= ab.getRange(); ++dx) {
			for (int dy = -(ab.getRange() - Math.abs(dx)); dy <= (ab.getRange() - Math.abs(dx)); ++dy) {
				if (!isInMap(myp.x + dx, myp.y + dy) || (!ab.isLobbing() && cellToPoint(world.getImpactCell(ab,
						pointToCell(myp), pointToCell(p[myp.x + dx][myp.y + dy]))) != p[myp.x + dx][myp.y + dy]))
//				 !world.isInVision(mhero.getCurrentCell(), world.getMap().getCell(myp.y + dy, myp.x + dx))
					continue;
				int k = 0;
				for (int tx = -ab.getAreaOfEffect(); tx <= ab.getAreaOfEffect(); ++tx) {
					for (int ty = -(ab.getAreaOfEffect() - Math.abs(tx)); ty <= (ab.getAreaOfEffect()
							- Math.abs(tx)); ++ty) {
						if (!isInMap(myp.x + dx + tx, myp.y + dy + ty))
							continue;
						if (p[myp.x + dx + tx][myp.y + dy + ty].ofull)
							++k;
					}
				}
				if (k > mx) {
					be = p[myp.x + dx][myp.y + dy];
					mx = k;
				}
			}
		}
		return be;
	}

	@Override
	public Point getNewDodge() {
		if (seenO.size() == 0) {
			if (!myp.isInObjectiveZone) {
				mainPath = Nav.bfsToObjective2(myp);
			}
			ntarget = mainPath.firstElement();
			int minn = 100000;
			Point bp = null;
			// System.out.println(ntarget);
			for (int dx = -4; dx <= +4; ++dx)
				for (int dy = -(4 - Math.abs(dx)); dy <= 4 - Math.abs(dx); ++dy) {
					if (isInMap(myp.x + dx, myp.y + dy) && !p[myp.x + dx][myp.y + dy].isWall
							&& !p[myp.x + dx][myp.y + dy].ifull) {
						if (dis[v(myp.x + dx, myp.y + dy)][v(ntarget)] != -1
								&& dis[v(myp.x + dx, myp.y + dy)][v(ntarget)] < minn) {
							minn = dis[v(myp.x + dx, myp.y + dy)][v(ntarget)];
							bp = p[myp.x + dx][myp.y + dy];
						}
					}
				}

			// System.out.println(minn + " - " + dis[v(myp)][v(ntarget)] + " - " + bp);
			if (minn < dis[v(myp)][v(ntarget)] - 6) {
				// btarget = bp;
				return bp;
			}

		}
		return null;
	}

}
