package code;

public class Movement extends Util {

	public Ahero ahero;
	public Point targetPoint;

	public Point lastPoint;
	public boolean noMove = false;

	public Movement(Ahero ahero, Point targetPoint) {
		this.ahero = ahero;
		this.targetPoint = targetPoint;
		if (targetPoint == ahero.myp)
			noMove = true;
		lastPoint = ahero.myp;
	}

	public void move() {
		if (noMove)
			return;
		moveHero(ahero, targetPoint);
		usedAP += ahero.moveCost;
		ahero.mresAP += ahero.moveCost;
	}

	public void simAct() {
		moveSimHero(ahero, targetPoint);
	}

	public void reSimAct() {
		if (lastPoint == ahero.myp)
			return;
		moveSimHero(ahero, lastPoint);

	}
}
