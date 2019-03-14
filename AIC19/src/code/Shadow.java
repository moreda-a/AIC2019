package code;

import java.util.ArrayList;
import java.util.HashSet;

import client.model.Ability;
import client.model.Hero;

public class Shadow extends Ahero {

	public Shadow(Hero h, boolean myTeam) {
		super(h, myTeam);
	}

	@Override
	public void update() {
		updateHero();
	}

	@Override
	public void moveTurn() {
		System.out.println("Shadow: " + mid + " - " + mhp + " - " + myp);

		if (phase == 0)
			initMoveTurn();
		startMoveTurn();

		stateCheck();

		addBestMoves();
	}

	@Override
	protected void initMoveTurn() {
		jumpTarget = null;
		beforeJumpTarget = null;
		w1 = false;
		w2 = false;
		w3 = false;
		w22 = false;
		mresAP = 0;
		// hello :D
		// better not jump 80%
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

	// what if give all to one guradian ?
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
//			if (sameNuke != null) {
//				// if(obdis[v(po)] == 0)
//				dd -= (double) target.distxy(px) * 0.15;// no dis
//				// dd += (double) sameNuke.size() * 0.3;
//			} else
//				dd -= 0.75;

			dd += (double) ordis[v(target)] * 0.06;
			if (beforeJumpTarget != null)
				dd += dis[v(target)][v(beforeJumpTarget)];
		} else {
			dd += (double) obdis[v(target)] * 0.1;
			if ((mrealAP() < cost3 + mcc || (mrealAP() + 7 < cost3 + mcc && turn != 5)) && realAP() < cost3 + mcc
					&& can3) { // this
				// mean
				// before
				dd += 15;
			}
			// back line idea for not getting stalk at least one level stalk
			Point pp = minDisEnemy(target);
			int mde = dis[v(target)][v(pp)];
			double ed = enemyDisF(target);
			int mdexy = target.distxy(pp);
//			double guardianDanger = enemyGuardianDanger(target);
//			dd += mde + guardianDanger;// 40 80 :|
			if (isReady3 || isReady2)
				dd += mde;
			else
				dd -= mde + ed;
			if (!isReady2 && !isReady3 && isInDanger && mcc == 0)
				dd += 3;
			if (!isReady3 && isReady2 && isInDanger && mcc == 0)
				dd -= 200;
//			if (mcc != 0 && wl) {
////				if (sysOn)
////					System.out.println("here");
//				if (seenOB.size() != 0) {
////					if (sysOn)
////						System.out.println("here");
//					for (Ahero hero : mlHeros.values()) {
////						if (sysOn)
////							System.out.println("here");
//						if (!hero.wl && from.distxy(hero.myp) <= 5)
//							dd += 1000;
//					}
//				}
//			}
//			if (sameNuke != null) {
//				dd -= (double) target.distxy(px) * 1.1;
//			} else
//				dd -= 5.5; // dd += sameNuke.size() * 2.0;
//			if (swap != null)
//				dd += 30 * dis[v(target)][v(swap)];
			// TODO not participate in
		}
		// System.out.println(dd);
		ev = 1000 - dd;
		return ev;
	}

	private double enemyDisF(Point po) {
		double sum = 0, count = 0;
		for (Ahero hero : ofsHeros.values()) {
			sum += po.distxy(hero.myp);
			count += 1;
		}
		if (count == 0)
			return 0;
		return sum / count;
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

		Point v = null;
		if (isReady3 && cost3 <= realAP()) {
			for (Ahero eh : osHeros.values()) {
				// System.out.println(eh.getCurrentCell());
				// a bug here btw TODO
				// if (myp.distxy(eh.myp) <= bb.getRange()) {
				// v = bestTarget(bb);

				Point ta = shadowShot(myp, eh.myp, a3);
				if (ta != null) {
					v = ta;
					actionList.add(new Action(this, a3, ta));
				}
				// }
			}
			// just get out if actionlist.size == 1 (0)
		}
		if (actionList.size() == 1 && isReady2 && cost2 <= realAP() && isInDanger) {
			oneJump();
			if (jumpTarget != null)
				actionList.add(new Action(this, a2, jumpTarget));
			jumpTarget = null;
		}
	}

	private void oneJump() {
		double maxx = minInt;
		Point bp = null;
		if (isReady2 && realAP() >= cost2) {
			for (int dx = -4; dx <= +4; ++dx)
				for (int dy = -(4 - Math.abs(dx)); dy <= 4 - Math.abs(dx); ++dy) {
					if (isInMap(myp.x + dx, myp.y + dy) && !p[myp.x + dx][myp.y + dy].isWall
							&& !p[myp.x + dx][myp.y + dy].ifull) {
						Point pf = p[myp.x + dx][myp.y + dy];
						double ev = minInt;
						Point pp = minDisFriend(pf);
						if (myp.distxy(pf) >= 3 && (pp == null || pp.distxy(pf) >= 3))
							ev = pp.distxy(pf) * 10 - myp.distxy(pf);
						if (ev > maxx) {
							ev = maxx;
							bp = p[myp.x + dx][myp.y + dy];
						}
					}
				}
		}
		jumpTarget = bp;
	}

	private Point shadowShot(Point startPoint, Point targetPoint, Ability ab) {
		// some if for not moving it self at least
		Point pp = minDisFriend(targetPoint);
		Point xq = null;
		if (startPoint.distxy(targetPoint) >= 3 && (pp == null || pp.distxy(targetPoint) >= 3))
			xq = linearShot(startPoint, targetPoint, ab);
		if (xq == null)
			xq = ggShot(startPoint, targetPoint, ab);
		return xq;
	}

	private Point ggShot(Point startPoint, Point targetPoint, Ability ab) {
		for (int dx = -3; dx <= 3; ++dx)
			for (int dy = -3; dy <= 3; ++dy) {
				if (dx == 0 && dy == 0)
					continue;
				int xx = (int) dx + targetPoint.x;
				int yy = (int) dy + targetPoint.y;
				if (isInMap(xx, yy) && !p[xx][yy].isWall) {
					Hero[] acc = world.getAbilityTargets(ab, pointToCell(startPoint), pointToCell(p[xx][yy]));
					if (acc.length != 0) {
						Point pp = minDisFriend(p[xx][yy]);
						if (startPoint.distxy(p[xx][yy]) >= 3 && (pp == null || pp.distxy(targetPoint) >= 3))
							return p[xx][yy];
					}
				}
			}

		return null;
	}
}
