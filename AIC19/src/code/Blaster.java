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
		if (phase == 0) {
			btarget = null;
			w1 = false;
			w2 = false;
			w3 = false;
		}
		System.out.println("Blaster: " + mid + " - " + mhp);
		// Random random = new Random();
		// System.out.println("FF2 : " + (System.currentTimeMillis() - startTime));
//		Stack<Point> path = Nav.simpleBFS2(myp, cellToPoint(
//				world.getMap().getObjectiveZone()[random.nextInt(world.getMap().getObjectiveZone().length)]));

		mainPath = null;
		// String str = "";

		stateCheck();

		if (seenO.size() == 0) {// what if in danger but see no body ?
			if (!myp.inObjectiveZone) {
				mainPath = Nav.bfsToObjective2(myp);
				if (mainPath != null && mainPath.size() != 0 && !w2) {
					ntarget = mainPath.firstElement();
					int minn = 100000;
					Point bp = null;
					if (isReady2 && realAP() >= cost2) {
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
						if (minn < dis[v(myp)][v(ntarget)] - 6) {
							btarget = bp;
							w2 = true;
							resAP += cost2;
						}
					}
				} else {
					// ok now not w2 so what?
					// see no one out of objective zone and no way to get in there
				}
				if (!w2)
					if (mainPath != null && mainPath.size() != 0)
						if (realAP() >= moveCost) {
							moveHero(this, mainPath.peek());
							// change isfull
							usedAP += moveCost;
						}
			} else {
				// see no one in objective zone btw
			}
		} else {// ok we see some one what to do ?
			if (canFight) {
				if (!w1 && !w3) {
					tryToFight();
				}
			} else {
				if (w1) {
					w1 = false;
					resAP -= cost1;
				}
				if (w3) {
					w3 = false;
					resAP -= cost3;
				}
			}

			if (isInDanger) {
				System.out.println("B isDanger");

				ArrayList<Ahero> sameNuke = DangerNuke();// now it is bullshit just check 4 range

				if (sameNuke == null) {
					System.out.println("no same");
					// TODO aghab neshini ?
				} else {
					Point po = tryToRunAwayFromSameNuke();// bullshit just see one step
					if (po != null) {
						System.out.println("po");
						if (realAP() >= moveCost) {
							moveHero(this, po);
							usedAP += moveCost;
						}
					} else {
						// TODO aghab neshini ?
					}
				}
			} else {
				if (canFight) {

				} else {
					if (seenO.size() != 0) {
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
					if (mainPath == null || mainPath.size() == 0)
						if (!myp.inObjectiveZone) {
							mainPath = Nav.bfsToObjective2(myp);
						}
					if (mainPath != null && mainPath.size() != 0)
						if (realAP() >= moveCost) {
							moveHero(this, mainPath.peek());
							// change isfull

							usedAP += moveCost;
						} else {
							// System.out.println("DDDFuck ? " + str);
						}
				}

			}
		}

		// System.out.println("pp: " + path.size());
		// world.moveHero(hero, Direction.values()[random.nextInt(4)]);
		// System.out.println("FF3 : " + (System.currentTimeMillis() - startTime));
		// System.out.println(path.peek() + " - " + path.peek().full);
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

	private void tryToFight() {
		if (seenA3.size() != 0 && isReady3 && realAP() >= cost3) {
			w3 = true;
			resAP += cost3;
		}
		if (!w3 && seenA1.size() != 0 && isReady1 && realAP() >= cost1) {
			w1 = true;
			resAP += cost1;
		}
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
		if (btarget != null && realAP() >= cost2) {
			actionList.add(new Action(this, a2, btarget));
			btarget = null;
			return;
		}
		actionList.add(new Action());
		Point v = null;
		Ability bb = mhero.getAbility(AbilityName.BLASTER_BOMB);
		// System.out.println("hi " + AP + " - " + bb.getRemCooldown());
		if (bb.getRemCooldown() == 0 && bb.getAPCost() <= realAP())
			for (Ahero eh : oHeros.values()) {
				// System.out.println(eh.getCurrentCell());
				if (eh.isInVision)
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
				if (eh.isInVision)
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

	@Override
	public Point getNewDodge() {
		if (seenO.size() == 0) {
			if (!myp.inObjectiveZone) {
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
