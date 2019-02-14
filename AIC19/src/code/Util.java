package code;

import client.model.*;

public class Util extends Constants {
	// static class
	public static int turn;
	public static int phase;

	public static int size;
	public static Map mapp;
	public static int yNum;
	public static int xNum;
	public static Point[][] p;

	public static void init(World world) {
		Constants.setInitialConstants(world);
		mapp = world.getMap();
		xNum = mapp.getColumnNum();
		yNum = mapp.getRowNum();
		size = yNum * xNum;
		Cell[][] cells = mapp.getCells();
		p = new Point[xNum][yNum];
		for (int i = 0; i < xNum; ++i)
			for (int j = 0; j < yNum; ++j) {
				p[i][j] = new Point(i, j, cells[j][i]);
				// cells[j][i].isInMyRespawnZone();
			}

	}

	public static void update(World world) {
		// Constants.setTurnConstants(world);
	}

	public boolean isInMap(int x, int y) {
		return (x >= 0 && x < xNum && y >= 0 && y < yNum);
	}
}
