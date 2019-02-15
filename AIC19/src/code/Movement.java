package code;

import client.model.*;

public class Movement extends Util {

	public static void doTurn() {
		for (Ahero hero : mHeros) {
			hero.doTurn();
			hero.moveTurn();
		}
	}

//		Random random = new Random();
//		Hero[] heroes = world.getMyHeroes();
//
//		for (Hero hero : heroes) {
//
////			switch (hero.getHeroConstants().getName()) {
////			case HEALER:
////				Healer.move();
////
////			default:
////				break;
////			}
//			if (hero.getHeroConstants().getName() == HeroName.HEALER)
//				continue;
//			Direction[] d = world.getPathMoveDirections(hero.getCurrentCell(),
//					world.getMap().getObjectiveZone()[random.nextInt(world.getMap().getObjectiveZone().length)]);
//			// world.moveHero(hero, Direction.values()[random.nextInt(4)]);
//			if (d != null && d.length != 0)
//				world.moveHero(hero, d[0]);
//		}

}
