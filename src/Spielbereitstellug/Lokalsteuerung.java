package Spielbereitstellug;

import Spielverlauf.DIRECTION;
import Spielverlauf.Spieler;

import javax.swing.*;


public class Lokalsteuerung {

	private int velx;
	private int vely;
	private Spiel spiel;
	private boolean isHost;

	public Lokalsteuerung(Spiel m, boolean isHost) {

		spiel = m;
		this.isHost = isHost;
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
		spiel.moveSP(velx,vely, getSpieler());
	}

	private Spieler getSpieler(){
		if(isHost)
			return spiel.sp1;
		else
			return spiel.sp2;
	}


	// TODO: Randbereiche dynamisch auf Fenstergröße anpassen + Exception Cases überlegen

	public void up() {

		DIRECTION latestDir = getSpieler().getMoveDir();
		if (isOnCrossroad() || latestDir == DIRECTION.UP || latestDir == DIRECTION.DOWN) {
			getSpieler().setMoveDir(DIRECTION.UP);
			vely = -getSteps();
		}
		else
			repeatLastMove();

		render();
		nullifyDirVector();
	}

	public void down() {

		DIRECTION latestDir = getSpieler().getMoveDir();
		if (isOnCrossroad() || latestDir == DIRECTION.DOWN || latestDir == DIRECTION.UP) {
			getSpieler().setMoveDir(DIRECTION.DOWN);
			vely = getSteps();
		}
		else
			repeatLastMove();

		render();
		nullifyDirVector();
	}

	public void left() {

		DIRECTION latestDir = getSpieler().getMoveDir();
		if (isOnCrossroad() || latestDir == DIRECTION.LEFT || latestDir == DIRECTION.RIGHT) {
			getSpieler().setMoveDir(DIRECTION.LEFT);
			velx = -getSteps();
		}
		else
			repeatLastMove();

		render();
		nullifyDirVector();
	}

	public void right() {

		DIRECTION latestDir = getSpieler().getMoveDir();
		if (isOnCrossroad() || latestDir == DIRECTION.RIGHT || latestDir == DIRECTION.LEFT) {
			getSpieler().setMoveDir(DIRECTION.RIGHT);

			velx = getSteps();
		}
		else
			repeatLastMove();

		render();
		nullifyDirVector();
	}

	// Methode zum Abfeuern eines Feuerballs

	public void shoot(){

		spiel.spawnFeuerball(spiel.sp1 );

		// System.out.println("FEUEEEEEER!"); // System.out.println( spiel.getFeuerball_sp1().getPosition()[0] );

		render();

	}

	private boolean isOnCrossroad() {

		int tolerance = getSteps()/2;
		int[] field_middle = spiel.getCenterOf(spiel.getFieldOf(getSpieler().getPosition()));
		int[] sp_pos = getSpieler().getPosition();

		if (field_middle[0] - tolerance <= sp_pos[0] && sp_pos[0] <= field_middle[0] + tolerance && field_middle[1] - tolerance <= sp_pos[1] && sp_pos[1] <= field_middle[1] + tolerance) {
			return true;
		}
		else {
			return false;
		}
	}

	private void repeatLastMove() {
		DIRECTION latestDir = getSpieler().getMoveDir();

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
