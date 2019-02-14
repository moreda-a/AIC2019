package code;

import java.util.Random;

import client.model.*;

public class Manage extends Util {
	public static void preProcess(World world) {
		System.out.println(world.getFirstMoveTimeout());
		// Util.init(world);// util
	}

	public static void pickTurn(World world) {
		Pick.doTurn(world);
	}

	public static void moveTurn(World world) {
		// Util.turn(world);
		Movement.moveTurn(world);
	}

	public static void actionTurn(World world) {
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
