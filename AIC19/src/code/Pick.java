package code;

import client.model.*;

public class Pick {
	static int turnNumber = 1;

	public static void turn(World world) {
		System.out.println("pick" + turnNumber);
		// world.pickHero(HeroName.values()[world.getCurrentTurn()]);
		world.pickHero(HeroName.HEALER);
		++turnNumber;
	}

}
