package Spielverlauf;

import org.json.JSONArray;

import java.util.ArrayList;

public class Spieler {

	private int[] position;
	// private String name; wird nicht benötigt, da Daten erst am Ende eines Spiels gebraucht werden und dann direkt in den Skore gespeichert werden.
	// private int alter;
	private int leben = 3;
	private DIRECTION moveDir;
	private ArrayList<Animation> animations;
	private long fbRegeneration;
	private boolean fired;

	public Spieler(int x_pixel, int y_pixel, ArrayList<Animation> as) {

		// Position
		position = new int[2];
		position[0] = x_pixel;
		position[1] = y_pixel;

		moveDir = DIRECTION.LEFT;
		fired = false;

		//Animation
		animations = as;
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

    public void decrementLife() {
    	leben -= 1;
	}

	public Animation getAnimation(){
		Animation an = null;

		for(Animation a : animations)
			if(a.getDir() == moveDir){
				an = a;
				break;
			}
		return an;
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
}