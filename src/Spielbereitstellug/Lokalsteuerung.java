package Spielbereitstellug;

import Spielverlauf.DIRECTION;


public class Lokalsteuerung {

	private int velx;
	private int vely;
	private Spiel map;
	private int steps = 6;

	public Lokalsteuerung(Spiel m) {

		map = m;

		velx = 0;
		vely = 0;
		map.setFocusTraversalKeysEnabled(false);



//		 map.setOpaque(false); // macht den Inhalt Transparent, im Prototyp bekommt man damit die Spur weg, aber hier setzt sich die Map dann vor den Spieler
		// TODO - implement Lokalsteuerung.Lokalsteuerung
		// throw new UnsupportedOperationException();
	}


	// Der Nachfolgende Teil stammt aus dem Prototyp und muss ggf angepasst werden

	public void render(){
		map.getSP1().addPosOff(velx,vely);
		map.repaint();
	}


	// TODO: Randbereiche dynamisch auf Fenstergröße anpassen + Exception Cases überlegen

	public void up() {
		vely = -steps;
		map.getSP1().setMoveDir(DIRECTION.UP);
		render();
		vely = 0;//Nullify direction vector
		// throw new UnsupportedOperationException();
	}

	public void down() {
		vely = steps;
		map.getSP1().setMoveDir(DIRECTION.DOWN);
		render();
		vely = 0;//Nullify direction vector
		// throw new UnsupportedOperationException();
	}

	public void left() {
		velx = -steps;
		map.getSP1().setMoveDir(DIRECTION.LEFT);
		render();
		velx = 0;//Nullify direction vector
		// throw new UnsupportedOperationException();
	}

	public void right() {
		velx = steps;
		map.getSP1().setMoveDir(DIRECTION.RIGHT);
		render();
		velx = 0;//Nullify direction vector
		// throw new UnsupportedOperationException();
	}

}