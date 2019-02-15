package code;

import java.util.Random;
import java.util.Stack;

import client.model.*;

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
		// System.out.println("FF2 : " + (System.currentTimeMillis() - startTime));
		Stack<Point> path = Nav.simpleBFS2(myp, cellToPoint(
				world.getMap().getObjectiveZone()[random.nextInt(world.getMap().getObjectiveZone().length)]));
		// System.out.println("pp: " + path.size());
		// world.moveHero(hero, Direction.values()[random.nextInt(4)]);
		// System.out.println("FF3 : " + (System.currentTimeMillis() - startTime));
		// System.out.println(path.peek() + " - " + path.peek().full);
		if (path != null && path.size() != 0)
			moveHero(h, path.peek());
	}

	@Override
	public void actionHero() {
		Point v = null;
		Ability bb = h.getAbility(AbilityName.BLASTER_BOMB);
		System.out.println("hi " + AP + " - " + bb.getRemCooldown());
		if (bb.getRemCooldown() == 0 && bb.getAPCost() <= AP)
			for (Hero eh : world.getOppHeroes()) {
				//System.out.println(eh.getCurrentCell());
				if (eh.getCurrentCell() != null && eh.getCurrentCell().getColumn() != -1)
					if (myp.distxy(cellToPoint(eh.getCurrentCell())) <= bb.getRange()) {
						world.castAbility(h, bb, eh.getCurrentCell());
						v = cellToPoint(eh.getCurrentCell());
						break;
					}
			}
		if (v == null) {
			bb = h.getAbility(AbilityName.BLASTER_ATTACK);
			if (bb.getRemCooldown() == 0 && bb.getAPCost() <= AP)
				for (Hero eh : world.getOppHeroes()) {
					if (eh.getCurrentCell() != null && eh.getCurrentCell().getColumn() != -1)
						if (myp.distxy(cellToPoint(eh.getCurrentCell())) <= bb.getRange()) {
							world.castAbility(h, bb, eh.getCurrentCell());
							v = cellToPoint(eh.getCurrentCell());
							break;
						}
				}
		}
	}

}
