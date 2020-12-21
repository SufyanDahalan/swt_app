package Spielbereitstellug;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;


public class Lokalsteuerung extends JPanel implements ActionListener, KeyListener {

	Timer t = new Timer(5,this);
	private double x;
	private double y;
	private double velx;
	private double vely;


	public Lokalsteuerung(int spx, int spy) {

		x = spx;
		y = spy;
		velx = 0;
		vely = 0;

		t.start();
        addKeyListener(this);
		setFocusable(true);
		setFocusTraversalKeysEnabled(false);



		// setOpaque(false); // macht den Inhalt Transparent, im Prototyp bekommt man damit die Spur weg, aber hier setzt sich die Map dann vor den Spieler

		// TODO - implement Lokalsteuerung.Lokalsteuerung
		// throw new UnsupportedOperationException();
	}

	// Der Nachfolgende Teil stammt aus dem Prototyp und muss ggf angepasst werden

	public void paintComponent(Graphics g){
        //super.paintComponent(g); // Mit dem Befehl: grauer Hintergrund überschreibt Map; Ohne den Befehl: Snake Modus
			// Problem: paintComponent radiert Inhalt vor dem Neuzeichnen: Hierbei wird auch die ganze Map radiert

        Graphics2D g2 = (Graphics2D) g;
        g2.fill(new RoundRectangle2D.Double(x,y,40,40,3,3));

	}
	
    public void actionPerformed(ActionEvent e){
        x+=velx;
        y+=vely;

		repaint();
    }


	// TODO: Randbereiche dynamisch auf Fenstergröße anpassen + Exception Cases überlegen

	public void up() {
		if (y<5){ vely = 0;}
        else{
            vely = -2;
        }

		// throw new UnsupportedOperationException();
	}

	public void down() {
		if (y>700){ vely = 0;}
        else{
            vely = 2;
		}
		
		// throw new UnsupportedOperationException();
	}

	public void left() {
		if (x<5){ velx = 0;}
        else{
            velx = -2;
		}
		
		// throw new UnsupportedOperationException();
	}

	public void right() {
		if (x>900){ velx = 0;}
        else{
            velx = 2;
		}
		
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