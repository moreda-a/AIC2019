package code;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import client.model.*;

public class Util extends Constants {
	// static class

	public static Point[][] p;
	public static Point pm1;

	public static HeroName nextHero;

	public static long startTime;

	public static HashMap<Integer, Ahero> mHeros = new HashMap<Integer, Ahero>();
	public static HashMap<Integer, Ahero> oHeros = new HashMap<Integer, Ahero>();

	public static ArrayList<Point> mfulls = new ArrayList<Point>();
	public static ArrayList<Point> ofulls = new ArrayList<Point>();

	public static ArrayList<Ahero> seenO;

	public static HashMap<Pair<Integer, Integer>, Direction> dirs = new HashMap<Pair<Integer, Integer>, Direction>();

	public static int usedAP;
	public static int resAP;
	// public static Pair<Integer, Integer>[][] pirs;

	public static void init(World world) {
		Constants.initConstants(world);
		Cell[][] cells = mapp.getCells();
		p = new Point[xNum][yNum];
		for (int i = 0; i < xNum; ++i)
			for (int j = 0; j < yNum; ++j) {
				p[i][j] = new Point(i, j, cells[j][i]);
				// cells[j][i].isInMyRespawnZone();
			}

		pm1 = null;

		for (int i = 0; i < 4; ++i) {
			dirs.put(new Pair<Integer, Integer>(dx1[i], dy1[i]), Direction.values()[i]);
		}
	}

	public static void update(World world) {
		updateConstants(world);
		System.out.println("Turn: " + turn + " Phase: " + phase + " AP: " + AP);

		usedAP = 0;
		if (phase == 0)
			resAP = 0;

		if (turn == 4 && phase == 0) {
			for (Hero h : world.getMyHeroes()) {
				if (h.getName() == HeroName.BLASTER)
					mHeros.put(h.getId(), new Blaster(h));
				else if (h.getName() == HeroName.SENTRY)
					mHeros.put(h.getId(), new Sentry(h));
				else if (h.getName() == HeroName.HEALER)
					mHeros.put(h.getId(), new Healer(h));
				else if (h.getName() == HeroName.GUARDIAN)
					mHeros.put(h.getId(), new Guardian(h));
			}
			for (Hero h : world.getOppHeroes()) {
				if (h.getName() == HeroName.BLASTER)
					oHeros.put(h.getId(), new Blaster(h));
				else if (h.getName() == HeroName.SENTRY)
					oHeros.put(h.getId(), new Sentry(h));
				else if (h.getName() == HeroName.HEALER)
					oHeros.put(h.getId(), new Healer(h));
				else if (h.getName() == HeroName.GUARDIAN)
					oHeros.put(h.getId(), new Guardian(h));
			}
		}

		if (turn > 3) {
			for (Ahero hero : mHeros.values())
				hero.update();
			for (Ahero hero : oHeros.values())
				hero.update();

			for (Point po : mfulls) {
				po.setIFull(false);
			}
			mfulls = new ArrayList<Point>();
			for (Hero h : world.getMyHeroes()) {
				if (h.getRemRespawnTime() != 0)
					continue;
				mfulls.add(p[h.getCurrentCell().getColumn()][h.getCurrentCell().getRow()]);
				p[h.getCurrentCell().getColumn()][h.getCurrentCell().getRow()].setIFull(true);
			}

			for (Point po : ofulls) {
				po.setOFull(false);
			}
			ofulls = new ArrayList<Point>();
			for (Hero h : world.getOppHeroes()) {
				if (h.getCurrentCell().isInVision()) {
					ofulls.add(p[h.getCurrentCell().getColumn()][h.getCurrentCell().getRow()]);
					p[h.getCurrentCell().getColumn()][h.getCurrentCell().getRow()].setOFull(true);
				}
			}
			seenO = new ArrayList<Ahero>();
			for (Ahero hero : oHeros.values()) {
				// check is in vision
				seenO.add(hero);
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
		if (c == null)
			return null;
		if (c.getColumn() == -1)
			return pm1;
		return p[c.getColumn()][c.getRow()];
	}

	public static Cell pointToCell(Point po) {
		// XXX what if po == null ?
		// go for shit cell ?
		// TODO!!!
		return mapp.getCell(po.y, po.x);
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

	/**
	 * The return value is true if we have vision from the start cell to end cell.
	 * The true value means that if there is an ally hero in start cell and an
	 * opponent hero in end cell, we will know he's there.
	 *
	 * @param startCell The cell we want to check the vision from
	 * @param endCell   The cell we want to check the vision to
	 * @return
	 */
//	public boolean isInVision(Cell startCell, Cell endCell) {
//		return world.isInVision(startCell, endCell);
//	}

	public boolean isInVision(Point start, Point end) {
		if (end == null || start == null)
			return false;
		return world.isInVision(pointToCell(start), pointToCell(end));
	}

//	public boolean isInVision(int startCellRow, int startCellColumn, int endCellRow, int endCellColumn) {
//		return isInVision(startCellRow, startCellColumn, endCellRow, endCellColumn);
//	}

	// can see at all ? F
	public boolean isInVision(Point po) {
		if (po == null)
			return false;
		return pointToCell(po).isInVision();
	}

	public int realAP() {
		return AP - usedAP - resAP;
	}

	public static void addHero() {
		world.pickHero(nextHero);
	}
}
