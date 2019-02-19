package code;

import java.util.Stack;

import client.model.Ability;
import client.model.AbilityName;
import client.model.Hero;

public class Sentry extends Ahero {

	public Sentry(Hero h) {
		super(h);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void update() {
		updateHero();
	}

	@Override
	public void moveTurn() {
		System.out.println("Sentry: " + mid + " - " + mhp);
		if (realAP() < moveCost)
			return;
		// Random random = new Random();
		// System.out.println("FF2 : " + (System.currentTimeMillis() - startTime));
//		Stack<Point> path = Nav.simpleBFS2(myp, cellToPoint(
//				world.getMap().getObjectiveZone()[random.nextInt(world.getMap().getObjectiveZone().length)]));
		Stack<Point> path = null;
		// String str = "";
		int fp = 0;
		for (Ahero hero : oHeros) {
			if (isInVision(myp, hero.myp))
				++fp;
		}
		if (myp.inObjectiveZone == false && fp == 0) {
			path = Nav.bfsToObjective2(myp);
		} else {
//			int mindis = 100000;
//			Point qo = null;
//			for (Ahero hh : oHeros) {
//				// str += myp + ":{" + hh.getCurrentCell().getColumn() + ", " +
//				// hh.getCurrentCell().getRow() + "}, ";
//				if (!hh.isDead && isInVision(hh.myp) && isInVision(myp, hh.myp)) {
//					Point po = hh.myp;
//					if (myp.distxy(po) < mindis) {
//						mindis = myp.distxy(po);
//						qo = po;
//					}
//				}
//			}
//			if (qo != null && myp.distxy(qo) > 4) {
//				path = Nav.simpleBFS2(myp, qo);
//			}
		}
		if (path != null && path.size() != 0) {
			moveHero(mhero, path.peek());
			usedAP += moveCost;
		} else {
			// System.out.println("DDDFuck ? " + str);
		}
		// System.out.println("pp: " + path.size());
		// world.moveHero(hero, Direction.values()[random.nextInt(4)]);
		// System.out.println("FF3 : " + (System.currentTimeMillis() - startTime));
		// System.out.println(path.peek() + " - " + path.peek().full);
	}

	@Override
	public void actionTurn() {
		Point v = null;
		Ability bb = mhero.getAbility(AbilityName.SENTRY_RAY);
		// System.out.println("hi " + AP + " - " + bb.getRemCooldown());
		if (bb.getRemCooldown() == 0 && bb.getAPCost() <= realAP())
			for (Ahero eh : oHeros) {
				// System.out.println(eh.getCurrentCell());
				if (eh.mcell != null && eh.mcell.getColumn() != -1) {
					// a bug here btw TODO
					// if (myp.distxy(eh.myp) <= bb.getRange()) {
					// v = bestTarget(bb);
					v = eh.myp;
					world.castAbility(mhero, bb, pointToCell(v));
					break;
					// }
				}
			}
		if (v == null) {
			bb = mhero.getAbility(AbilityName.SENTRY_ATTACK);
			if (bb.getRemCooldown() == 0 && bb.getAPCost() <= realAP())
				for (Ahero eh : oHeros) {
					if (eh.mcell != null && eh.mcell.getColumn() != -1)
						if (myp.distxy(eh.myp) <= bb.getRange()) {
							// here too TODO
							// v = bestTarget(bb);
							v = eh.myp;
							world.castAbility(mhero, bb, pointToCell(v));
							break;
						}
				}
		}
		if (v != null)
			usedAP += bb.getAPCost();

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

}
