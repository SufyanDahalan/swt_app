package Spielverlauf;

import org.json.JSONArray;


public class Diamant extends Item {

	private int wertung = 25;

	public Diamant(int[] pos){
		super(pos);
	}

	@Override
	public int getValue() {
		return wertung;
	}
}