package Spielverlauf;
import org.json.JSONArray;
import Spielverlauf.*;

import java.awt.image.BufferedImage;
import java.io.Serializable;


public class Geldsack implements Serializable {
	private int[] position;
	private int[] field;
	private boolean falling;
	private int fallHeight;
	final private long liveTime = 1000;
	private long remLiveTime;
	private boolean shaking;

	public Geldsack(int[] fp){
		position=null;
		falling = false;
		shaking = false;
		fallHeight = 0;
		field=fp;
		remLiveTime = liveTime;
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

	public void setFalling(boolean f) {
		falling = f;
    }

	public boolean getFalling() {
		return falling;
	}

	public void incFallHeight(int fh) {
		fallHeight += fh;
	}

	public int getFallHeight() {
		return fallHeight;
	}

	public void resetFallHeight() {
		fallHeight = 0;
	}

	public void decRemainingTime(long delay_period) {
		remLiveTime -= delay_period;
	}

	public boolean outOfTime(){
		return remLiveTime>0?false:true;
	}

	public void resetLiveTime(){
		remLiveTime = liveTime;
	}

	public void setShaking(boolean s){
		shaking  = s;
	}

	public boolean getShaking() {
		return shaking;
	}
}