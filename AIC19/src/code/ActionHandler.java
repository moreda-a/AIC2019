package code;

import java.util.ArrayList;

import client.model.*;

public class ActionHandler extends Util {

	public static ArrayList<ArrayList<Action>> actionLists;
	public static ArrayList<Action> bestTurnAction = new ArrayList<Action>();

	public static void doTurn() {
		resAP = 0;// really ?
		actionLists = new ArrayList<ArrayList<Action>>();
		for (Ahero hero : mHeros.values()) {
			if (hero.isDead)
				continue;
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
		int c = 0, cta, maxx = -1;
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
		System.out.println("alpha : " + als + " - " + alpha);
		if (bestTurnAction != null) {
			doBestActions(bestTurnAction);
		}
	}

	private static void doBestActions(ArrayList<Action> bestTurnAction) {
		for (Action ac : bestTurnAction) {
			ac.doAction();
		}

	}

	private static int checkTurnAction(ArrayList<Action> turnAction) {
		// simple damage dealt
		int damage = 0;
		int app = 0;
		for (Action ac : turnAction) {
			if (ac.isNothing)
				continue;
			app += ac.ability.getAPCost();
			if (ac.ability.getType() == AbilityType.DODGE)
				continue;
			if (ac.ability.getType() == AbilityType.OFFENSIVE) {
				actionDamgePredict(ac);
			}
		}
		for (Ahero hero : oHeros.values()) {
			damage += Math.min(hero.mhp, hero.potDamage);
			hero.potDamage = 0;
		}
		if (app <= AP)
			return damage;
		return 0;

	}

	private static void actionDamgePredict(Action ac) {
		// for now use this : Hero[] getAbilityTargets(Ability,Cell, Cell);
		// might be slow!!!!!

		Hero[] hs = world.getAbilityTargets(ac.ability, ac.ahero.mcell, ac.targetCell);
		if (ac.ability.getName() == AbilityName.SENTRY_RAY && hs.length == 0)
			System.out.println("sentry ray BAD: " + hs + " - " + hs.length);
		if (ac.ability.getName() == AbilityName.SENTRY_ATTACK && hs.length == 0)
			System.out.println("sentry attack BAD: " + hs + " - " + hs.length);
		if (ac.ability.getName() == AbilityName.BLASTER_ATTACK && hs.length == 0)
			System.out.println("blaster attack BAD: " + hs + " - " + hs.length);
		if (ac.ability.getName() == AbilityName.BLASTER_BOMB && hs.length == 0)
			System.out.println("blaster bomb BAD: " + hs + " - " + hs.length);

		for (Hero hero : hs) {
			Ahero ah = oHeros.get(hero.getId());
			if (ah != null) {
				ah.potDamage += ac.ability.getPower();
			}
		}
	}

}
