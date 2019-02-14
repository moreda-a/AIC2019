package code;

import client.model.*;

public class Pick extends Util {

	public static void doTurn(World world) {
		System.out.println("pick" + world.getCurrentTurn());
		// world.pickHero(HeroName.values()[world.getCurrentTurn()]);
		if (turn == 0)
			world.pickHero(HeroName.BLASTER);
		else
			world.pickHero(HeroName.HEALER);

	}

}
