package Spielverlauf;

import org.json.JSONArray;

public class Kirsche extends Item {

	private int wertung = 1000;
	private boolean visible = false;

	public Kirsche(JSONArray pos) {
		super(pos);
	}

	public void setVisible(boolean v) {
		visible = v;
	}
}