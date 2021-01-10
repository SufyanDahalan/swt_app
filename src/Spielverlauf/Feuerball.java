package Spielverlauf;

import java.awt.image.BufferedImage;

public class Feuerball {

	private int[] position;
	private DIRECTION dir;
	private BufferedImage bild;

	/**
	 * 
	 * @param pos
	 * @param d
	 */
	public Feuerball(int[] pos, DIRECTION d, Skin sk) {

		dir = d;
		position = pos;
		bild=sk.getImage("fireball_red_f6");
	}

	public int[] getPosition() {
		return position;
	}
	public void addPosOff(int x, int y){
		position[0]+=x;
		position[1]+=y;
	}
}