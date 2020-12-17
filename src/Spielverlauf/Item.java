package Spielverlauf;

import org.json.JSONArray;

public abstract class Item {

	protected JSONArray position;
	protected int wertung;

	public Item(JSONArray pos) {
		position = pos;
	}

	public boolean sichtbar() {
		// TODO - implement Item.sichtbar
		throw new UnsupportedOperationException();
	}

}