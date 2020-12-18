package Spielverlauf;

import org.json.JSONArray;

public abstract class Item {

	protected JSONArray position;
	protected int wertung;

	public Item(JSONArray pos) {
		position = pos;
	}

	public JSONArray getPosition() {
		return position;
	}


}