package code;

import java.util.Random;
import java.util.Stack;

import client.model.*;

// what if i was dead ? hello ? :((( i am deadddddd :D
public class Blaster extends Ahero {

	public Blaster(Hero h) {
		super(h);
	}

	@Override
	public void update() {
		updateHero();
	}

	@Override
	public void moveTurn() {
		System.out.println("Blaster: " + mhero.getId() + " - " + mhero.getCurrentHP());
		// Random random = new Random();
		// System.out.println("FF2 : " + (System.currentTimeMillis() - startTime));
//		Stack<Point> path = Nav.simpleBFS2(myp, cellToPoint(
//				world.getMap().getObjectiveZone()[random.nextInt(world.getMap().getObjectiveZone().length)]));
		Stack<Point> path = null;
		// String str = "";
		if (myp.inObjectiveZone == false) {
			path = Nav.bfsToObjective(myp);
		} else {
			int mindis = 100000;
			Point qo = null;
			for (Ahero hh : oHeros) {
				// str += myp + ":{" + hh.getCurrentCell().getColumn() + ", " +
				// hh.getCurrentCell().getRow() + "}, ";
				if (isInVision(hh.myp) && isInVision(myp, hh.myp)) {
					Point po = hh.myp;
					if (myp.distxy(po) < mindis) {
						mindis = myp.distxy(po);
						qo = po;
					}
				}
			}
			if (qo != null && myp.distxy(qo) > 4) {
				path = Nav.simpleBFS2(myp, qo);
			}
		}
		if (path != null && path.size() != 0)
			moveHero(mhero, path.peek());
		else {
			// System.out.println("DDDFuck ? " + str);
		}
		// System.out.println("pp: " + path.size());
		// world.moveHero(hero, Direction.values()[random.nextInt(4)]);
		// System.out.println("FF3 : " + (System.currentTimeMillis() - startTime));
		// System.out.println(path.peek() + " - " + path.peek().full);
	}

	@Override
	public void actionHero() {
		Point v = null;
		Ability bb = mhero.getAbility(AbilityName.BLASTER_BOMB);
		System.out.println("hi " + AP + " - " + bb.getRemCooldown());
		if (bb.getRemCooldown() == 0 && bb.getAPCost() <= AP)
			for (Hero eh : world.getOppHeroes()) {
				// System.out.println(eh.getCurrentCell());
				if (eh.getCurrentCell() != null && eh.getCurrentCell().getColumn() != -1)
					if (myp.distxy(cellToPoint(eh.getCurrentCell())) <= bb.getRange()) {
						v = bestTarget(bb);
						world.castAbility(mhero, bb, pointToCell(v));
						break;
					}
			}
		if (v == null) {
			bb = mhero.getAbility(AbilityName.BLASTER_ATTACK);
			if (bb.getRemCooldown() == 0 && bb.getAPCost() <= AP)
				for (Hero eh : world.getOppHeroes()) {
					if (eh.getCurrentCell() != null && eh.getCurrentCell().getColumn() != -1)
						if (myp.distxy(cellToPoint(eh.getCurrentCell())) <= bb.getRange()) {
							v = bestTarget(bb);
							world.castAbility(mhero, bb, pointToCell(v));
							break;
						}
				}
		}
		if (AP != world.getAP())
			System.out.println("-------------JESUS------------");// sad no jesus here
	}

	public Point bestTarget(Ability ab) {
		int mx = -1;
		Point be = null;
		for (int dx = -ab.getRange(); dx <= ab.getRange(); ++dx) {
			for (int dy = -(ab.getRange() - Math.abs(dx)); dy <= (ab.getRange() - Math.abs(dx)); ++dy) {
				if (!ab.isLobbing()
						&& !world.isInVision(mhero.getCurrentCell(), world.getMap().getCell(myp.y + dy, myp.x + dx)))
					continue;
				int k = 0;
				for (int tx = -ab.getAreaOfEffect(); tx <= ab.getAreaOfEffect(); ++tx) {
					for (int ty = -(ab.getAreaOfEffect() - Math.abs(tx)); ty <= (ab.getAreaOfEffect()
							- Math.abs(tx)); ++ty) {
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
