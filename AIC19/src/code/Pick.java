package code;

import client.model.*;

public class Pick extends Util {

	public static void doTurn() {
		// world.pickHero(HeroName.values()[world.getCurrentTurn()]);
		if (turn == 0)
			nextHero = HeroName.BLASTER;
		else if (turn == 1)
			nextHero = HeroName.BLASTER;
		else if (turn == 2)
			nextHero = HeroName.BLASTER;
		else if (turn == 3)
			nextHero = HeroName.BLASTER;
		addHero();
	}

	private static void addHero() {
		world.pickHero(nextHero);
	}

}
