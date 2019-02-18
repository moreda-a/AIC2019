package code;


import client.model.*;

public class Constants {
	public static int[] dx1 = { 0, 0, -1, 1 };
	public static int[] dy1 = { -1, 1, 0, 0 };

	public static World world;
	public static Map mapp;
	public static int yNum;
	public static int xNum;
	public static int size;

	static int killScore;
	static int objectiveZoneScore;
	static int maxTurns;
	static int maxAP;
	static int maxScore;

	static int phase;// movePhaseNum;
	static Phase currentPhase;
	static int turn;// currentTurn;
	static int myScore;
	static int oppScore;
	static int AP;

	public static void initConstants(World world) {
		Constants.world = world;

		mapp = world.getMap();
		xNum = mapp.getColumnNum();
		yNum = mapp.getRowNum();
		size = yNum * xNum;

		killScore = world.getKillScore();
		objectiveZoneScore = world.getObjectiveZoneScore();
		maxTurns = world.getMaxTurns();
		maxAP = world.getMaxAP();
		maxScore = world.getMaxScore();
	}

	public static void updateConstants(World world) {
		Constants.world = world;
		mapp = world.getMap();

		phase = world.getMovePhaseNum();
		currentPhase = world.getCurrentPhase();
		turn = world.getCurrentTurn();
		myScore = world.getMyScore();
		oppScore = world.getOppScore();
		AP = world.getAP();
	}

}
