package code;

import client.model.*;

public class PickHandler extends Util {

	public static void doTurn() {
		pickByStr("bbbb");
		// b4();
		// b3g1();
		// b2g2();
		// b1g3();
		// g4();
		// b3s1();
		// b2s2();
		// b1s3();
		// s4();
		// b3h1();
		// b2h2();
		// b1h3();
		// h4();
	}

	private static void pickByStr(String str) {
		if (str.charAt(turn) == 'b')
			nextHero = HeroName.BLASTER;
		else if (str.charAt(turn) == 's')
			nextHero = HeroName.SENTRY;
		else if (str.charAt(turn) == 'h')
			nextHero = HeroName.HEALER;
		else if (str.charAt(turn) == 'g')
			nextHero = HeroName.GUARDIAN;
		addHero();
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
	private static void b1s3() {
		if (turn == 0)
			nextHero = HeroName.BLASTER;
		else if (turn == 1)
			nextHero = HeroName.SENTRY;
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

	@SuppressWarnings("unused")
	private static void b2g2() {
		if (turn == 0)
			nextHero = HeroName.BLASTER;
		else if (turn == 1)
			nextHero = HeroName.BLASTER;
		else if (turn == 2)
			nextHero = HeroName.GUARDIAN;
		else if (turn == 3)
			nextHero = HeroName.GUARDIAN;
		addHero();
	}

	@SuppressWarnings("unused")
	private static void b1g3() {
		if (turn == 0)
			nextHero = HeroName.BLASTER;
		else if (turn == 1)
			nextHero = HeroName.GUARDIAN;
		else if (turn == 2)
			nextHero = HeroName.GUARDIAN;
		else if (turn == 3)
			nextHero = HeroName.GUARDIAN;
		addHero();
	}
}
