package Spielverlauf;

import java.io.Serializable;

public class Diamant extends Item implements Serializable {

	private int wertung = 25;

	public Diamant(int[] fp) {
		super(fp);
	}

	public Diamant(Diamant d){
		super(d.getField().clone());
	}

	@Override
	public int getValue() {
		return wertung;
	}
}