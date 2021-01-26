package Spielverlauf;

import java.awt.image.BufferedImage;
import java.io.Serializable;

public class Feuerball implements Serializable {

	private int[] position;
	private DIRECTION dir;

	/**
	 * 
	 * @param pos
	 * @param d
	 */
	public Feuerball(int[] pos, DIRECTION d) {

		dir = d;
		position = pos.clone();
	}

	public int[] getPosition() {
		return position;
	}
	public void addPosOff(int x, int y){
		position[0]+=x;
		position[1]+=y;
	}

    public DIRECTION getMovDir() {
        return dir;
    }

    public void setMovDir(DIRECTION movDir) {
        this.dir = movDir;
    }
}