package code;

import java.util.ArrayList;
import java.util.Stack;

import client.model.Ability;
import client.model.AbilityName;
import client.model.Hero;

public class Sentry extends Ahero {
	public boolean waitingForRay;
	public boolean waitingForAttack;
	public int rayCost;
	public int attackCost;
//	public Ability ray;
//	public Ability attack;
	public boolean isInDanger;
	public boolean canRunAway;
	public boolean canFight;

	public Sentry(Hero h) {
		super(h);
		waitingForRay = false;
		waitingForAttack = false;
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
		mainPath = null;
		// process
		stateCheck();
		// move handler
		// Random random = new Random();
		// System.out.println("FF2 : " + (System.currentTimeMillis() - startTime));
//		Stack<Point> path = Nav.simpleBFS2(myp, cellToPoint(
//				world.getMap().getObjectiveZone()[random.nextInt(world.getMap().getObjectiveZone().length)]));

		if (isInDanger) {
			// System.out.println("isInDanger");
			Point po = tryToRunAway();
			if (po != null) {
				if (realAP() >= moveCost) {
					moveHero(this, po);
					usedAP += moveCost;
				}
			} else {
				// what should i do ? :(
				// stay and fight ?
				// change position for next rounds
				// take better position
				// now stay and fight
			}
		} else {
			if (canFight) {
				// System.out.println("canFight");
				if (!waitingForAttack && !waitingForRay)
					tryToFight();
			} else {// maybe can fight but cost issue
				if (waitingForAttack) {
					waitingForAttack = false;
					resAP -= cost1;
				}
				if (waitingForRay) {
					waitingForRay = false;
					resAP -= cost3;
				}
				if (seenO.size() != 0) {
					System.out.println("here");
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
					if (qo != null && myp.distxy(qo) > 6) {
						mainPath = Nav.simpleBFS2(myp, qo);
					}
					if (mainPath != null && mainPath.size() != 0) {
						if (realAP() >= moveCost) {
							moveHero(this, mainPath.peek());
							usedAP += moveCost;
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
						}
					}
				}

			}
		}
	}

	private void tryToFight() {
		if (seenA3.size() != 0 && isReady3 && realAP() >= cost3) {
			waitingForRay = true;
			resAP += cost3;
		}
		// isReady1 == true
		if (!waitingForRay && seenA1.size() != 0 && isReady1 && realAP() >= cost1) {
			waitingForAttack = true;
			resAP += cost1;
		}

	}

	private Point tryToRunAway() {
		for (Direction1 dir : Direction1.values()) {
			Point po = myp.dir1To(dir);
			if (po != null && !po.isWall && !po.ifull) {
				boolean nsafe = false;
				for (Ahero hero : seenO) {
					if (hero.myp.distxy(po) < 7)
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
		actionList.add(new Action());
		// System.out.println("S + " + resAP + " - " + AP + " - " + usedAP);
//		if (waitingForRay == true)
//			resAP -= cost3;
//		if (waitingForAttack == true)
//			resAP -= cost1;
		waitingForRay = false;
		waitingForAttack = false;
		Point v = null;
		Ability bb = a3;
		// System.out.println("hi " + AP + " - " + bb.getRemCooldown());
		if (bb.getRemCooldown() == 0 && bb.getAPCost() <= realAP())
			for (Ahero eh : oHeros.values()) {
				// System.out.println(eh.getCurrentCell());
				if (eh.isInVision) {
					// a bug here btw TODO
					// if (myp.distxy(eh.myp) <= bb.getRange()) {
					// v = bestTarget(bb);
					v = eh.myp;
					actionList.add(new Action(this, bb, v));
					break;
					// }
				}
			}
		// if (v == null) {
		bb = a1;
		if (bb.getRemCooldown() == 0 && bb.getAPCost() <= realAP())
			for (Ahero eh : oHeros.values()) {
				if (eh.isInVision)
					if (myp.distxy(eh.myp) <= bb.getRange()) {
						// here too TODO
						// v = bestTarget(bb);
						v = eh.myp;
						actionList.add(new Action(this, bb, v));
						break;
					}
			}
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
