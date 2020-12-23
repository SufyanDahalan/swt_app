package Spielverlauf;

import org.json.JSONArray;

public abstract class Item {

	protected JSONArray field;
	protected int[] position;
	protected int wertung;

	public Item(JSONArray fp) {
		field = fp;
		position = null;
	}

	public int[] getPosition() {
		return position;
	}
	public JSONArray getField() {
		return field;
	}


}