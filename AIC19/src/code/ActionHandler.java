package code;

import java.util.ArrayList;

import javax.rmi.CORBA.PortableRemoteObjectDelegate;

import client.model.*;

public class ActionHandler extends Util {

	public static ArrayList<ArrayList<Action>> actionLists;

	public static void doTurn() {
		resAP = 0;// really ?
		actionLists = new ArrayList<ArrayList<Action>>();
		for (Ahero hero : mlHeros.values()) {
			hero.actionTurn();
			if (hero.actionList != null && hero.actionList.size() != 0)
				actionLists.add(hero.actionList);
		}

		int als = actionLists.size();
		int[] index = new int[4];
		index[0] = 0;
		index[1] = 0;
		index[2] = 0;
		index[3] = 0;
		double cta, maxx = -1;
		int c = 0;
		int alpha = 0;
		ArrayList<Action> bestTurnAction = null;
		while (c == 0) {
			++alpha;
			// System.out.println(++alpha + " - " + als);
			ArrayList<Action> turnAction = new ArrayList<Action>();
			for (int i = 0; i < als; ++i) {
				ArrayList<Action> al = actionLists.get(i);
				// System.out.println(al + " _ " + al.size());
				turnAction.add(al.get(index[i]));
			}
			cta = checkTurnAction(turnAction);
			// System.out.println(cta + " - " + index[3] + "," + index[2] + "," + index[1] +
			// "," + index[0]);
			if (cta > maxx) {
				bestTurnAction = turnAction;
				maxx = cta;
			}
			c = 1;
			for (int i = 0; i < als; ++i) {
				index[i] = (index[i] + c) % actionLists.get(i).size();
				if (index[i] != 0)
					c = 0;
			}
		}
		System.out.println("Action : " + als + " - " + alpha + " - " + maxx);
		if (bestTurnAction != null) {
			doBestActions(bestTurnAction);
		}
	}

	private static void doBestActions(ArrayList<Action> bestTurnAction) {
		for (Action ac : bestTurnAction) {
			if (ac.noAct)
				continue;
			ac.act();
		}

	}

	private static double checkTurnAction(ArrayList<Action> turnAction) {
		// simple damage dealt
		double damage = 0;
		double heal = 0;
		int app = 0;
		int cdr = 0;
		int dod = 0;
		int willDie = 0;
		int hdamage = 0;
		int noSameDamage = 0;
		int fu = 0;
		for (Action ac : turnAction) {
			if (ac.noAct)
				continue;
			app += ac.ability.getAPCost();
			if (ac.ahero.type == HeroName.SHADOW && (ac.ability.getName() == AbilityName.SHADOW_SLASH
					|| ac.ability.getName() == AbilityName.SHADOW_DODGE))
				++fu;
			if (ac.ability.getName() == AbilityName.SHADOW_SLASH) {
				actionDamgePredict(ac);
			} else if (ac.ability.getType() == AbilityType.DODGE) {
				++dod;
				continue;
			} else if (ac.ability.getType() == AbilityType.DEFENSIVE) {
				if (ac.ability.getName() == AbilityName.HEALER_HEAL)
					actionHealPredict(ac);
			} else if (ac.ability.getType() == AbilityType.OFFENSIVE) {
				actionDamgePredict(ac);
				if (ac.ability.getCooldown() != 1)
					++cdr;
			}
		}
		for (Ahero hero : osHeros.values()) {
			damage += Math.min(hero.mhp, hero.potDamage);

			if (hero.potDamage >= hero.mhp)
				++willDie;

			hdamage += hero.mhp * Math.min(hero.mhp, hero.potDamage);
			if (hero.potDamage != 0) {
				++noSameDamage;
			}
//			if (hero.type == HeroName.SENTRY)
//				damage += 0.004;
//			else if (hero.type == HeroName.HEALER)
//				damage += 0.003;
//			else if (hero.type == HeroName.BLASTER)
//				damage += 0.002;
//			else if (hero.type == HeroName.GUARDIAN)
//				damage += 0.001;
//
//			damage += (double) Math.min(hero.mhp, hero.potDamage) * Math.min(hero.mhp, hero.potDamage) * 0.0000001;// 100
//																													// 00
//
//			damage -= (double) hero.mhp * 0.001;
			hero.potDamage = 0;
		}
		for (Ahero hero : mlHeros.values()) {
			heal += Math.min(hero.maxHP - hero.mhp, hero.potHeal);
			hero.potHeal = 0;
		}
		// most kill first ? what about dodge and reposition and fortify
		// then most damage ? what about priority damage == -heal for now :)
		// priority damage on less hp people?
		// priority damage on same target
		// Nearest enemies ??
		// then less cd
		// then more dodge
		damage += fu * 100;
		// same target in one turn ?
		if (app <= AP) {
			FastMath.startNewEvaluationFunction();// start New Evaluation Function
			FastMath.addFeature(+willDie, 1, 4);
			FastMath.addFeature(+damage + heal, 5, (40 * 4 * 4) + (30 * 4));
			FastMath.addFeature(-hdamage, 5, (400 * 200 * 4));
			FastMath.addFeature(-noSameDamage, 1, 4);
			FastMath.addFeature(-cdr, 1, 4);
			FastMath.addFeature(+dod, 1, 4);
			// is it slow ?

			return FastMath.evaluationFunction();
		}
//					(double) willDie / 4 + 0.1 * damage / (40 * 4 * 4) - hdamage * 0.0001 - noSameDamage * 0.00002
//					- (double) cdr * 0.0000045 + (double) dod * 0.000001;
		return 0;

	}

	private static void actionHealPredict(Action ac) {
		Hero[] hs = world.getAbilityTargets(ac.ability, pointToCell(ac.ahero.myp), pointToCell(ac.targetPoint));
		for (Hero hero : hs) {
			Ahero ah = mHeros.get(hero.getId());
			if (ah != null) {
				ah.potHeal += ac.ability.getPower();
			}
		}
	}

	private static void actionDamgePredict(Action ac) {
		// for now use this : Hero[] getAbilityTargets(Ability,Cell, Cell);
		// might be slow!!!!!

		Hero[] hs = world.getAbilityTargets(ac.ability, pointToCell(ac.ahero.myp), pointToCell(ac.targetPoint));
		if (ac.ability.getName() == AbilityName.SENTRY_RAY && hs.length == 0)
			System.out.println("sentry ray BAD: " + ac.targetPoint + " - " + hs.length);
		if (ac.ability.getName() == AbilityName.SENTRY_ATTACK && hs.length == 0)
			System.out.println("sentry attack BAD: " + hs + " - " + hs.length);
		if (ac.ability.getName() == AbilityName.BLASTER_ATTACK && hs.length == 0)
			System.out.println("blaster attack BAD: " + hs + " - " + hs.length);
		if (ac.ability.getName() == AbilityName.BLASTER_BOMB && hs.length == 0)
			System.out.println("blaster bomb BAD: " + hs + " - " + hs.length);
		if (ac.ability.getName() == AbilityName.SHADOW_SLASH && hs.length == 0)
			System.out.println("shadow bomb BAD: " + hs + " - " + hs.length);

		for (Hero hero : hs) {
			Ahero ah = oHeros.get(hero.getId());
			if (ah != null) {
				ah.potDamage += ac.ability.getPower();
			}
		}
	}

}
