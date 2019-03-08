package code;

import java.util.ArrayList;
import java.util.Stack;

import client.model.Ability;
import client.model.AbilityName;
import client.model.Hero;
import client.model.HeroName;

public class Sentry extends Ahero {
	public int rayCost;
	public int attackCost;
//	public Ability ray;
//	public Ability attack;
	public boolean isInDanger;
	public boolean canRunAway;
	public boolean canFight;
	public Point btarget = null;

	public Sentry(Hero h) {
		super(h);
		w1 = false;
		w2 = false;
		w3 = false;
		// rayCost = h.getAbility(AbilityName.SENTRY_RAY).getAPCost();
		// attackCost = h.getAbility(AbilityName.SENTRY_ATTACK).getAPCost();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void update() {
		updateHero();
		// ray = mhero.getAbility(AbilityName.SENTRY_RAY);
		// attack = mhero.getAbility(AbilityName.SENTRY_ATTACK);
	}

	// DoNothing(some reason)
	// MoveBackToSafty
	// MoveForwardForShot
	// MoveForwardForObejctive

	// safe?
	// good for shot
	// ap cost
	// ap!!!

	@Override
	public void moveTurn() {
		System.out.println("Sentry: " + mid + " - " + mhp);

		if (phase == 0) {
			btarget = null;
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
				btarget = bp;
			}
		}

		mainPath = null;
		// process
		stateCheck();
		System.out.println(isInDanger + " -f- " + canFight);
		// move handler
		// Random random = new Random();
		// System.out.println("FF2 : " + (System.currentTimeMillis() - startTime));
//		Stack<Point> path = Nav.simpleBFS2(myp, cellToPoint(
//		world.getMap().getObjectiveZone()[random.nextInt(world.getMap().getObjectiveZone().length)]));

//		Point bp = chooseBestMove();
//		if (bp != null) {
//			if (moveCost <= realAP()) {
//				moveHero(this, bp);
//				usedAP += moveCost;
//				mresAP += moveCost;
//			}
//		}

		if (w2)
			return;

		if (isInDanger) {
			// System.out.println("isInDanger");
			Point po = tryToRunAway();
			if (po != null) {
				if (realAP() >= moveCost || AP - usedAP >= moveCost) {
					moveHero(this, po);
					usedAP += moveCost;
					mresAP += moveCost;
				}
			} else {
				System.out.println("hereX");
				po = tryToRunAwayX();
				if (po != null) {
					if (realAP() >= moveCost) {
						moveHero(this, po);
						usedAP += moveCost;
						mresAP += moveCost;
					}
				}
				// what should i do ? :(
				// stay and fight ?
				// change position for next rounds
				// take better position
				// now stay and fight
			}
		} else {
			if (canFight) {
				// System.out.println("canFight");
				if (!w1 && !w3)
					tryToFight();
			} else {// maybe can fight but cost issue
				if (w1) {
					w1 = false;
					resAP -= cost1;
				}
				if (w3) {
					w3 = false;
					resAP -= cost3;
				}
				if (seenO.size() != 0) {
					// go to enemy(respect distance)
					int mindis = 100000;
					Point qo = null;
					for (Ahero hh : seenO) {
						// str += myp + ":{" + hh.getCurrentCell().getColumn() + ", " +
						// hh.getCurrentCell().getRow() + "}, ";
						Point po = hh.myp;
						if (myp.distxy(po) < mindis) {
							mindis = myp.distxy(po);
							qo = po;
						}
					}
					// System.out.println(qo + " -- " + realAP());
					if (qo != null && myp.distxy(qo) > 6) {
						// mainPath = Nav.simpleBFS3(myp, qo);
						// mainPath = Nav.simpleBFS2(myp, qo);
						int minn = maxInt;
						Point bp = null;
						for (Direction1 dir : Direction1.values()) {
							Point pqo = myp.dir1To(dir);
							if (pqo != null && !pqo.isWall && !pqo.ifull) {
								if (dis[v(pqo)][v(qo)] < minn) {
									minn = dis[v(pqo)][v(qo)];
									bp = pqo;
								}
							}
						}
						if (bp != null) {
							if (realAP() >= moveCost) {
								moveHero(this, bp);
								usedAP += moveCost;
								mresAP += moveCost;
							}
						}
					}

				} else {
					// System.out.println("shit");
					// go to objective zone
					if (!myp.isInObjectiveZone)
						mainPath = Nav.bfsToObjective2(myp);
					if (mainPath != null && mainPath.size() != 0) {
						if (realAP() >= moveCost) {
							moveHero(this, mainPath.peek());
							usedAP += moveCost;
							mresAP += moveCost;
						}
					}
				}

			}
		}
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

	private Point tryToRunAwayX() {
		int maxx = minInt;
		Point bp = null;
		for (Direction1 dir : Direction1.values()) {
			Point po = myp.dir1To(dir);
			if (po != null && !po.isWall && !po.ifull) {
				if (maxx < dis[v(po)][v(minDisEnemy(po))]) {
					maxx = dis[v(po)][v(minDisEnemy(po))];
					bp = po;
				}
			}
		}
		return bp;
	}

	private Point chooseBestMove() {
		Point bp = null;
		double maxx = minInt;// set max to no move TODO
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
//		int mcc = 0;
//		if (po != myp) {
//			mcc = moveCost;
//		}
//		double ev = 0;
//		double dd = 0;
//		ArrayList<Ahero> sameNuke = DangerNuke(po);
//		Point px = minDisFriend(po);
//		// now it is bullshit just check 4 range
//
//		dd += (double) mcc / 100;// .04 .06 .08
//
//		if (seenO.size() == 0) {
//			dd += obdis[v(po)]; // 0 1 2 3 ...// chand nafar nazdik manan
//			if (sameNuke != null) {
//				// if(obdis[v(po)] == 0)
//				dd -= (double) po.distxy(px) * 0.15;// no dis
//				// dd += (double) sameNuke.size() * 0.3;
//			} else
//				dd -= 0.75;
//
//			dd += (double) ordis[v(po)] * 0.06;
//		} else {
//			dd += (double) obdis[v(po)] * 0.1;
//			if (mrealAP() < cost1 + mcc && canFight1) {
//				dd += 15;
//			}
//			// back line idea for not getting stalk at least one level stalk
//			Point pp = minDisEnemy(po);
//			int mde = dis[v(po)][v(pp)];
//			int mdexy = po.distxy(pp);
//			int cccop = disToGuardian(po);
//			int cedop = canEnemyDamageOnPoint(po);
//			int cedopx = canEnemyDamageOnPointXXX(po);
//			dd += mde + (double) cedop / 2 + (double) cedopx / 2;// 40 80 :|
//			if (cccop != maxInt && cccop <= 2)
//				dd -= 20 * cccop;
//			else if (cccop != maxInt && cccop > 2)
//				dd -= 40;
//			if (sameNuke != null) {
//				dd -= (double) po.distxy(px) * 1.1;
//			} else
//				dd -= 5.5; // dd += sameNuke.size() * 2.0;
//		}
//		// System.out.println(dd);
//		ev = 1000 - dd;
//		return ev;
		return 0;
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

	private void tryToFight() {
		if (seenA3.size() != 0 && isReady3 && realAP() >= cost3) {
			w3 = true;
			resAP += cost3;
		}
		// isReady1 == true
		if (!w3 && seenA1.size() != 0 && isReady1 && realAP() >= cost1) {
			w1 = true;
			resAP += cost1;
		}

	}

	private Point tryToRunAway() {
		for (Direction1 dir : Direction1.values()) {
			Point po = myp.dir1To(dir);
			if (po != null && !po.isWall && !po.ifull) {
				boolean nsafe = false;
				for (Ahero hero : seenOB) {
					if (hero.myp.distxy(po) <= hero.range3 + hero.aoe3 - 1)
						nsafe = true;
				}
				for (Ahero hero : seenOG) {
					if (hero.myp.distxy(po) <= hero.range1 + hero.aoe1)
						nsafe = true;
				}
				for (Ahero hero : seenOH) {
					if (hero.myp.distxy(po) <= hero.range1 + hero.aoe1)
						nsafe = true;
				}
				if (!nsafe)
					return po;
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
		canFight = false;
		// canFight3 = false;
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

			if (isInVision(myp, hero.myp)) {
				seenA3.add(hero);
				if (myp.distxy(hero.myp) <= 7)
					seenA1.add(hero);
			}
			if (hero.type == HeroName.GUARDIAN)
				if (myp.distxy(hero.myp) <= hero.range1 + hero.aoe1)
					isInDanger = true;
			if (hero.type == HeroName.BLASTER)
				if (myp.distxy(hero.myp) <= hero.range3 + hero.aoe3 - 1)
					isInDanger = true;
			if (hero.type == HeroName.HEALER)
				if (myp.distxy(hero.myp) <= hero.range1 + hero.aoe1)
					isInDanger = true;
		}
		if (seenA3.size() != 0 && isReady3 && (realAP() >= cost3 || w3))
			canFight = true;
		// isReady1 == true
		if (seenA1.size() != 0 && isReady1 && (realAP() >= cost1 || w1))
			canFight = true;
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
		actionList.add(new Action());
		// System.out.println("S + " + resAP + " - " + AP + " - " + usedAP);
//		if (waitingForRay == true)
//			resAP -= cost3;
//		if (waitingForAttack == true)
//			resAP -= cost1;
		w3 = false;
		w2 = false;
		w1 = false;
		Point v = null;
		Ability bb = a3;
		// System.out.println("hi " + AP + " - " + bb.getRemCooldown());
		if (bb.getRemCooldown() == 0 && bb.getAPCost() <= realAP())
			for (Ahero eh : osHeros.values()) {
				// System.out.println(eh.getCurrentCell());
				// a bug here btw TODO
				// if (myp.distxy(eh.myp) <= bb.getRange()) {
				// v = bestTarget(bb);
				Point ta = linearShot(myp, eh.myp, bb);
				if (ta != null) {
					v = ta;
					actionList.add(new Action(this, bb, ta));
				}
				// }
			}
		// if (v == null) {
		bb = a1;
		if (bb.getRemCooldown() == 0 && bb.getAPCost() <= realAP())
			for (Ahero eh : osHeros.values()) {
				if (myp.distxy(eh.myp) <= bb.getRange()) {
					// here too TODO
					// v = bestTarget(bb);
					Point ta = linearShot(myp, eh.myp, bb);
					if (ta != null) {
						v = ta;
						actionList.add(new Action(this, bb, ta));
					}
				}
			}
		// }
//		if (v != null)
//			usedAP += bb.getAPCost();

	}

	private Point linearShot(Point startPoint, Point targetPoint, Ability ab) {
//		if (ab.getName() == AbilityName.SENTRY_ATTACK) {
//
//		} else if (ab.getName() == AbilityName.SENTRY_RAY) {
//
//		}
		if (cellToPoint(world.getImpactCell(ab, pointToCell(startPoint), pointToCell(targetPoint))) == targetPoint)
			return targetPoint;
		else {
			if (startPoint.x == targetPoint.x || startPoint.y == targetPoint.y)
				return null;
			for (int dx = 0; dx < 3; ++dx)
				for (int dy = 0; dy < 3; ++dy) {
					if (dx == 0 && dy == 0)
						continue;
					int xx = (int) Math.signum(targetPoint.x - startPoint.x) * dx + targetPoint.x;
					int yy = (int) Math.signum(targetPoint.y - startPoint.y) * dy + targetPoint.y;
					if (isInMap(xx, yy))
						if (cellToPoint(world.getImpactCell(ab, pointToCell(startPoint),
								pointToCell(p[xx][yy]))) == targetPoint)
							return p[xx][yy];
				}
			//
		}
		return null;
	}

	@Override
	public Point getNewDodge() {
		// TODO Auto-generated method stub
		return null;
	}

}
