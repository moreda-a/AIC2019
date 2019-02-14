package code;

import java.util.Random;
import java.util.Stack;

import client.model.Direction;
import client.model.Hero;

// what if i was dead ? hello ? :((( i am deadddddd :D
public class Blaster extends Ahero {

	public Blaster(Hero h) {
		super(h);
	}

	@Override
	public void doTurn() {
		updateHero();

	}

	@Override
	public void moveTurn() {
		System.out.println("Blaster: " + h.getId() + " - " + h.getCurrentHP());
		Random random = new Random();
		Stack<Point> path = Nav.simpleBFS(myp, cellToPoint(
				world.getMap().getObjectiveZone()[random.nextInt(world.getMap().getObjectiveZone().length)]));

		// world.moveHero(hero, Direction.values()[random.nextInt(4)]);
		if (path != null && path.size() != 0)
			moveHero(h, path.peek());

	}

}
