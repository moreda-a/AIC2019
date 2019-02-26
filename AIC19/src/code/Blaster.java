package code;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

import client.model.*;

// what if i was dead ? hello ? :((( i am deadddddd :D
public class Blaster extends Ahero {
	public boolean isInDanger;
	public boolean canRunAway;
	public boolean canFight;

	public Blaster(Hero h) {
		super(h);
	}

	@Override
	public void update() {
		updateHero();
	}

	@Override
	public void moveTurn() {
		System.out.println("Blaster: " + mid + " - " + mhp);
		// Random random = new Random();
		// System.out.println("FF2 : " + (System.currentTimeMillis() - startTime));
//		Stack<Point> path = Nav.simpleBFS2(myp, cellToPoint(
//				world.getMap().getObjectiveZone()[random.nextInt(world.getMap().getObjectiveZone().length)]));

		mainPath = null;
		// String str = "";

		stateCheck();
		if (isInDanger) {
			System.out.println("B isDanger");
			ArrayList<Ahero> sameNuke = DangerNuke();// now it is bullshit just check 4 range
			if (sameNuke == null) {
				System.out.println("no same");
				// TODO
			} else {
				Point po = tryToRunAwayFromSameNuke();// bullshit just see one step
				if (po != null) {
					System.out.println("po");
					if (realAP() >= moveCost) {
						moveHero(mhero, po);
						usedAP += moveCost;
					}
				} else {
					// TODO
				}
			}
		} else {
			if (canFight) {
				System.out.println("B fight");
				// TODO
			} else {
				if (myp.inObjectiveZone == false) {
					mainPath = Nav.bfsToObjective2(myp);
				} else {
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
				}
				if (mainPath != null && mainPath.size() != 0)
					if (realAP() >= moveCost) {
						moveHero(mhero, mainPath.peek());
						usedAP += moveCost;
					} else {
						// System.out.println("DDDFuck ? " + str);
					}
			}
		}

		// System.out.println("pp: " + path.size());
		// world.moveHero(hero, Direction.values()[random.nextInt(4)]);
		// System.out.println("FF3 : " + (System.currentTimeMillis() - startTime));
		// System.out.println(path.peek() + " - " + path.peek().full);
	}

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

		for (Direction1 dir : Direction1.values()) {
			Point po = myp.dir1To(dir);
			if (po != null && !po.wall && !po.ifull) {
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

	private ArrayList<Ahero> DangerNuke() {
		ArrayList<Ahero> dn = new ArrayList<Ahero>();

		for (Ahero hero : mHeros.values()) {
			if (hero == this)
				continue;
			if (hero.isDead)
				continue;
			if (hero.myp.distxy(myp) < 5) {
				dn.add(hero);
			}
		}

		if (dn.size() != 0)
			return dn;
		return null;

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
			// if (isInVision(myp, hero.myp)) {
			if (myp.distxy(hero.myp) <= 4 + 1)
				seenA1.add(hero);
			if (myp.distxy(hero.myp) <= 5 + 2)
				seenA3.add(hero);
			// }
			if (myp.distxy(hero.myp) <= 7)
				isInDanger = true;
		}

		// isReady1 == true
		if (seenA1.size() != 0 && isReady1 && (realAP() >= cost1))
			canFight = true;
		if (seenA3.size() != 0 && isReady3 && (realAP() >= cost3))
			canFight = true;
		// System.out.println(isReady3);

	}

	@Override
	public void actionTurn() {
		actionList = new ArrayList<Action>();
		actionList.add(new Action());
		Point v = null;
		Ability bb = mhero.getAbility(AbilityName.BLASTER_BOMB);
		// System.out.println("hi " + AP + " - " + bb.getRemCooldown());
		if (bb.getRemCooldown() == 0 && bb.getAPCost() <= realAP())
			for (Ahero eh : oHeros.values()) {
				// System.out.println(eh.getCurrentCell());
				if (eh.mcell != null && eh.mcell.getColumn() != -1)
					if (myp.distxy(eh.myp) <= bb.getRange()) {
						v = bestTarget(bb);
						actionList.add(new Action(this, bb, v));
						break;
					}
			}
		// if (v == null) {
		bb = mhero.getAbility(AbilityName.BLASTER_ATTACK);
		if (bb.getRemCooldown() == 0 && bb.getAPCost() <= realAP())
			for (Ahero eh : oHeros.values()) {
				if (eh.mcell != null && eh.mcell.getColumn() != -1)
					if (myp.distxy(eh.myp) <= bb.getRange()) {
						v = bestTarget(bb);
						actionList.add(new Action(this, bb, v));
						break;
					}
			}
		// }

//		if (v != null)
//			usedAP += bb.getAPCost();
//
//		if (AP != world.getAP())
//			System.out.println("-------------JESUS------------");// sad no jesus here
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
