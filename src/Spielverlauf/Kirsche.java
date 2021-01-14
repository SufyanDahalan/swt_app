package Spielverlauf;

import java.awt.image.BufferedImage;

public class Kirsche extends Item {

	private int wertung = 1000;
	private long liveTime = 15000; //ms

	public Kirsche(int[] fp, Skin sk) {
		super(fp, sk.getImage("cherry"));
	}

	@Override
	public int getValue() {
		return wertung;
	}

	public void decRemainingTime(long delay_period) {
		liveTime -= delay_period;
	}

	public boolean outOfTime(){
		return liveTime>0?false:true;
	}
}