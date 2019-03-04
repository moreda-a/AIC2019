package code;

public class MovementHandler extends Util {

	public static void doTurn() {
		if (phase == 0) {
			int k = 0;
			int maxForRes = 0, minForRes = 0;
			for (Ahero hero : mHeros.values()) {
				if (hero.isDead)
					continue;
				k++;
				if (hero.isReady3)
					maxForRes += hero.cost3;
				else if (hero.isReady1)
					maxForRes += hero.cost1;
				minForRes += hero.cost1;
			}
			if (k != 0)
				for (Ahero hero : mHeros.values()) {
					if (hero.isDead)
						continue;
					hero.mAP = maxAP / k;// or AP
				}
		}

		for (Ahero hero : mHeros.values()) {
			if (hero.isDead)
				continue;
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
