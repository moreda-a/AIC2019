package code;

import client.model.*;

public class Manage extends Util {
	public static void preProcess(World world) {
		startTime = System.currentTimeMillis();
		init(world);
		System.out.println("This preprocess time in client : " + (System.currentTimeMillis() - startTime));
	}

	public static void pickTurn(World world) {
		startTime = System.currentTimeMillis();
		update(world);
		PickHandler.doTurn();
		System.out.println("This pick turn time in client : " + (System.currentTimeMillis() - startTime));
	}

	public static void moveTurn(World world) {
		startTime = System.currentTimeMillis();
		update(world);
		MovementHandler.doTurn();
		System.out.println("This move turn time in client : " + (System.currentTimeMillis() - startTime));
	}

	public static void actionTurn(World world) {
		startTime = System.currentTimeMillis();
		update(world);
		ActionHandler.doTurn();
		System.out.println("This action turn time in client : " + (System.currentTimeMillis() - startTime));

	}
}
