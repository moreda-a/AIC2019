package code;

import java.util.ArrayList;
import java.util.Stack;

import client.model.*;

public class MovementHandler extends Util {

	public static ArrayList<ArrayList<Movement>> movementLists;
	public static ArrayList<Movement> bestTurnMovement = new ArrayList<Movement>();

	public static void doTurn() {

		if (phase == 0) {
			int k = 0;
			int maxForRes = 0, minForRes = 0;
			for (Ahero hero : mlHeros.values()) {
				k++;
				if (hero.isReady3)
					maxForRes += hero.cost3;
				else if (hero.isReady1)
					maxForRes += hero.cost1;
				minForRes += hero.cost1;
			}
			if (k != 0)
				for (Ahero hero : mHeros.values()) {
					if (hero.isDead)
						continue;
					hero.mAP = maxAP / k;// or AP
					hero.wl = false;
				}

			for (Ahero hero : mlHeros.values())
				hero.swap = null;
			for (Ahero hero : mlHeros.values()) {
				if (hero.swap != null)
					continue;
				if (hero.mhp <= 35) {
					for (Ahero h : mlHeros.values()) {
						if (h == hero || h.swap != null)
							continue;
						// dis is better because we want to change places
						if (dis[v(hero.myp)][v(h.myp)] <= 6 && h.mhp >= 150 && Ahero.damageOfEnemiesOn(h.myp) <= 15) {
							h.swap = hero.myp;
							hero.swap = h.myp;
						}
					}
				}
			}
		}

		if (turn == 5 && phase == 0) {
			lockBackLine();
		}

		movementLists = new ArrayList<ArrayList<Movement>>();
		for (Ahero hero : mlHeros.values()) {
			hero.moveTurn();
			if (hero.movementList != null && hero.movementList.size() != 0)
				movementLists.add(hero.movementList);
		}

		int als = movementLists.size();
		int[] index = new int[4];
		index[0] = 0;
		index[1] = 0;
		index[2] = 0;
		index[3] = 0;
		double cta, maxx = -1;
		int c = 0;
		int alpha = 0;
		ArrayList<Movement> bestTurnMovements = null;
		while (c == 0) {
			++alpha;
			// System.out.println(++alpha + " - " + als);
			ArrayList<Movement> turnMovement = new ArrayList<Movement>();
			for (int i = 0; i < als; ++i) {
				ArrayList<Movement> al = movementLists.get(i);
				// System.out.println(al + " _ " + al.size());
				turnMovement.add(al.get(index[i]));
			}
			cta = checkTurnMovement(turnMovement);
			// System.out.println(cta + " - " + index[3] + "," + index[2] + "," + index[1] +
			// "," + index[0]);
			if (cta > maxx) {
				bestTurnMovements = turnMovement;
				maxx = cta;
			}
			c = 1;
			for (int i = 0; i < als; ++i) {
				index[i] = (index[i] + c) % movementLists.get(i).size();
				if (index[i] != 0)
					c = 0;
			}
		}
		System.out.println("Move : " + als + " - " + alpha + " - " + maxx);
		if (bestTurnMovements != null) {
			doBestMovements(bestTurnMovements);
		}

	}

	private static void lockBackLine() {
		int max1 = minInt;
		int max2 = minInt;
		Ahero m1 = null, m2 = null;
		for (Ahero hero : mflHeros.values()) {
			Stack<Point> sp = Nav.bfsToObjective2(hero.myp);
			if (sp != null) {
				int fp = sp.size();
				if (fp > max1) {
					max1 = fp;
					m1 = hero;
				} else if (fp > max2) {
					max2 = fp;
					m2 = hero;
				}
			}
		}
		if (m1 != null)
			m1.wl = true;
		if (m2 != null)
			m2.wl = true;
		System.out.println(max1 + " - " + max2);
	}

	private static double checkTurnMovement(ArrayList<Movement> turnMovement) {
		// simple damage dealt
		// sigma ev manteghiye ? (thinking ?) felan bezar
		int app = 0;
		double sev = 0;
		boolean v = true;
		for (Movement mv : turnMovement) {
			for (Movement mvv : turnMovement) {
				if (mv == mvv)
					continue;
				if (mv.targetPoint == mvv.targetPoint)
					v = false;
			}
		}
		if (v) {
			for (Movement mv : turnMovement) {
				if (mv.noMove)
					continue;
				app += mv.ahero.moveCost;
				mv.simAct();
			}

			for (Movement mv : turnMovement) {
				sev += mv.ahero.evaluate(mv.targetPoint, mv.lastPoint);
			}
			// TODO TODO TODO XXX
			// sev += ourDamage();
			sev -= damageOfEnemiesThatCanShotMeAndMyAlies();
			// if (phase == 5)
			// sev -= enemyDamage();
//			else
//				sev -= damageOfEnemiesThatCanShotMeAndMyAlies();
//			if (ourDamage() != 0)
//				System.out.println("OD: " + ourDamage());
//			if (enemyDamage() != 0)
//				System.out.println("ED: " + enemyDamage());
			for (int i = turnMovement.size() - 1; i >= 0; --i) {
				Movement mv = turnMovement.get(i);
				if (mv.noMove)
					continue;
				mv.reSimAct();
			}

			if (app <= AP) {
				return sev;
			}
		}
		return 0;
	}

	private static double enemyDamage() {
		// +heal
		double damage = 0;
		for (Ahero hero : osHeros.values()) {
			// just for seen one
			damage += Math.max(hero.enemyDamageFrom(hero.myp), hero.myHealFromO(hero.myp));
			// turn ? ? ? ? TODO
		}
		return damage;
	}

	private static double ourDamage() {
		// + heal
		double damage = 0;
		for (Ahero hero : mlHeros.values()) {
			double dd = Math.max(hero.myTurnDamageFrom(hero.myp), hero.myTurnHealFrom(hero.myp));
			damage += dd;
//			if (dd != 0 && phase != 5)
//				damage += 500;
			// TODO
			// heal is correct but damage is not correct actually hp ...
			// was not correct before too
		}
		return damage;
	}

	private static void doBestMovements(ArrayList<Movement> bestTurnMovements) {
		sscheckTurnMovement(bestTurnMovements);
		for (Movement ac : bestTurnMovements) {
			sysOn = true;
			double ev = ac.ahero.evaluate(ac.lastPoint, ac.lastPoint);
			System.out.println(ev);
			for (Direction1 dir : Direction1.values()) {
				Point po = ac.ahero.myp.dir1To(dir);
				if (po != null && !po.isWall && !po.ifull) {
					// do we really need ifull ?
					// can we calculate all together ?
					// what about planning ?
					ev = ac.ahero.evaluate(po, ac.lastPoint);
					System.out.println(ev + " - " + dir);
				}
			}
			sysOn = false;
			ac.move();
		}
	}

	private static double damageOfEnemiesThatCanShotMeAndMyAlies() {
		double damage = 0;
		for (Ahero hero : mflHeros.values()) { // no shadow
			damage += hero.damageOfEnemiesThatCanShotMeAndMyAlies(hero.myp);
		}
		return damage;
	}

	private static double sscheckTurnMovement(ArrayList<Movement> turnMovement) {
		// simple damage dealt
		// sigma ev manteghiye ? (thinking ?) felan bezar
		int app = 0;
		double sev = 0;
		boolean v = true;
		for (Movement mv : turnMovement) {
			for (Movement mvv : turnMovement) {
				if (mv == mvv)
					continue;
				if (mv.targetPoint == mvv.targetPoint)
					v = false;
			}
		}
		if (v) {
			for (Movement mv : turnMovement) {
				if (mv.noMove)
					continue;
				app += mv.ahero.moveCost;
				mv.simAct();
			}

			for (Movement mv : turnMovement) {
				sev += mv.ahero.evaluate(mv.targetPoint, mv.lastPoint);
			}
//			if (phase == 4 || phase == 5)
//				sev += ourDamage();
			System.out.println(ourDamage());
			sev -= damageOfEnemiesThatCanShotMeAndMyAlies();
			System.out.println(damageOfEnemiesThatCanShotMeAndMyAlies());
			// if (phase == 5)
			// sev -= enemyDamage();
//			else
//				sev -= damageOfEnemiesThatCanShotMeAndMyAlies();
//			if (ourDamage() != 0)
//				System.out.println("OD: " + ourDamage());
//			if (enemyDamage() != 0)
//				System.out.println("ED: " + enemyDamage());
			for (int i = turnMovement.size() - 1; i >= 0; --i) {
				Movement mv = turnMovement.get(i);
				if (mv.noMove)
					continue;
				mv.reSimAct();
			}

			if (app <= AP) {
				return sev;
			}
		}
		return 0;
	}
}
