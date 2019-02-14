package code;

import client.model.*;

public class Pick extends Util {

	public static void doTurn() {
		// world.pickHero(HeroName.values()[world.getCurrentTurn()]);
		if (turn == 0)
			nextHero = HeroName.BLASTER;
		else
			nextHero = HeroName.HEALER;
		addHero();
	}

	private static void addHero() {
		world.pickHero(nextHero);
	}

}
