package Spielverlauf;
import org.json.JSONArray;
import Spielverlauf.*;

import java.awt.image.BufferedImage;


public class Geldsack{
	private int[] position;
	private int[] field;
	private boolean falling;
	private int fallHeight;

	public Geldsack(int[] pos) {
		position=pos;
		falling = false;
		fallHeight = 0;
		field=fp;
		bild=sk.getImage("money_static");
	}

	public void setPosition(int[] pos) {
		position = pos;
	}

	public int[] getPosition(){return position;}

	public void addPosOff(int x, int y){
		position[0]+=x;
		position[1]+=y;
	}

	public int[] getField() {
		return field;
	}

	public void setField(int[] i) {
		i = field;
	}

	public void addFieldPosOff(int x, int y) {
		field[0] += x;
		field[1] += y;
	}

	public void setFalling(boolean f) {
		falling = f;
    }


	public boolean getFalling() {
		return falling;
	}

	public void incFallHeight() {
		fallHeight++;
	}

	public int getFallHeight() {
		return fallHeight;
	}

	public void resetFallHeight() {
		fallHeight = 0;
	}
}