package Spielverlauf;

import java.awt.image.BufferedImage;


public class Diamant extends Item {

	private int wertung = 25;

	public Diamant(int[] fp, Skin sk) {
		super(fp, sk.getImage("diamond"));
	}

	@Override
	public int getValue() {
		return wertung;
	}
}