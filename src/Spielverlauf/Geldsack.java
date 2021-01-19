package Spielverlauf;
import org.json.JSONArray;
import Spielverlauf.*;

import java.awt.image.BufferedImage;


public class Geldsack{
	private int[] position;
	private int[] field;
	private boolean falling;
	private int fallHeight;
	private BufferedImage bild;
	private long liveTime = 700;

	public Geldsack(int[] fp, Skin sk) {
		position=null;
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

	public BufferedImage getImage() {
		return bild;
	}

	public void decRemainingTime(long delay_period) {
		liveTime -= delay_period;
	}

	public boolean outOfTime(){
		return liveTime>0?false:true;
	}
}