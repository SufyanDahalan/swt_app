package Spielbereitstellug;

import Spielverlauf.DIRECTION;

import javax.swing.*;


public class Lokalsteuerung {

	private int velx;
	private int vely;
	private Spiel spiel;

	public Lokalsteuerung(Spiel m) {

		spiel = m;

		velx = 0;
		vely = 0;
		spiel.setFocusTraversalKeysEnabled(false);



//		 spiel.setOpaque(false); // macht den Inhalt Transparent, im Prototyp bekommt man damit die Spur weg, aber hier setzt sich die spiel dann vor den Spieler
		// TODO - implement Lokalsteuerung.Lokalsteuerung
		// throw new UnsupportedOperationException();
	}

	public int getSteps(){
		return spiel.getFieldSize()/10;
	}

	// Der Nachfolgende Teil stammt aus dem Prototyp und muss ggf angepasst werden

	public void render(){
		spiel.moveSP(velx,vely);
		spiel.repaint();
	}


	// TODO: Randbereiche dynamisch auf Fenstergröße anpassen + Exception Cases überlegen

	public void up() {

		DIRECTION latestDir = spiel.getSP1().getMoveDir();
		if (isOnCrossroad() || latestDir == DIRECTION.UP || latestDir == DIRECTION.DOWN) {
			spiel.getSP1().setMoveDir(DIRECTION.UP);
			vely = -getSteps();
		}
		else
			repeatLastMove();

		render();
		nullifyDirVector();
	}

	public void down() {

		DIRECTION latestDir = spiel.getSP1().getMoveDir();
		if (isOnCrossroad() || latestDir == DIRECTION.DOWN || latestDir == DIRECTION.UP) {
			spiel.getSP1().setMoveDir(DIRECTION.DOWN);
			vely = getSteps();
		}
		else
			repeatLastMove();

		render();
		nullifyDirVector();
	}

	public void left() {

		DIRECTION latestDir = spiel.getSP1().getMoveDir();
		if (isOnCrossroad() || latestDir == DIRECTION.LEFT || latestDir == DIRECTION.RIGHT) {
			spiel.getSP1().setMoveDir(DIRECTION.LEFT);
			velx = -getSteps();
		}
		else
			repeatLastMove();

		render();
		nullifyDirVector();
	}

	public void right() {

		DIRECTION latestDir = spiel.getSP1().getMoveDir();
		if (isOnCrossroad() || latestDir == DIRECTION.RIGHT || latestDir == DIRECTION.LEFT) {
			spiel.getSP1().setMoveDir(DIRECTION.RIGHT);

			velx = getSteps();
		}
		else
			repeatLastMove();

		render();
		nullifyDirVector();
	}

	private boolean isOnCrossroad() {

		int tolerance = getSteps()/2;
		int[] field_middle = spiel.getCenterOf(spiel.getFieldOf(spiel.getSP1().getPosition()));
		int[] sp_pos = spiel.getSP1().getPosition();

		if (field_middle[0] - tolerance <= sp_pos[0] && sp_pos[0] <= field_middle[0] + tolerance && field_middle[1] - tolerance <= sp_pos[1] && sp_pos[1] <= field_middle[1] + tolerance) {
			return true;
		}
		else {
			return false;
		}
	}

	private void repeatLastMove() {
		DIRECTION latestDir = spiel.getSP1().getMoveDir();

		switch (latestDir){
			case UP:
				up();
				break;
			case DOWN:
				down();
				break;
			case RIGHT:
				right();
				break;
			case LEFT:
				left();
				break;
			default:
				break;
		}
	}

	private void nullifyDirVector() {
		velx = 0;
		vely = 0;
	}

}
