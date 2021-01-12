package Spielverlauf;

import java.awt.image.BufferedImage;

public class Kirsche extends Item {

	private int wertung = 1000;
	private boolean visible = false;

	public Kirsche(int[] fp, Skin sk) {
		super(fp, sk.getImage("cherry"));
	}

    public void setVisible(boolean v) {
		visible = v;
	}
	public boolean getVisible() {
		 return visible;
	}

	@Override
	public int getValue() {
		return wertung;
	}
}