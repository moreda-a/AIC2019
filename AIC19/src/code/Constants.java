package code;

import client.model.*;

public class Constants {
	static int killScore;
	static int objectiveZoneScore;
	static int maxTurns;
	static int maxAP;
	static int maxScore;

	static int movePhaseNum;
	static Phase currentPhase;
	static int currentTurn;
	static int myScore;
	static int oppScore;
	static int AP;

	public static void setInitialConstants(World world) {
		killScore = world.getKillScore();
		objectiveZoneScore = world.getObjectiveZoneScore();
		maxTurns = world.getMaxTurns();
		maxAP = world.getMaxAP();
		maxScore = world.getMaxScore();
	}

	public static void setTurnConstants(World world) {
		movePhaseNum = world.getMovePhaseNum();
		currentPhase = world.getCurrentPhase();
		currentTurn = world.getCurrentTurn();
		myScore = world.getMyScore();
		oppScore = world.getOppScore();
		AP = world.getAP();
	}

}
