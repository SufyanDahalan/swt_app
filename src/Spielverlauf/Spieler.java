package Spielverlauf;

import java.io.Serializable;

/**
 * Von ihr intanzierte Objekte stellen eine Spieler innerhalb eines Spielfeldes dar. Es enthält sowohl äußerliche Informationen über {@link #position Position} und Anzahl der noch vorhandnen {@link #leben Leben},
 * alsauch interne Attribute im Bezug auf Objektzustände.
 */
public class Spieler implements Serializable {

	/**	Position in Pixeln */
	private int[] position;
	/** Anzahl der aktuell vorhanden Leben. Wenn 0, dann Spieler tot. */
	private int leben = 3;
	/** Bewegungsrichtung nach der letzten Positionsänderung */
	private DIRECTION moveDir;
	/** Verbleibende Regenerationszeit des Feuerball */
	private long fbRegeneration;
	/** Zustand, ob Spieler gefeuert hat */
	private boolean fired;

	/**
	 * Erzeugt einen Spieler an einer Pixelposition x,y
	 * @param x_pixel x Position in Pixel
	 * @param y_pixel y Position in Pixel
	 */
	public Spieler(int x_pixel, int y_pixel) {

		// Position
		position = new int[2];
		position[0] = x_pixel;
		position[1] = y_pixel;

		moveDir = DIRECTION.LEFT;
		fired = false;

	}

	public int[] getPosition(){return position;}

	public void addPosOff(int x, int y){
		position[0]+=x;
		position[1]+=y;
	}

	public boolean isAlive() {
		if(leben > 0)
			return true;
		else
			return false;
	}

    public DIRECTION getMoveDir() {
        return moveDir;
    }

	public void setMoveDir(DIRECTION d) {
		moveDir = d;
	}

	public void setPosition(int[] pos) {
		position = pos;
	}

	public int getLeben() {
		return leben;
	}

    public boolean decrementLife() {
    	leben -= 1;
		return isAlive();
	}

	public void setFbRegeneration(long fbRegeneration) {
		this.fbRegeneration = fbRegeneration;
	}

    public boolean getFired() {
        return fired;
    }

	public void setFired(boolean b) {
		fired = b;
	}
	public long getRegTime(){
		return fbRegeneration;
	}

	public void decRegTime(long delay_period) {
		fbRegeneration -= delay_period;
	}

    public void incrementLife() {
		if(leben < 3)
			leben++;
    }

	public void setLeben(int l) {
		leben = l;
	}
}