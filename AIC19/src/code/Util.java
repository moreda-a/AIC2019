package code;

import java.util.ArrayList;
import java.util.HashMap;

import client.model.*;

public class Util extends Constants {
	// static class
	public static int[] dx1 = { 0, 0, -1, 1 };
	public static int[] dy1 = { -1, 1, 0, 0 };
	public static int turn;
	public static int phase;

	public static int size;
	public static Map mapp;
	public static int yNum;
	public static int xNum;
	public static Point[][] p;

	public static World world;

	public static HeroName nextHero;

	public static long startTime;

	public static ArrayList<Ahero> mHeros = new ArrayList<Ahero>();
	public static ArrayList<Point> fulls = new ArrayList<Point>();

	public static HashMap<Pair<Integer, Integer>, Direction> dirs = new HashMap<Pair<Integer, Integer>, Direction>();
	// public static Pair<Integer, Integer>[][] pirs;

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

		for (int i = 0; i < 4; ++i) {
			dirs.put(new Pair<Integer, Integer>(dx1[i], dy1[i]), Direction.values()[i]);
		}

	}

	public static void update(World world) {
		System.out.println(
				"Turn: " + world.getCurrentTurn() + " Phase: " + world.getMovePhaseNum() + " AP: " + world.getAP());

		Util.world = world;
		setTurnConstants(world);
		turn = world.getCurrentTurn();
		phase = world.getMovePhaseNum();
		if (turn > 3) {
			for (Point po : fulls) {
				po.setFull(false);
			}
			fulls = new ArrayList<Point>();
			for (Hero h : world.getMyHeroes()) {
				fulls.add(p[h.getCurrentCell().getColumn()][h.getCurrentCell().getRow()]);
				p[h.getCurrentCell().getColumn()][h.getCurrentCell().getRow()].setFull(true);
			}
		}

		if (turn == 4 && phase == 0) {
			for (Hero h : world.getMyHeroes()) {
				if (h.getName() == HeroName.BLASTER)
					mHeros.add(new Blaster(h));
				else if (h.getName() == HeroName.SENTRY)
					mHeros.add(new Sentry(h));
				else if (h.getName() == HeroName.HEALER)
					mHeros.add(new Healer(h));
				else if (h.getName() == HeroName.GUARDIAN)
					mHeros.add(new Guardian(h));

			}
		}

	}

	public static boolean isInMap(int x, int y) {
		return (x >= 0 && x < xNum && y >= 0 && y < yNum);
	}

	public static int dx1(Direction1 dir) {
		return dx1[dir.ordinal()];
	}

	public static int dy1(Direction1 dir) {
		return dy1[dir.ordinal()];
	}

	public static Point cellToPoint(Cell c) {
		return p[c.getColumn()][c.getRow()];
	}

	public static void moveHero(Hero hero, Point po) {
		world.moveHero(hero, dirFromTo(cellToPoint(hero.getCurrentCell()), po));
	}

	public static Direction dirFromTo(Point fo, Point po) {
		// no new!
//		Pair<Integer, Integer> pa = new Pair<Integer, Integer>(po.x - fo.x, po.y - fo.y);
//		System.out.println((po.x - fo.x) + " - " + (po.y - fo.y));
//		System.out.println(dirs.get(pa));
		// O(4)
		for (int i = 0; i < 4; ++i)
			if (dx1[i] == po.x - fo.x && dy1[i] == po.y - fo.y)
				return Direction.values()[i];
		return null;
	}
}
