package Spielbereitstellug;

import Spielverlauf.*;


public class Lokalsteuerung {

	private int x;
	private int y;
	private int velx;
	private int vely;
	private Map map;


	public Lokalsteuerung(int spx, int spy, Map m) {

		map = m;

		x = spx;
		y = spy;
		velx = 0;
		vely = 0;
		map.setFocusTraversalKeysEnabled(false);



//		 map.setOpaque(false); // macht den Inhalt Transparent, im Prototyp bekommt man damit die Spur weg, aber hier setzt sich die Map dann vor den Spieler
		// TODO - implement Lokalsteuerung.Lokalsteuerung
		// throw new UnsupportedOperationException();
	}


	// Der Nachfolgende Teil stammt aus dem Prototyp und muss ggf angepasst werden
	public void render(){
        map.getSP1().addPos(velx,vely);
		map.repaint();
    }


	// TODO: Randbereiche dynamisch auf Fenstergröße anpassen + Exception Cases überlegen

	public void up() {
		vely = -1;
		render();
		vely = 0;//Nullify direction vector
		// throw new UnsupportedOperationException();
	}

	public void down() {
		vely = 1;
		render();
		vely = 0;//Nullify direction vector
		// throw new UnsupportedOperationException();
	}

	public void left() {
		velx = -1;
		render();
		velx = 0;//Nullify direction vector
		// throw new UnsupportedOperationException();
	}

	public void right() {
		velx = 1;
		render();
		velx = 0;//Nullify direction vector
		// throw new UnsupportedOperationException();
	}

}