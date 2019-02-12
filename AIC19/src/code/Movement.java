package code;

import java.util.Random;

import client.model.*;

public class Movement extends Util {

	public static void moveTurn(World world) {
		Random random = new Random();
		Hero[] heroes = world.getMyHeroes();

		for (Hero hero : heroes) {
			world.moveHero(hero, Direction.values()[random.nextInt(4)]);
		}
	}

}
