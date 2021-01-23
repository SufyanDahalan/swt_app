package Spielverlauf;

import java.io.Serializable;

public class Diamant extends Item implements Serializable {

	private int wertung = 25;

	public Diamant(int[] fp) {
		super(fp);
	}

	@Override
	public int getValue() {
		return wertung;
	}
}