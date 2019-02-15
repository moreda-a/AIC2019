package code;

import client.model.*;

public class Manage extends Util {
	public static void preProcess(World world) {
		System.out.println(world.getFirstMoveTimeout());
		Util.init(world);// util
	}

	public static void pickTurn(World world) {
		update(world);
		Pick.doTurn();
	}

	public static void moveTurn(World world) {
		startTime = System.currentTimeMillis();
		update(world);
		System.out.println("FF : " + (System.currentTimeMillis() - startTime));
		Movement.doTurn();
		System.out.println("This turn time in client : " + (System.currentTimeMillis() - startTime));
	}

	public static void actionTurn(World world) {
		update(world);
		for (Ahero hero : mHeros) {
			hero.doTurn();
			hero.actionHero();
		}
//		Util.turn(world);
//		Hero[] heroes = world.getMyHeroes();
//		Random random = new Random();
//		Map map = world.getMap();
//		for (Hero hero : heroes) {
//			int row = random.nextInt(map.getRowNum());
//			int column = random.nextInt(map.getColumnNum());
//
//			world.castAbility(hero, hero.getAbilities()[random.nextInt(3)], row, column);
//		}
	}
}
