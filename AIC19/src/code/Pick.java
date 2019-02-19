package code;

import client.model.*;

public class Pick extends Util {

	public static void doTurn() {
		// world.pickHero(HeroName.values()[world.getCurrentTurn()]);
		// b4();
		s4();
	}

	private static void s4() {
		if (turn == 0)
			nextHero = HeroName.SENTRY;
		else if (turn == 1)
			nextHero = HeroName.SENTRY;
		else if (turn == 2)
			nextHero = HeroName.SENTRY;
		else if (turn == 3)
			nextHero = HeroName.SENTRY;
		addHero();

	}

	private static void b4() {
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

}
