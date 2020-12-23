package Spielbereitstellug;

import Spielverlauf.Map;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;


public class Lokalsteuerung implements ActionListener, KeyListener {

	Timer t = new Timer(5,this);
	private int x;
	private int y;
	private int velx;
	private int vely;
	private Map map;


	public Lokalsteuerung(Map m) {

		map = m;

		velx = 0;
		vely = 0;

		t.start();
        map.addKeyListener(this);
		map.setFocusable(true);
		map.setFocusTraversalKeysEnabled(false);



		// setOpaque(false); // macht den Inhalt Transparent, im Prototyp bekommt man damit die Spur weg, aber hier setzt sich die Map dann vor den Spieler

		// TODO - implement Lokalsteuerung.Lokalsteuerung
		// throw new UnsupportedOperationException();
	}

	// Der Nachfolgende Teil stammt aus dem Prototyp und muss ggf angepasst werden
	
    public void actionPerformed(ActionEvent e){
        map.getSP1().addPosOff(velx,vely);

		map.repaint();
    }


	// TODO: Randbereiche dynamisch auf Fenstergröße anpassen + Exception Cases überlegen

	public void up() {
		vely = -1;

		// throw new UnsupportedOperationException();
	}

	public void down() {
            vely = 1;
		
		// throw new UnsupportedOperationException();
	}

	public void left() {
		velx = -1;
		
		// throw new UnsupportedOperationException();
	}

	public void right() {
            velx = 1;
		
		// throw new UnsupportedOperationException();
	}

	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
        if(code == KeyEvent.VK_UP){
			// TODO: Spielerbewegung
            up();
        }
        if(code == KeyEvent.VK_DOWN){

            down();
        }
        if(code == KeyEvent.VK_LEFT){
            left();
        }
        if(code == KeyEvent.VK_RIGHT){
            right();
        }
		else{
			// Exception triggert bei jeder Richtung außer Reichts, kp warum
			// throw new UnsupportedOperationException();
		}
	}

	public void keyTyped(KeyEvent e) {
		// TODO - implement Lokalsteuerung.keyTyped
		// throw new UnsupportedOperationException();
	}

	public void keyReleased(KeyEvent e) {
		velx = 0;
		vely = 0;

		// TODO - implement Lokalsteuerung.keyReleased
		// throw new UnsupportedOperationException();
	}

}