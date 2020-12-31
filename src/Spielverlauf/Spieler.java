package Spielverlauf;

import org.json.JSONArray;

public class Spieler {

	private int[] position;
	private String name;
	private int alter;
	private int leben = 3;
	private DIRECTION moveDir;

	public Spieler(int x_pixel, int y_pixel) {

		position = new int[2];

		position[0] = x_pixel;
		position[1] = y_pixel;

		moveDir = DIRECTION.LEFT;
	}

	public JSONArray sterben() {
		// TODO - implement Spieler.sterben
		throw new UnsupportedOperationException();
	}

	public boolean sichBewegen() {
		// TODO - implement Spieler.sichBewegen
		throw new UnsupportedOperationException();
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
}