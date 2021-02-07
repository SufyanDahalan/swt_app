package Spielbereitstellug;

import Spielverlauf.DIRECTION;
import Spielverlauf.Spieler;

/***
 * Klasse zur lokalen Steuerung der Spielfigur mit den Pfeiltasten und der Leertaste
 */
public class Lokalsteuerung {

	private int velx;
	private int vely;
	private Spiel spiel;
	private boolean isHost;

	private Chat chat = null;
	private boolean isSpaceBarAvailable = true; // um Konflikt mit Chat zu verhindern

	/***
	 * Konstruktor der Lokalsteuerung
	 * @param m Spiel-Objekt, in dem die Position der Spielfigur zu finden ist
	 * @param isHost Boolean Variable zur Unterscheidung von Host und Client im Multiplayer-Modus
	 */
	public Lokalsteuerung(Spiel m, boolean isHost) {

		spiel = m;
		this.isHost = isHost;
		velx = 0;
		vely = 0;
		spiel.setFocusTraversalKeysEnabled(false);

	}

	public Lokalsteuerung(Spiel m, boolean isHost, Chat c) {

		spiel = m;
		this.isHost = isHost;
		velx = 0;
		vely = 0;
		spiel.setFocusTraversalKeysEnabled(false);

		chat = c;

	}

	/***
	 * Methode wendet Positionsveränderungen auf das Spiel an
	 */
	public void render(){
		spiel.moveSP(velx,vely, getSpieler());
	}

	/***
	 * Getter für das Spieler-Objekt
	 * @return gibt Spieler1 bzw. Spieler2 zurück
	 */
	private Spieler getSpieler(){
		if(isHost)
			return spiel.sp1;
		else
			return spiel.sp2;
	}

	/***
	 * bewegt den Spieler nach Oben
	 */
	public void up() {

		DIRECTION latestDir = getSpieler().getMoveDir();
		if (isOnCrossroad() || latestDir == DIRECTION.UP || latestDir == DIRECTION.DOWN) {
			getSpieler().setMoveDir(DIRECTION.UP);
			vely = -spiel.getSPSteps();
		}
		else
			repeatLastMove();

		render();
		nullifyDirVector();
	}

	/***
	 * bewegt den Spieler nach Unten
	 */
	public void down() {

		DIRECTION latestDir = getSpieler().getMoveDir();
		if (isOnCrossroad() || latestDir == DIRECTION.DOWN || latestDir == DIRECTION.UP) {
			getSpieler().setMoveDir(DIRECTION.DOWN);
			vely = spiel.getSPSteps();
		}
		else
			repeatLastMove();

		render();
		nullifyDirVector();
	}

	/***
	 * bewegt den Spieler nach Links
	 */
	public void left() {

		DIRECTION latestDir = getSpieler().getMoveDir();
		if (isOnCrossroad() || latestDir == DIRECTION.LEFT || latestDir == DIRECTION.RIGHT) {
			getSpieler().setMoveDir(DIRECTION.LEFT);
			velx = -spiel.getSPSteps();
		}
		else
			repeatLastMove();

		render();
		nullifyDirVector();
	}

	/***
	 * bewegt den Spieler nach Rechts
	 */
	public void right() {

		DIRECTION latestDir = getSpieler().getMoveDir();
		if (isOnCrossroad() || latestDir == DIRECTION.RIGHT || latestDir == DIRECTION.LEFT) {
			getSpieler().setMoveDir(DIRECTION.RIGHT);

			velx = spiel.getSPSteps();
		}
		else
			repeatLastMove();

		render();
		nullifyDirVector();
	}

	/***
	 * Methode zum Abfeuern eines Feuerballs
	 * Prüft zunächst ob die Leertaste gerade vom Multiplayer-Chat benutzt wird
	 */
	public void shoot(){

		if(chat != null) updateSpaceAvailable();


		if(isSpaceBarAvailable){
			if(isHost)
				spiel.spawnFeuerball(spiel.sp1);
			else {
				spiel.sp2.setFired(true);
			}

			render();
		}

	}


	private boolean isOnCrossroad() {

		int tolerance = spiel.getSPSteps()/2;
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

	/***
	 * Prüft ob die Leertaste verfügbar ist, erfragt dies beim Chat
	 */
	private void updateSpaceAvailable() {

		boolean inUse = chat.getSpaceBarUsage();

		isSpaceBarAvailable = !inUse;
	}

}
