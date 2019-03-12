package code;

import java.util.ArrayList;
import java.util.Stack;

import client.model.*;

public class Guardian extends Ahero {

	public boolean waitingForRay;
	public boolean waitingForAttack;
	public int rayCost;
	public int attackCost;
//	public Ability ray;
//	public Ability attack;
	public boolean isInDanger;
	public boolean canRunAway;
	public boolean canFight;

	public Point otarget;
	public Point ntarget;
	public Point btarget = null;

	public int mresAP;

	public Guardian(Hero h, boolean myTeam) {
		super(h, myTeam);
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
		System.out.println("Guardian: " + mid + " - " + mhp);
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
		Point mde = minDisEnemy(myp);
		int mded = -1;
		if (mde != null)
			mded = dis[v(myp)][v(mde)];// dis or distxy?

		Point bp = chooseBestMove();

		if (bp != null) {
			if (moveCost <= realAP() && moveCost <= mAP - mresAP && isGoodToMove(mde, mded)) {
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

	private boolean isGoodToMove(Point mde, int mded) {
		// {(6 - phase < mded - 2) || mded > 2 || mresAP <= 10 - 8)&& mresAP <= 25 - 8)
		// || (Math.min(6 - phase, mded - 2) * moveCost > realAP())
		// give AP or too far ,no enemy ,enemy to far don't save ,have AP for attack
		return (mde == null || mded > 2 || mresAP <= 10 - 8);
	}

	private Point chooseBestMove() {
		Point bp = null;
		double maxx = -100000;// set max to no move
		for (Direction1 dir : Direction1.values()) {
			Point po = myp.dir1To(dir);
			if (po != null && !po.isWall && !po.ifull) {
				// do we really need ifull ?
				// can we calculate all together ?
				// what about planning ?
				double ev = evaluate(po, myp);
				// System.out.println(ev + " - " + dir);
				if (maxx < ev) {
					maxx = ev;
					bp = po;
				}
			}
		}
		return bp;
	}

//what if give all to one guradian ? 
	@Override
	public double evaluate(Point target, Point from) {
		int ev = 0;
		int dd = 0;
		if (seenO.size() != 0) {
			Point pp = minDisEnemy(target);
			dd = dis[v(target)][v(pp)];
		} else {
			if (!target.isInObjectiveZone) {
				Stack<Point> path = Nav.bfsToObjective2(target);
				if (path != null && path.size() != 0) {
					Point pp = path.firstElement();
					dd = dis[v(target)][v(pp)];
				} else {
					// not in objective zone but no path to objective zone. Probably blocked
					// so don't go to this
					dd = 1000 + 1;// ? not move VS move
					// System.out.println("here" + path);
				}
			} else {
				// in objective zone is fine
				// System.out.println("here1");
				dd = 0;
			}
		}
		ev = 1000 - dd;
		return ev;
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

	private void stateCheck() {
		seenO = new ArrayList<Ahero>();
		seenA1 = new ArrayList<Ahero>();
		seenA3 = new ArrayList<Ahero>();
		isInDanger = false;
		canFight = false;
		for (Ahero hero : oHeros.values()) {
			if (!isInVision(hero.myp))
				continue;
			seenO.add(hero);
			if (isInVision(myp, hero.myp)) {
				seenA3.add(hero);
				if (myp.distxy(hero.myp) <= 7)
					seenA1.add(hero);
			}
			if (myp.distxy(hero.myp) <= 6)
				isInDanger = true;
		}
		if (seenA3.size() != 0 && isReady3 && (realAP() >= cost3 || waitingForRay))
			canFight = true;
		// isReady1 == true
		if (seenA1.size() != 0 && isReady1 && (realAP() >= cost1 || waitingForAttack))
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
		Point v = null;
		if (rcd1 == 0 && cost1 <= realAP()) {
			for (Ahero eh : oHeros.values()) {
				if (eh.isDead || !eh.isInVision)
					continue;
				if (myp.distxy(eh.myp) <= range1 + aoe1) {
					v = bestTarget(a1);
					// what if v == null ?
					actionList.add(new Action(this, a1, v));
					break;
				}
			}
		}

		// System.out.println("S + " + resAP + " - " + AP + " - " + usedAP);
//		if (waitingForRay == true)
//			resAP -= cost3;
//		if (waitingForAttack == true)
//			resAP -= cost1;
//		waitingForRay = false;
//		waitingForAttack = false;
//		Point v = null;
//		Ability bb = a3;
//		// System.out.println("hi " + AP + " - " + bb.getRemCooldown());
//		if (bb.getRemCooldown() == 0 && bb.getAPCost() <= realAP())
//			for (Ahero eh : oHeros.values()) {
//				// System.out.println(eh.getCurrentCell());
//				if (eh.isInVision) {
//					// a bug here btw TODO
//					// if (myp.distxy(eh.myp) <= bb.getRange()) {
//					// v = bestTarget(bb);
//					v = eh.myp;
//					actionList.add(new Action(this, bb, v));
//					break;
//					// }
//				}
//			}
//		// if (v == null) {
//		bb = a1;
//		if (bb.getRemCooldown() == 0 && bb.getAPCost() <= realAP())
//			for (Ahero eh : oHeros.values()) {
//				if (eh.isInVision)
//					if (myp.distxy(eh.myp) <= bb.getRange()) {
//						// here too TODO
//						// v = bestTarget(bb);
//						v = eh.myp;
//						actionList.add(new Action(this, bb, v));
//						break;
//					}
//			}
		// }
//		if (v != null)
//			usedAP += bb.getAPCost();

	}

	public Point bestTarget(Ability ab) {
		int mx = -1;
		Point be = null;
		for (int dx = -ab.getRange(); dx <= ab.getRange(); ++dx) {
			for (int dy = -(ab.getRange() - Math.abs(dx)); dy <= (ab.getRange() - Math.abs(dx)); ++dy) {
				if (!ab.isLobbing() && !isInMap(myp.x + dx, myp.y + dy)
						&& !world.isInVision(mhero.getCurrentCell(), world.getMap().getCell(myp.y + dy, myp.x + dx)))
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
		// TODO Auto-generated method stub
		return null;
	}
}
