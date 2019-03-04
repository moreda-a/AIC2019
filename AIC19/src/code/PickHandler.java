package code;

import client.model.*;

public class PickHandler extends Util {

	public static void doTurn() {
		// world.pickHero(HeroName.values()[world.getCurrentTurn()]);
		// b3s1();
		// g4();
		// h4();
		// b2s2();
		b4();
		// b3g1();
	}

	@SuppressWarnings("unused")
	private static void h4() {
		if (turn == 0)
			nextHero = HeroName.HEALER;
		else if (turn == 1)
			nextHero = HeroName.HEALER;
		else if (turn == 2)
			nextHero = HeroName.HEALER;
		else if (turn == 3)
			nextHero = HeroName.HEALER;
		addHero();
	}

	@SuppressWarnings("unused")
	private static void g4() {
		if (turn == 0)
			nextHero = HeroName.GUARDIAN;
		else if (turn == 1)
			nextHero = HeroName.GUARDIAN;
		else if (turn == 2)
			nextHero = HeroName.GUARDIAN;
		else if (turn == 3)
			nextHero = HeroName.GUARDIAN;
		addHero();
	}

	@SuppressWarnings("unused")
	private static void b2s2() {
		if (turn == 0)
			nextHero = HeroName.BLASTER;
		else if (turn == 1)
			nextHero = HeroName.BLASTER;
		else if (turn == 2)
			nextHero = HeroName.SENTRY;
		else if (turn == 3)
			nextHero = HeroName.SENTRY;
		addHero();

	}

	@SuppressWarnings("unused")
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

	@SuppressWarnings("unused")
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

	@SuppressWarnings("unused")
	private static void b3s1() {
		if (turn == 0)
			nextHero = HeroName.BLASTER;
		else if (turn == 1)
			nextHero = HeroName.BLASTER;
		else if (turn == 2)
			nextHero = HeroName.BLASTER;
		else if (turn == 3)
			nextHero = HeroName.SENTRY;
		addHero();
	}

	@SuppressWarnings("unused")
	private static void b3g1() {
		if (turn == 0)
			nextHero = HeroName.BLASTER;
		else if (turn == 1)
			nextHero = HeroName.BLASTER;
		else if (turn == 2)
			nextHero = HeroName.BLASTER;
		else if (turn == 3)
			nextHero = HeroName.GUARDIAN;
		addHero();
	}
}
