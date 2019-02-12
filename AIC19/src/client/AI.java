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

}
