package Spielverlauf;

import java.io.Serializable;

/***
 * Klasse für Feuerball-Objekt,
 * enthölt Position und Richtung eines Feuerballs
 */
public class Feuerball implements Serializable {

	private int[] position;
	private DIRECTION dir;

	/***
	 * Konstruktor nimmt Position und Richtung entgegen
	 * @param pos Position
	 * @param d Richtung
	 */
	public Feuerball(int[] pos, DIRECTION d) {

		dir = d;
		position = pos.clone();
	}

	/***
	 * Getter für die Position eines Feuerballs
	 * @return Position pos
	 */
	public int[] getPosition() {
		return position;
	}

	/***
	 * übernimmt Positionsänderungen
	 * @param x Positionsänderung auf der X-Achse
	 * @param y Positionsänderung auf der Y-Achse
	 */
	public void addPosOff(int x, int y){
		position[0]+=x;
		position[1]+=y;
	}

	/***
	 * Getter für die Richtung eines Feuerballs
	 * @return Richtung
	 */
    public DIRECTION getMovDir() {
        return dir;
    }

	/***
	 * Setter für die Richtung
	 * @param movDir Richtung
	 */
    public void setMovDir(DIRECTION movDir) {
        this.dir = movDir;
    }
}