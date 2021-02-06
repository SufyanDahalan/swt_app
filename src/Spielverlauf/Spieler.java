package Spielverlauf;

import java.io.Serializable;

public class Spieler implements Serializable {

	private int[] position;
	// private String name; wird nicht benötigt, da Daten erst am Ende eines Spiels gebraucht werden und dann direkt in den Skore gespeichert werden.
	// private int alter;
	private int leben = 3;
	private DIRECTION moveDir;
	private long fbRegeneration;
	private boolean fired;

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
		/*
    	try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		 */
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