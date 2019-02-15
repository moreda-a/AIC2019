package code;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class Nav extends Util {
	public static Stack<Point> simpleBFS(Point start, Point end) {// just not first block
		// bfs order for search E = 4 * 961
		// System.out.println(start + " - " + end);
		Point it, ie, ic;
		Stack<Point> path = new Stack<Point>();
		HashMap<Point, Point> parent = new HashMap<Point, Point>();
		Queue<Point> que = new LinkedList<Point>();
		que.add(start);
		parent.put(start, null);

		while (!que.isEmpty()) {
			it = que.poll();
			for (Direction1 dir : Direction1.values()) {
				ie = it.dir1To(dir);
				if (ie != null && !parent.containsKey(ie) && !ie.wall && (!ie.full || start != it)) {
					que.add(ie);
					parent.put(ie, it);
					if (ie == end) {
						ic = ie;
						while (parent.get(ic) != null) {
							path.push(ic);
							ic = parent.get(ic);
						}
						return path;
					}
				}
			}
		}
		return null;
	}

	public static Stack<Point> simpleBFS2(Point start, Point end) {// just not first block
		// hero == wall
		// bfs order for search E = 4 * 961
		// System.out.println(start + " - " + end);
		Point it, ie, ic;
		Stack<Point> path = new Stack<Point>();
		HashMap<Point, Point> parent = new HashMap<Point, Point>();
		Queue<Point> que = new LinkedList<Point>();
		que.add(start);
		parent.put(start, null);

		while (!que.isEmpty()) {
			it = que.poll();
			for (Direction1 dir : Direction1.values()) {
				ie = it.dir1To(dir);
				if (ie != null && !parent.containsKey(ie) && !ie.wall && !ie.full) {
					que.add(ie);
					parent.put(ie, it);
					if (ie == end) {
						ic = ie;
						while (parent.get(ic) != null) {
							path.push(ic);
							ic = parent.get(ic);
						}
						return path;
					}
				}
			}
		}
		return null;
	}
}