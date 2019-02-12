package client;

import client.model.*;
import code.*;

public class AI {

	public void preProcess(World world) {
		Manage.preProcess(world);
	}

	public void pickTurn(World world) {
		Manage.pickTurn(world);
	}

	public void moveTurn(World world) {
		Manage.moveTurn(world);

	}

	public void actionTurn(World world) {
		Manage.actionTurn(world);
	}
//    public void preProcess(World world)
//    {
//        System.out.println("pre process started");
//    }
//
//    public void pickTurn(World world)
//    {
//        System.out.println("pick started");
//        world.pickHero(HeroName.values()[world.getCurrentTurn()]);
//    }
//
//    public void moveTurn(World world)
//    {
//        System.out.println("move started");
//        Random random = new Random();
//        Hero[] heroes = world.getMyHeroes();
//
//        for (Hero hero : heroes)
//        {
//            world.moveHero(hero, Direction.values()[random.nextInt(4)]);
//        }
//    }
//
//    public void actionTurn(World world) {
//        System.out.println("action started");
//        Hero[] heroes = world.getMyHeroes();
//        Random random = new Random();
//        Map map = world.getMap();
//        for (Hero hero : heroes)
//        {
//            int row = random.nextInt(map.getRowNum());
//            int column = random.nextInt(map.getColumnNum());
//
//            world.castAbility(hero, hero.getAbilities()[random.nextInt(3)], row, column);
//        }
//    }
}
